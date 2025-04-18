# app/services/tongue_analyzer.py
from io import BytesIO
from typing import Dict, Any, Tuple, Optional

import cv2
import numpy as np


class TongueAnalyzer:
    """舌象分析服务"""

    def __init__(self):
        """初始化舌象分析服务"""
        pass

    def analyze_image(self, image_data: BytesIO) -> Dict[str, Any]:
        """
        分析舌象图像

        Args:
            image_data: 图像二进制数据

        Returns:
            分析结果
        """
        # 加载图像
        image = self._load_image(image_data)

        # 定位舌头区域
        tongue_region = self._detect_tongue_region(image)

        # 如果成功检测到舌头区域，则只分析舌头部分
        if tongue_region is not None:
            processed_image = tongue_region
        else:
            # 如果未检测到舌头区域，使用整个图像进行分析
            processed_image = self._preprocess_image(image)
            print("警告: 未能精确定位舌头区域，使用整个图像进行分析")

        # 提取颜色特征
        color_features = self._extract_color_features(processed_image)

        # 确定舌质颜色
        tongue_color = self._determine_tongue_color(color_features)

        # 确定舌苔特征
        tongue_coating = self._determine_tongue_coating(color_features)

        # 检测裂纹和齿痕
        has_crack, has_tooth_mark = self._detect_cracks_and_marks(processed_image)

        # 估计湿润度（基于亮度）
        moisture = float(color_features["value_mean"] / 255.0)

        # 构建结果
        result = {
            "tongueColor": tongue_color,
            "tongueCoating": tongue_coating,
            "tongueShape": "正常",  # 简化处理
            "hasCrack": has_crack,
            "hasToothMark": has_tooth_mark,
            "moisture": moisture,
            "rawFeatures": color_features
        }

        return result

    def _load_image(self, image_data: BytesIO) -> np.ndarray:
        """加载图像数据"""
        image_array = np.frombuffer(image_data.getvalue(), np.uint8)
        image = cv2.imdecode(image_array, cv2.IMREAD_COLOR)
        return image

    def _preprocess_image(self, image: np.ndarray) -> np.ndarray:
        """预处理图像"""
        # 调整大小为统一尺寸
        resized = cv2.resize(image, (512, 512))
        return resized

    def _detect_tongue_region(self, image: np.ndarray) -> Optional[np.ndarray]:
        """
        检测并提取舌头区域

        参数:
            image: 原始图像

        返回:
            舌头区域图像，如果未检测到则返回None
        """
        # 转换为HSV颜色空间
        hsv_image = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)

        # 红色和粉色范围 - 舌头的典型颜色范围
        lower_red1 = np.array([0, 70, 50])
        upper_red1 = np.array([10, 255, 255])
        lower_red2 = np.array([160, 70, 50])
        upper_red2 = np.array([180, 255, 255])

        # 创建掩码
        mask1 = cv2.inRange(hsv_image, lower_red1, upper_red1)
        mask2 = cv2.inRange(hsv_image, lower_red2, upper_red2)
        mask = cv2.bitwise_or(mask1, mask2)

        # 进行形态学操作以改善掩码
        kernel = np.ones((5, 5), np.uint8)
        mask = cv2.morphologyEx(mask, cv2.MORPH_OPEN, kernel)
        mask = cv2.morphologyEx(mask, cv2.MORPH_CLOSE, kernel)

        # 查找轮廓
        contours, _ = cv2.findContours(mask, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

        # 如果找到轮廓，选择最大的一个
        if contours:
            # 按轮廓面积排序
            contours = sorted(contours, key=cv2.contourArea, reverse=True)
            largest_contour = contours[0]

            # 计算轮廓边界框
            x, y, w, h = cv2.boundingRect(largest_contour)

            # 判断是否为有效的舌头区域（根据面积和宽高比）
            area = w * h
            aspect_ratio = w / float(h)

            # 舌头通常宽度大于高度
            if area > 10000 and 0.5 < aspect_ratio < 2.5:
                # 扩大区域以确保包含整个舌头
                padding = 10
                x = max(0, x - padding)
                y = max(0, y - padding)
                w = min(image.shape[1] - x, w + 2 * padding)
                h = min(image.shape[0] - y, h + 2 * padding)

                # 提取舌头区域
                tongue_region = image[y:y + h, x:x + w]
                return self._preprocess_image(tongue_region)

        # 如果基于颜色的方法失败，尝试使用Haar级联分类器检测嘴部
        try:
            # 灰度转换
            gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

            # 使用Haar级联分类器检测人脸
            face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_frontalface_default.xml')
            faces = face_cascade.detectMultiScale(gray, 1.3, 5)

            if len(faces) > 0:
                # 检测到人脸，尝试查找嘴部区域
                # 使用OpenCV自带的嘴部分类器
                mouth_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_smile.xml')

                for (x, y, w, h) in faces:
                    # 在面部区域内搜索嘴部
                    roi_gray = gray[y:y + h, x:x + w]
                    mouth_rects = mouth_cascade.detectMultiScale(roi_gray, 1.7, 11)

                    if len(mouth_rects) > 0:
                        # 取第一个检测到的嘴部区域
                        mx, my, mw, mh = mouth_rects[0]
                        # 转换为原图坐标
                        mx += x
                        my += y

                        # 扩大区域以包含舌头
                        padding_x = int(mw * 0.2)
                        padding_y = int(mh * 0.8)  # 向下多扩展一些

                        mx = max(0, mx - padding_x)
                        mw = min(image.shape[1] - mx, mw + 2 * padding_x)
                        my = max(0, my - padding_y // 4)  # 向上少扩展一些
                        mh = min(image.shape[0] - my, mh + padding_y)

                        # 提取舌头可能的区域
                        tongue_region = image[my:my + mh, mx:mx + mw]
                        return self._preprocess_image(tongue_region)
        except Exception as e:
            print(f"使用级联分类器检测失败: {str(e)}")

        # 如果所有方法都失败，返回None
        return None

    def _extract_color_features(self, image: np.ndarray) -> Dict[str, Any]:
        """提取颜色特征"""
        # 转换为HSV颜色空间
        hsv_image = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)

        # 计算各通道均值和标准差
        h_mean, h_std = cv2.meanStdDev(hsv_image[:, :, 0])
        s_mean, s_std = cv2.meanStdDev(hsv_image[:, :, 1])
        v_mean, v_std = cv2.meanStdDev(hsv_image[:, :, 2])

        return {
            "hue_mean": float(h_mean[0][0]),
            "hue_std": float(h_std[0][0]),
            "saturation_mean": float(s_mean[0][0]),
            "saturation_std": float(s_std[0][0]),
            "value_mean": float(v_mean[0][0]),
            "value_std": float(v_std[0][0]),
        }

    def _detect_cracks_and_marks(self, image: np.ndarray) -> Tuple[bool, bool]:
        """
        检测舌头上的裂纹和齿痕
        """
        # 转换为灰度图
        gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

        # 增强对比度
        clahe = cv2.createCLAHE(clipLimit=2.0, tileGridSize=(8, 8))
        enhanced = clahe.apply(gray)

        # 边缘检测
        edges = cv2.Canny(enhanced, 50, 150)

        # 检测直线（裂纹通常呈现为直线）
        lines = cv2.HoughLinesP(edges, 1, np.pi / 180, threshold=50, minLineLength=30, maxLineGap=10)
        has_crack = bool(lines is not None and len(lines) > 0)  # 转换为Python原生布尔类型

        # 检测齿痕 - 通常在舌头边缘呈现为规则的凹陷
        h, w = edges.shape

        # 定义边缘区域
        border_width = int(w * 0.15)
        left_border = edges[:, :border_width]
        right_border = edges[:, w - border_width:]

        # 计算边缘密度
        left_density = np.sum(left_border > 0) / (left_border.shape[0] * left_border.shape[1])
        right_density = np.sum(right_border > 0) / (right_border.shape[0] * right_border.shape[1])

        # 如果边缘密度超过阈值，认为存在齿痕
        has_tooth_mark = bool(left_density > 0.1 or right_density > 0.1)  # 转换为Python原生布尔类型

        return has_crack, has_tooth_mark

    def _determine_tongue_color(self, color_features: Dict[str, Any]) -> str:
        """确定舌质颜色"""
        # 简化版规则判断
        hue_mean = color_features["hue_mean"]
        saturation_mean = color_features["saturation_mean"]
        value_mean = color_features["value_mean"]

        if value_mean < 100:
            return "紫舌"  # 紫舌
        elif hue_mean < 10 or hue_mean > 170:
            if saturation_mean > 150:
                return "绛舌"  # 绛舌
            else:
                return "红舌"  # 红舌
        elif 10 <= hue_mean <= 30:
            if value_mean < 150:
                return "红舌"  # 红舌
            else:
                return "淡红舌"  # 淡红舌
        else:
            return "淡白舌"  # 淡白舌

    def _determine_tongue_coating(self, color_features: Dict[str, Any]) -> str:
        """确定舌苔特征"""
        # 简化版规则判断
        saturation_mean = color_features["saturation_mean"]
        value_mean = color_features["value_mean"]

        if value_mean < 80:
            return "黑苔"  # 黑苔
        elif value_mean < 120:
            return "灰苔"  # 灰苔
        elif saturation_mean < 50:
            if value_mean > 180:
                return "薄白苔"  # 薄白苔
            else:
                return "厚白苔"  # 厚白苔
        elif 50 <= saturation_mean <= 100:
            if value_mean > 180:
                return "薄黄苔"  # 薄黄苔
            else:
                return "厚黄苔"  # 厚黄苔
        else:
            return "腻苔"  # 腻苔

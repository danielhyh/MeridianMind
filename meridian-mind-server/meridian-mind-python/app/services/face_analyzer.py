# app/services/face_analyzer.py
import cv2
import numpy as np
from io import BytesIO
from typing import Dict, Any, Tuple, List

from app.core.config import settings

class FaceAnalyzer:
    """面色分析服务"""

    def __init__(self):
        """初始化面色分析服务"""
        # 面部区域定义（简化版）
        self.face_regions = {
            "forehead": ((0.3, 0.0), (0.7, 0.3)),  # 前额区域
            "leftCheek": ((0.0, 0.3), (0.3, 0.7)),  # 左颊区域
            "rightCheek": ((0.7, 0.3), (1.0, 0.7)),  # 右颊区域
            "nose": ((0.3, 0.3), (0.7, 0.5)),  # 鼻部区域
            "mouth": ((0.3, 0.5), (0.7, 0.7)),  # 口部区域
            "chin": ((0.3, 0.7), (0.7, 1.0))  # 下巴区域
        }

        # 加载人脸检测器 (可选)
        self.face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_frontalface_default.xml')

    def analyze_image(self, image_data: BytesIO, image_url: str) -> Dict[str, Any]:
        """
        分析面色图像

        Args:
            image_data: 图像二进制数据
            image_url: 图像URL

        Returns:
            分析结果
        """
        # 加载图像
        image = self._load_image(image_data)

        # 预处理图像
        processed_image = self._preprocess_image(image)

        # 提取颜色特征
        color_features = self._extract_color_features(processed_image)

        # 提取各区域颜色特征
        region_features = self._extract_region_features(processed_image)

        # 确定整体面色
        face_color = self._determine_face_color(color_features)

        # 构建结果
        result = {
            "faceColor": face_color,
            "colorSaturation": float(color_features["saturation_mean"]),
            "colorBrightness": float(color_features["value_mean"]),
            "regionColors": region_features,
            "imageUrl": image_url,
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

    def _extract_region_features(self, image: np.ndarray) -> Dict[str, Any]:
        """提取各区域颜色特征"""
        h, w = image.shape[:2]
        region_features = {}

        for region_name, ((x1_rel, y1_rel), (x2_rel, y2_rel)) in self.face_regions.items():
            # 计算绝对坐标
            x1, y1 = int(x1_rel * w), int(y1_rel * h)
            x2, y2 = int(x2_rel * w), int(y2_rel * h)

            # 提取区域图像
            region_image = image[y1:y2, x1:x2]

            if region_image.size > 0:
                # 提取区域颜色特征
                region_color_features = self._extract_color_features(region_image)

                # 确定区域面色
                region_color = self._determine_face_color(region_color_features)

                region_features[region_name] = {
                    "color": region_color,
                    "saturation": float(region_color_features["saturation_mean"]),
                    "brightness": float(region_color_features["value_mean"])
                }

        return region_features

    def _determine_face_color(self, color_features: Dict[str, Any]) -> str:
        """确定面色"""
        # 简化版规则判断
        hue_mean = color_features["hue_mean"]
        saturation_mean = color_features["saturation_mean"]
        value_mean = color_features["value_mean"]

        if value_mean < 100:
            if hue_mean < 30 or hue_mean > 330:
                return "暗红"
            else:
                return "暗黑"
        elif saturation_mean < 50:
            if value_mean < 180:
                return "灰白"
            else:
                return "苍白"
        elif 0 <= hue_mean <= 30 or 330 <= hue_mean <= 360:
            if saturation_mean > 150:
                return "鲜红"
            else:
                return "淡红"
        elif 30 < hue_mean <= 60:
            return "黄色"
        elif 60 < hue_mean <= 150:
            return "青色"
        elif 150 < hue_mean <= 270:
            return "青紫"
        else:
            return "淡紫"
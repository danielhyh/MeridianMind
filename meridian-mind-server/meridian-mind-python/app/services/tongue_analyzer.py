# app/services/tongue_analyzer.py
import cv2
import numpy as np
from io import BytesIO
from typing import Dict, Any, Tuple

from app.core.config import settings

class TongueAnalyzer:
    """舌象分析服务"""

    def __init__(self):
        """初始化舌象分析服务"""
        pass

    def analyze_image(self, image_data: BytesIO, image_url: str) -> Dict[str, Any]:
        """
        分析舌象图像

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

        # 确定舌质颜色
        tongue_color = self._determine_tongue_color(color_features)

        # 确定舌苔特征
        tongue_coating = self._determine_tongue_coating(color_features)

        # 检测裂纹和齿痕 (简化版)
        has_crack = False
        has_tooth_mark = False

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
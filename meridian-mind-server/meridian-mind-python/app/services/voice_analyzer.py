# app/services/voice_analyzer.py
import os
import librosa
import numpy as np
from typing import Dict, Any, Tuple

from app.core.config import settings

class VoiceAnalyzer:
    """语音分析服务"""

    def __init__(self):
        """初始化语音分析服务"""
        pass

    def analyze_audio(self, file_path: str, audio_url: str) -> Dict[str, Any]:
        """
        分析语音音频

        Args:
            file_path: 音频文件路径
            audio_url: 音频URL

        Returns:
            分析结果
        """
        # 加载音频
        audio, sr = librosa.load(file_path, sr=22050)

        # 提取音频特征
        features = self._extract_audio_features(audio, sr)

        # 简化版：基于音频特征的简单规则进行语音特性判断
        strength = "中等"
        if features["rms_mean"] > 0.1:
            strength = "强"
        elif features["rms_mean"] < 0.05:
            strength = "弱"

        tone = "中等"
        if features["spectral_centroid_mean"] > 1000:
            tone = "尖细"
        elif features["spectral_centroid_mean"] < 500:
            tone = "低沉"

        rhythm = "均匀"
        if features["zcr_std"] > 0.1:
            rhythm = "不均"

        breath_pattern = "正常"

        # 构建结果
        result = {
            "strength": strength,
            "tone": tone,
            "rhythm": rhythm,
            "breathPattern": breath_pattern,
            "audioUrl": audio_url,
            "rawFeatures": features
        }

        return result

    def _extract_audio_features(self, audio: np.ndarray, sr: int) -> Dict[str, Any]:
        """提取音频特征"""
        # 提取时域特征
        # RMS能量
        rms = librosa.feature.rms(y=audio)[0]
        rms_mean = np.mean(rms)
        rms_std = np.std(rms)

        # 过零率
        zcr = librosa.feature.zero_crossing_rate(audio)[0]
        zcr_mean = np.mean(zcr)
        zcr_std = np.std(zcr)

        # 谱质心
        spectral_centroid = librosa.feature.spectral_centroid(y=audio, sr=sr)[0]
        spectral_centroid_mean = np.mean(spectral_centroid)

        # 谱带宽
        spectral_bandwidth = librosa.feature.spectral_bandwidth(y=audio, sr=sr)[0]
        spectral_bandwidth_mean = np.mean(spectral_bandwidth)

        return {
            "rms_mean": float(rms_mean),
            "rms_std": float(rms_std),
            "zcr_mean": float(zcr_mean),
            "zcr_std": float(zcr_std),
            "spectral_centroid_mean": float(spectral_centroid_mean),
            "spectral_bandwidth_mean": float(spectral_bandwidth_mean),
        }
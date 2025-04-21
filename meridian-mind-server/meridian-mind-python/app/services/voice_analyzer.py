# 修改 app/services/voice_analyzer.py 文件

from typing import Dict, Any, Tuple

import librosa
import numpy as np


class VoiceAnalyzer:
    """语音分析服务"""

    def __init__(self):
        """初始化语音分析服务"""
        pass

    # app/services/voice_analyzer.py 修改

    # app/services/voice_analyzer.py 修改示例

    def analyze_audio_temp(self, temp_file_path: str) -> Dict[str, Any]:
        """
        分析临时音频文件，只返回原始特征参数
        """
        try:
            # 加载音频
            audio, sr = librosa.load(temp_file_path, sr=22050)

            # 提取音频特征
            rms = librosa.feature.rms(y=audio)[0]
            rms_mean = float(np.mean(rms))
            rms_std = float(np.std(rms))

            # 过零率
            zcr = librosa.feature.zero_crossing_rate(audio)[0]
            zcr_mean = float(np.mean(zcr))
            zcr_std = float(np.std(zcr))

            # 谱质心
            spectral_centroid = librosa.feature.spectral_centroid(y=audio, sr=sr)[0]
            spectral_centroid_mean = float(np.mean(spectral_centroid))

            # 谱带宽
            spectral_bandwidth = librosa.feature.spectral_bandwidth(y=audio, sr=sr)[0]
            spectral_bandwidth_mean = float(np.mean(spectral_bandwidth))

            # 梅尔频率倒谱系数(MFCC)
            mfccs = librosa.feature.mfcc(y=audio, sr=sr, n_mfcc=13)
            mfcc_means = np.mean(mfccs, axis=1).tolist()

            # 构建结果 - 只返回原始参数，不做判断
            result = {
                "rms_mean": rms_mean,
                "rms_std": rms_std,
                "zcr_mean": zcr_mean,
                "zcr_std": zcr_std,
                "spectral_centroid_mean": spectral_centroid_mean,
                "spectral_bandwidth_mean": spectral_bandwidth_mean,
                "mfcc_means": mfcc_means
            }

            return result

        except Exception as e:
            print(f"音频分析异常: {str(e)}")
            # 返回默认结果而不是抛出异常
            return {
                "rms_mean": 0.0,
                "rms_std": 0.0,
                "zcr_mean": 0.0,
                "zcr_std": 0.0,
                "spectral_centroid_mean": 0.0,
                "spectral_bandwidth_mean": 0.0,
                "mfcc_means": [0.0] * 13,
                "error": str(e)
            }

    def analyze_audio(self, file_path: str, audio_url: str) -> Dict[str, Any]:
        """
        分析语音音频
        """
        print(f"开始分析音频: {file_path}")

        try:
            # 只处理前10秒的音频以加快处理速度
            audio, sr = self._load_audio_safe(file_path, max_duration=10)

            print(f"音频加载成功: {file_path}, 采样率: {sr}, 时长: {len(audio) / sr:.2f}秒")

            # 提取音频特征
            features = self._extract_audio_features(audio, sr)
            print(f"音频特征提取成功")

            # 基于音频特征的语音特性判断
            strength = self._determine_strength(features)
            tone = self._determine_tone(features)
            rhythm = self._determine_rhythm(features)
            breath_pattern = self._determine_breath_pattern(features)

            # 构建结果
            result = {
                "strength": strength,
                "tone": tone,
                "rhythm": rhythm,
                "breathPattern": breath_pattern,
                "audioUrl": audio_url,
                "rawFeatures": features
            }

            print(f"音频分析完成: {file_path}")
            return result
        except Exception as e:
            print(f"音频分析异常: {str(e)}")
            # 返回默认结果而不是抛出异常
            return {
                "strength": "中等",
                "tone": "中等",
                "rhythm": "均匀",
                "breathPattern": "正常",
                "audioUrl": audio_url,
                "rawFeatures": {
                    "rms_mean": 0.0,
                    "rms_std": 0.0,
                    "zcr_mean": 0.0,
                    "zcr_std": 0.0,
                    "spectral_centroid_mean": 0.0,
                    "spectral_bandwidth_mean": 0.0,
                    "mfcc_means": [0.0] * 13
                },
                "error": str(e)
            }

    def _load_audio_safe(self, file_path: str, max_duration: int = None) -> Tuple[np.ndarray, int]:
        """
        安全加载音频，支持多种格式

        Args:
            file_path: 音频文件路径
            max_duration: 最大持续时间（秒），None表示全部加载
        """
        try:
            # 方法1: 直接使用librosa加载，限制音频长度
            if max_duration:
                print(f"尝试加载前 {max_duration} 秒的音频...")
                audio, sr = librosa.load(file_path, sr=22050, duration=max_duration)
            else:
                audio, sr = librosa.load(file_path, sr=22050)
            print(f"成功使用librosa直接加载音频: {file_path}")
            return audio, sr
        except Exception as e:
            print(f"librosa直接加载失败: {str(e)}，尝试其他方法")

    # 其他加载方法...
    # (保留您现有的其他加载方法，但修改为也支持 max_duration 参数)

    def _convert_to_wav(self, input_path: str, output_path: str) -> None:
        """
        将音频文件转换为WAV格式

        Args:
            input_path: 输入文件路径
            output_path: 输出文件路径
        """
        # 这里使用ffmpeg进行转换
        import subprocess

        try:
            # 使用ffmpeg转换
            subprocess.run([
                'ffmpeg', '-i', input_path,
                '-ar', '22050', '-ac', '1',  # 22050Hz采样率，单声道
                output_path
            ], check=True, capture_output=True)
        except subprocess.CalledProcessError as e:
            raise Exception(f"ffmpeg转换失败: {e.stderr.decode()}")
        except FileNotFoundError:
            raise Exception("ffmpeg未安装，请安装ffmpeg")

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

        # 梅尔频率倒谱系数(MFCC)
        mfccs = librosa.feature.mfcc(y=audio, sr=sr, n_mfcc=13)
        mfcc_means = np.mean(mfccs, axis=1).tolist()

        return {
            "rms_mean": float(rms_mean),
            "rms_std": float(rms_std),
            "zcr_mean": float(zcr_mean),
            "zcr_std": float(zcr_std),
            "spectral_centroid_mean": float(spectral_centroid_mean),
            "spectral_bandwidth_mean": float(spectral_bandwidth_mean),
            "mfcc_means": mfcc_means
        }

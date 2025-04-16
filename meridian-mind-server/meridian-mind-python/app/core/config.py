# app/core/config.py
import os
from pathlib import Path

from pydantic import BaseSettings

# 获取项目根目录
ROOT_DIR = Path(__file__).resolve().parent.parent.parent

class Settings(BaseSettings):
    # API配置
    PORT: int = 8000
    HOST: str = "0.0.0.0"
    DEBUG: bool = True

    # 文件配置
    UPLOAD_DIR: str = str(ROOT_DIR / "uploads")
    MAX_UPLOAD_SIZE: int = 10 * 1024 * 1024  # 10MB

    class Config:
        env_file = str(ROOT_DIR / ".env")
        env_file_encoding = "utf-8"

# 单例模式加载配置 - 这行很关键，需要定义settings变量
settings = Settings()

# 确保上传目录存在
os.makedirs(settings.UPLOAD_DIR, exist_ok=True)
# app/api/endpoints/voice.py
import io
import os
import uuid
from typing import Dict, Any

from fastapi import APIRouter, UploadFile, File, HTTPException
from fastapi.responses import JSONResponse

from app.core.config import settings
from app.services.voice_analyzer import VoiceAnalyzer

router = APIRouter()
voice_analyzer = VoiceAnalyzer()

@router.post("/analyze")
async def analyze_voice_audio(file: UploadFile = File(...)):
    """
    分析语音音频并返回特征数据

    Args:
        file: 上传的语音音频文件

    Returns:
        语音分析结果
    """
    try:
        # 检查文件类型
        allowed_audio_types = ["audio/wav", "audio/mpeg", "audio/mp3", "audio/ogg"]
        if file.content_type not in allowed_audio_types:
            return JSONResponse(
                status_code=400,
                content={"success": False, "message": "请上传音频文件"}
            )

        # 读取上传的音频
        contents = await file.read()

        # 生成唯一文件名
        ext = os.path.splitext(file.filename)[1]
        filename = f"{uuid.uuid4().hex}{ext}"
        file_path = os.path.join(settings.UPLOAD_DIR, filename)

        # 保存音频
        with open(file_path, "wb") as f:
            f.write(contents)

        # 相对URL路径
        file_url = f"/uploads/{filename}"

        # 分析音频
        features = voice_analyzer.analyze_audio(file_path, file_url)

        return {"success": True, "data": features}
    except Exception as e:
        return JSONResponse(
            status_code=500,
            content={"success": False, "message": f"语音分析失败: {str(e)}"}
        )
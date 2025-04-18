# 修改 app/api/endpoints/voice.py 文件

import os
import uuid

from fastapi import APIRouter, UploadFile, File
from fastapi.responses import JSONResponse

from app.core.config import settings
from app.services.voice_analyzer import VoiceAnalyzer

router = APIRouter()
voice_analyzer = VoiceAnalyzer()


@router.post("/analyze")
async def analyze_voice_audio(file: UploadFile = File(...)):
    """
    分析语音音频并返回特征数据
    """
    try:
        # 设置处理超时时间
        timeout = 30  # 30秒超时

        # 读取上传的音频
        contents = await file.read()

        # 避免处理过大的文件
        if len(contents) > 10 * 1024 * 1024:  # 10MB
            return JSONResponse(
                status_code=400,
                content={"success": False, "message": "音频文件过大，请上传小于10MB的文件"}
            )

        # 生成唯一文件名
        file_ext = os.path.splitext(file.filename)[1].lower()
        ext = file_ext if file_ext else ".wav"
        filename = f"{uuid.uuid4().hex}{ext}"
        file_path = os.path.join(settings.UPLOAD_DIR, filename)

        # 保存音频
        with open(file_path, "wb") as f:
            f.write(contents)

        # 相对URL路径
        file_url = f"/uploads/{filename}"

        # 使用asyncio创建超时任务
        import asyncio
        from concurrent.futures import ThreadPoolExecutor

        # 创建线程池执行器
        executor = ThreadPoolExecutor(max_workers=1)

        # 在线程池中运行音频分析
        loop = asyncio.get_event_loop()
        try:
            # 使用超时运行音频分析
            features = await asyncio.wait_for(
                loop.run_in_executor(
                    executor,
                    voice_analyzer.analyze_audio,
                    file_path,
                    file_url
                ),
                timeout=timeout
            )
        except asyncio.TimeoutError:
            print(f"音频分析超时: {file_path}")
            # 返回默认响应而不是错误
            features = {
                "strength": "中等",
                "tone": "中等",
                "rhythm": "均匀",
                "breathPattern": "正常",
                "audioUrl": file_url,
                "error": "音频分析超时"
            }

        return {"success": True, "data": features}
    except Exception as e:
        print(f"语音分析异常: {str(e)}")
        return JSONResponse(
            status_code=500,
            content={"success": False, "message": f"语音分析失败: {str(e)}"}
        )

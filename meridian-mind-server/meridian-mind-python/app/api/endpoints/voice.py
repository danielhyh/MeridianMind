# app/api/endpoints/voice.py
import asyncio
from concurrent.futures import ThreadPoolExecutor

from fastapi import APIRouter, UploadFile, File
from fastapi.responses import JSONResponse

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

        # 创建内存中的临时文件以供分析
        import tempfile
        import os

        # 获取文件扩展名
        file_ext = os.path.splitext(file.filename)[1].lower() if file.filename else ".wav"

        # 创建临时文件用于分析
        with tempfile.NamedTemporaryFile(suffix=file_ext, delete=False) as temp_file:
            temp_file.write(contents)
            temp_path = temp_file.name

        try:
            # 创建线程池执行器
            executor = ThreadPoolExecutor(max_workers=1)

            # 在线程池中运行音频分析
            loop = asyncio.get_event_loop()

            # 使用超时运行音频分析
            features = await asyncio.wait_for(
                loop.run_in_executor(
                    executor,
                    voice_analyzer.analyze_audio_temp,
                    temp_path
                ),
                timeout=timeout
            )
        finally:
            # 无论成功失败，都删除临时文件
            if os.path.exists(temp_path):
                os.remove(temp_path)

        return {"success": True, "data": features}
    except asyncio.TimeoutError:
        return JSONResponse(
            status_code=500,
            content={"success": False, "message": "音频分析超时"}
        )
    except Exception as e:
        print(f"语音分析异常: {str(e)}")
        return JSONResponse(
            status_code=500,
            content={"success": False, "message": f"语音分析失败: {str(e)}"}
        )

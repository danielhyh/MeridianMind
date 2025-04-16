# app/api/endpoints/face.py
import io
import os
import uuid
from typing import Dict, Any

from fastapi import APIRouter, UploadFile, File, HTTPException
from fastapi.responses import JSONResponse

from app.core.config import settings
from app.services.face_analyzer import FaceAnalyzer

router = APIRouter()
face_analyzer = FaceAnalyzer()

@router.post("/analyze")
async def analyze_face_image(file: UploadFile = File(...)):
    """
    分析面色图像并返回特征数据

    Args:
        file: 上传的面色图像文件

    Returns:
        面色分析结果
    """
    try:
        if not file.content_type.startswith("image/"):
            return JSONResponse(
                status_code=400,
                content={"success": False, "message": "请上传图像文件"}
            )

        # 读取上传的图像
        contents = await file.read()

        # 生成唯一文件名
        ext = os.path.splitext(file.filename)[1]
        filename = f"{uuid.uuid4().hex}{ext}"
        file_path = os.path.join(settings.UPLOAD_DIR, filename)

        # 保存图像
        with open(file_path, "wb") as f:
            f.write(contents)

        # 相对URL路径
        file_url = f"/uploads/{filename}"

        # 分析图像
        features = face_analyzer.analyze_image(io.BytesIO(contents), file_url)

        return {"success": True, "data": features}
    except Exception as e:
        return JSONResponse(
            status_code=500,
            content={"success": False, "message": f"面色分析失败: {str(e)}"}
        )
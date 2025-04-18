# app/api/endpoints/face.py
import io

from fastapi import APIRouter, UploadFile, File
from fastapi.responses import JSONResponse

from app.services.face_analyzer import FaceAnalyzer

router = APIRouter()
face_analyzer = FaceAnalyzer()


@router.post("/analyze")
async def analyze_face_image(file: UploadFile = File(...)):
    """
    分析面色图像并返回特征数据
    """
    try:
        if not file.content_type.startswith("image/"):
            return JSONResponse(
                status_code=400,
                content={"success": False, "message": "请上传图像文件"}
            )

        # 读取上传的图像
        contents = await file.read()

        # 直接分析图像,不保存文件
        features = face_analyzer.analyze_image(io.BytesIO(contents))

        return {"success": True, "data": features}
    except Exception as e:
        return JSONResponse(
            status_code=500,
            content={"success": False, "message": f"面色分析失败: {str(e)}"}
        )

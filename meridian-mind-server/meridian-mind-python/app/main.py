# app/main.py
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles

# 确保这些导入是正确的
from app.api.endpoints import tongue, face, voice
from app.core.config import settings

app = FastAPI(
    title="经络心智中医四诊数字化处理服务",
    description="提供舌象分析、面色分析、语音分析服务",
    version="1.0.0",
    docs_url="/api/docs",
    redoc_url="/api/redoc",
    openapi_url="/api/openapi.json"
)

# 添加CORS中间件
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # 设置允许跨域的来源
    allow_credentials=True,
    allow_methods=["*"],  # 设置允许跨域的HTTP方法
    allow_headers=["*"],  # 设置允许的HTTP请求头
)

# 挂载API路由
app.include_router(tongue.router, prefix="/api/tongue", tags=["舌象分析"])
app.include_router(face.router, prefix="/api/face", tags=["面色分析"])
app.include_router(voice.router, prefix="/api/voice", tags=["语音分析"])

# 创建上传目录并挂载静态文件服务
app.mount("/uploads", StaticFiles(directory="uploads"), name="uploads")

@app.get("/")
def read_root():
    return {"message": "经络心智中医四诊数字化处理服务正在运行"}
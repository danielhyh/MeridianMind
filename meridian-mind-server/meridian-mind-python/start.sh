#!/bin/bash
# start.sh

# 启动FastAPI服务
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
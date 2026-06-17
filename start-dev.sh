#!/bin/bash
# ChefMind Cloud 本地开发启动脚本

set -e

echo "=== 启动 ChefMind Cloud 开发环境 ==="

# 1. 启动基础设施
echo "[1/4] 启动基础设施 (PostgreSQL + Redis + Nacos)..."
docker compose up -d postgres redis nacos
sleep 5

# 2. 编译后端
echo "[2/4] 编译后端..."
cd "$(dirname "$0")"
mvn install -DskipTests -q

# 3. 启动微服务（后台）
echo "[3/4] 启动微服务..."
SERVICES_DIR=$(pwd)

start_service() {
    local dir=$1
    local log="/tmp/${dir}.log"
    cd "${SERVICES_DIR}/${dir}"
    nohup mvn spring-boot:run > "${log}" 2>&1 &
    echo "  🚀 ${dir} started (PID: $!) - log: ${log}"
    cd "${SERVICES_DIR}"
}

start_service "gateway"
sleep 3
start_service "user-service"
start_service "recipe-service"
start_service "meal-plan-service"
start_service "recommendation-service"
start_service "admin-service"

# 4. 启动前端
echo "[4/4] 启动前端..."
cd "${SERVICES_DIR}/admin-web"
npm run dev &
echo "  🚀 Frontend started - http://localhost:5173"

cd "${SERVICES_DIR}"

echo ""
echo "=== 开发环境启动完成 ==="
echo "前端:          http://localhost:5173"
echo "API 网关:      http://localhost:8080"
echo "Nacos 控制台:  http://localhost:8848/nacos"
echo ""
echo "查看日志: tail -f /tmp/{service-name}.log"
echo "停止服务: kill \$(jobs -p)"

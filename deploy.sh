#!/bin/bash
# ChefMind Cloud 一键部署脚本

set -e

echo "=== ChefMind Cloud Deploy ==="

# 1. 检查环境
echo "[1/4] 检查环境..."
command -v docker >/dev/null 2>&1 || { echo "需要安装 Docker"; exit 1; }
command -v docker compose >/dev/null 2>&1 || { echo "需要安装 Docker Compose"; exit 1; }

# 2. 编译
echo "[2/4] 全量编译..."
./mvnw install -DskipTests -q

# 3. 构建并启动
echo "[3/4] Docker 构建并启动..."
export DEEPSEEK_API_KEY="${DEEPSEEK_API_KEY:-your-api-key}"
docker compose build --parallel
docker compose up -d

# 4. 健康检查
echo "[4/4] 健康检查..."
sleep 10

SERVICES=("gateway:8080" "user-service:8081" "recipe-service:8082" "meal-plan-service:8083" "recommendation-service:8084" "admin-service:8085")
for svc in "${SERVICES[@]}"; do
    name="${svc%%:*}"
    port="${svc##*:}"
    if curl -s -o /dev/null -w "%{http_code}" "http://localhost:${port}/api/${name}/health" 2>/dev/null | grep -q "200\|401\|404"; then
        echo "  ✅ ${name}:${port} 运行中"
    else
        echo "  ⚠️  ${name}:${port} 可能未就绪"
    fi
done

echo ""
echo "=== 部署完成 ==="
echo "前端: http://localhost"
echo "管理后台: http://localhost/admin"
echo "Nacos 控制台: http://localhost:8848/nacos"
echo "API 网关: http://localhost:8080"

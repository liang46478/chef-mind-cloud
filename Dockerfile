# 多阶段构建 - Java 微服务
FROM eclipse-temurin:17-jre-alpine AS builder

WORKDIR /app

# 复制 Maven Wrapper 和源码
COPY .mvn .mvn
COPY mvnw pom.xml ./
COPY common common
COPY gateway gateway
COPY user-service user-service
COPY recipe-service recipe-service
COPY meal-plan-service meal-plan-service
COPY recommendation-service recommendation-service
COPY admin-service admin-service

# 构建（跳过测试）
RUN ./mvnw install -DskipTests -q

# 运行阶段
FROM eclipse-temurin:17-jre-alpine

ARG SERVICE_NAME
ARG SERVICE_PORT=8080

WORKDIR /app
COPY --from=builder /app/${SERVICE_NAME}/target/${SERVICE_NAME}-*.jar ./app.jar

EXPOSE ${SERVICE_PORT}

ENTRYPOINT ["sh", "-c", "java -jar /app/app.jar"]

# 使用OpenJDK 21作為基礎映像
FROM openjdk:21-jre-slim

# 設置工作目錄
WORKDIR /app

# 複製Maven構建的JAR文件
COPY test-library-demo/target/test-library-demo-1.0.0-SNAPSHOT.jar app.jar

# 創建非root用戶
RUN groupadd -r appgroup && useradd -r -g appgroup appuser
RUN chown -R appuser:appgroup /app
USER appuser

# 暴露端口
EXPOSE 8080

# 設置JVM參數和應用程式參數
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseContainerSupport"

# 健康檢查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 啟動應用程式
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
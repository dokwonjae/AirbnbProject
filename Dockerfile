# ====== BUILD STAGE ======
FROM gradle:8.5-jdk17 AS build
WORKDIR /workspace
COPY . .
RUN gradle clean bootJar -x test

# ====== RUNTIME STAGE ======
FROM eclipse-temurin:17-jre
ENV TZ=Asia/Seoul \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Dfile.encoding=UTF-8"

# healthcheck에서 curl 사용
RUN apt-get update && apt-get install -y --no-install-recommends curl tzdata && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar /app/app.jar

EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --retries=5 \
  CMD curl -fsS http://localhost:8080/actuator/health || exit 1

# 보안상 non-root 권장
RUN useradd -ms /bin/bash spring
USER spring

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]

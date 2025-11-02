# ====== BUILD STAGE ======
FROM gradle:8.5-jdk17 AS build
WORKDIR /workspace
COPY . .
# ✅ WAR 빌드 (bootWar)
RUN gradle clean bootWar -x test

# ====== RUNTIME STAGE ======
FROM eclipse-temurin:17-jre

# ✅ 환경변수 & JVM 설정
ENV TZ=Asia/Seoul \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Dfile.encoding=UTF-8"

# ✅ curl 설치 (헬스체크용)
RUN apt-get update && apt-get install -y --no-install-recommends curl tzdata && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# ✅ WAR 파일 복사
COPY --from=build /workspace/build/libs/*.war /app/app.war

EXPOSE 8080

# ✅ Health Check (Spring Boot Actuator 기반)
HEALTHCHECK --interval=30s --timeout=5s --retries=5 \
  CMD curl -fsS http://localhost:8080/actuator/health || exit 1

# ✅ 보안상 non-root 사용자 권장
RUN useradd -ms /bin/bash spring
USER spring

# ✅ 실행 명령 (WAR 파일로 실행)
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.war"]

# ===== Builder =====
FROM gradle:8.10.2-jdk-21 AS builder
WORKDIR /app

COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
RUN ./gradlew --no-daemon dependencies || true

COPY src ./src
RUN ./gradlew --no-daemon bootJar -x test

# ===== Runtime =====
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080

# Se tiver actuator/health, pode ativar um HEALTHCHECK
# HEALTHCHECK --interval=30s --timeout=3s --start-period=30s CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java","-jar","/app/app.jar"]

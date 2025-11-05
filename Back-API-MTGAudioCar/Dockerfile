# ===== Builder =====
FROM gradle:8.10.2-jdk21 AS builder
WORKDIR /app

COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# ✅ Corrige permissão e possíveis CRLF do Windows
RUN chmod +x gradlew && sed -i 's/\r$//' gradlew

# Opcional: também normalize os scripts do wrapper, se existirem
RUN find gradle -type f -name "*.bat" -prune -o -type f -exec sed -i 's/\r$//' {} \;

# Puxa deps (cache)
RUN ./gradlew --no-daemon dependencies || true

# Código
COPY src ./src

# Build
RUN ./gradlew --no-daemon bootJar -x test

# ===== Runtime =====
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]

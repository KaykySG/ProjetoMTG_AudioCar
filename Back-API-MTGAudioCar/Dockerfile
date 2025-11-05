# ===== Builder =====
FROM gradle:8.10.2-jdk21 AS builder
WORKDIR /app

# Copia arquivos de config primeiro (cache)
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Baixa dependências (aproveita cache mesmo sem código)
RUN ./gradlew --no-daemon dependencies || true

# Copia o código
COPY src ./src

# Gera o JAR
RUN ./gradlew --no-daemon bootJar -x test

# ===== Runtime =====
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]

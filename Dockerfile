# =============================================================================
# Dockerfile for NextCar Backend (Spring Boot 4.0.6 / Java 26)
# =============================================================================
FROM eclipse-temurin:26-jdk-alpine AS builder

WORKDIR /app
COPY mvnw pom.xml ./
COPY .mvn .mvn
COPY src src

RUN chmod +x mvnw && ./mvnw clean package -DskipTests -q

# ── Runtime stage ─────────────────────────────────────────────────────────────
FROM eclipse-temurin:26-jre-alpine

RUN addgroup -S nextcar && adduser -S nextcar -G nextcar
USER nextcar

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8091

ENTRYPOINT ["java", "-jar", "app.jar"]
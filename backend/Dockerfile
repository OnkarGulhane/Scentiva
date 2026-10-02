# ==============================================================================
# SCENTIVA Backend — Multi-Stage Production Dockerfile (Spring Boot 3.3.4 & Java 21)
# Optimized for Render Web Services, Cloud Run, AWS ECS, and Docker Compose
# ==============================================================================

# ------------------------------------------------------------------------------
# Stage 1: Build & Package
# ------------------------------------------------------------------------------
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Copy Maven descriptor and cache dependencies offline
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy application source code
COPY src ./src

# Package production executable JAR without running tests during image build
RUN mvn clean package -DskipTests -B

# ------------------------------------------------------------------------------
# Stage 2: Minimalist Lightweight Runtime (Temurin JRE 21 Alpine)
# ------------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine AS runner

# Install curl for health checking and tzdata for timezone configuration
RUN apk add --no-cache curl tzdata

# Configure standard timezone
ENV TZ=Asia/Kolkata

# Create dedicated non-root application user and group for security compliance
RUN addgroup -S scentiva && adduser -S scentiva -G scentiva

WORKDIR /app

# Copy the built standalone fat JAR from builder
COPY --from=builder /build/target/*.jar app.jar

# Enforce secure file permissions
RUN chown -R scentiva:scentiva /app

# Switch to unprivileged non-root user
USER scentiva:scentiva

# JVM Container Memory & Garbage Collection Optimization Flags
# Ensures zero out-of-memory crashes on Render 512MB/1GB container tiers
ENV SPRING_PROFILES_ACTIVE=prod \
    PORT=8080 \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=40.0 -XX:+ExitOnOutOfMemoryError"

# Expose backend port
EXPOSE 8080

# Health check using Spring Boot Public Health Endpoint
HEALTHCHECK --interval=30s --timeout=5s --start-period=35s --retries=3 \
  CMD curl -f http://localhost:${PORT:-8080}/api/v1/health || exit 1

# Launch Spring Boot Application
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]

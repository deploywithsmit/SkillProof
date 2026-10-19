# ==========================================
# SkillProof Multi-Stage Production Dockerfile
# ==========================================

# 1. Build Stage
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /workspace

# Cache maven dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build runnable artifact
COPY src ./src
RUN mvn clean package -DskipTests -B

# 2. Runtime Stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Create unprivileged system user for application security
RUN groupadd -g 1001 skillproof && \
    useradd -u 1001 -g skillproof -s /bin/bash -m skillproof

# Create uploads storage directory with proper permissions
RUN mkdir -p /app/uploads && chown -R skillproof:skillproof /app

# Copy packaged JAR from builder
COPY --from=builder --chown=skillproof:skillproof /workspace/target/*.jar app.jar

USER skillproof

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"
ENV SPRING_PROFILES_ACTIVE="prod"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

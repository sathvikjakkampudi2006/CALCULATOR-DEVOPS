# ---------- Stage 1: Build ----------
    FROM maven:3.9.9-eclipse-temurin-17 AS builder

    WORKDIR /build
    
    # Copy Maven descriptor first for better layer caching
    COPY pom.xml .
    
    # Download dependencies
    RUN mvn dependency:go-offline
    
    # Copy application source
    COPY src ./src
    
    # Build the executable JAR
    RUN mvn clean package
    
    
    # ---------- Stage 2: Runtime ----------
    FROM eclipse-temurin:17-jre

    RUN apt-get update \
        && apt-get install -y --no-install-recommends curl \
        && rm -rf /var/lib/apt/lists/*
    
    WORKDIR /app
    
    # Create a non-root user
    RUN useradd --system --create-home --uid 1001 appuser
    
    # Copy only the built JAR from the builder stage
    COPY --from=builder /build/target/calculator-devops-1.0.0.jar app.jar
    
    # Run the application as non-root
    USER appuser
    
    # Calculator application port
    EXPOSE 8081
    
    ENTRYPOINT ["java", "-jar", "app.jar"]
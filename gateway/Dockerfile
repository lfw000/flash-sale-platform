# STAGE 1: BUILD THE APPLICATION
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# COPY THE PARENT POM AND THE SPECIFIC MODULE POM (COPY THE WHOLE PROJECT TO RESOLVE
# PARENT/CHILD DEPENDENCIES
COPY pom.xml .
COPY common/pom.xml common/
COPY gateway/pom.xml gateway/
COPY auth-service/pom.xml auth-service/
COPY booking-service/pom.xml booking-service/
COPY notification-service/pom.xml notification-service/

# DOWNLOAD DEPENDENCIES (CACHED LAYER)
RUN mvn dependency:go-offline -B

# COPY SOURCE CODE
COPY . .

# COMPILE AND PACKAGE ONLY THE SPECIFIC MODULE
RUN mvn clean package -pl gateway -am -DskipTests -B

# STAGE 2: CREATE THE MINIMAL RUNTIME IMAGE

FROM eclipse-temurin:21-jre-alpine

# INSTALL CURL FOR HEALTH CHECKS
RUN apk add --no-cache curl

# CREATE A NON-ROOT USER FOR SECURITY
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# COPY THE BUILT JAR FROM THE BUILDER STAGE
COPY --from=builder /app/gateway/target/*.jar app.jar

# CREATE A DIRECTORY FOR LOGS AND ASSIGN OWNERSHIP
RUN mkdir -p /var/log/flashsale && chown -R appuser:appgroup /var/log/flashsale

# SWITCH TO NON-ROOT USER
USER appuser

# EXPOSE THE DEFAULT PORT (WILL BE OVERRIDEN BY DOCKER-COMPOSE OR ENV VARS)
EXPOSE 8080

# HEALTH CHECK
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM OPTIONS CAN BE OVERRIDDEN AT RUNTIME
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# START THE APPLICATION
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
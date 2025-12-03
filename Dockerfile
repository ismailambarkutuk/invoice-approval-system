# Multi-stage build for Invoice Approval System
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom files
COPY pom.xml .
COPY invoice-approval-data/pom.xml ./invoice-approval-data/
COPY invoice-approval-messaging/pom.xml ./invoice-approval-messaging/
COPY invoice-approval-api/pom.xml ./invoice-approval-api/
COPY invoice-approval-web/pom.xml ./invoice-approval-web/

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY invoice-approval-data/src ./invoice-approval-data/src
COPY invoice-approval-messaging/src ./invoice-approval-messaging/src
COPY invoice-approval-api/src ./invoice-approval-api/src
COPY invoice-approval-web/src ./invoice-approval-web/src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage with Payara Server
FROM payara/server-full:6.2024.1-jdk17

# Copy WAR file to Payara deployments
COPY --from=build /app/invoice-approval-web/target/invoice-approval-system.war $DEPLOY_DIR/

# Expose ports
EXPOSE 8080 4848

# Payara base image already has the correct CMD to start the server


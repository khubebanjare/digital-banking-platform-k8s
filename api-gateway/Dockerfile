# =========================================
# Stage 1 - Build
# =========================================
FROM gradle:9.5.1-jdk21 AS build

WORKDIR /digital-payment/api-gateway

COPY . .

RUN chmod +x gradlew && \
    ./gradlew clean build -x test --no-daemon

# =========================================
# Stage 2 - Runtime
# =========================================
FROM eclipse-temurin:21-jre

WORKDIR /digital-payment/api-gateway

COPY --from=build \
    /digital-payment/api-gateway/build/libs/*.jar \
    api-gateway.jar

COPY agents/opentelemetry-javaagent.jar /digital-payment/auth-service/opentelemetry-javaagent.jar

EXPOSE 8082

ENTRYPOINT ["java","-jar","api-gateway.jar"]
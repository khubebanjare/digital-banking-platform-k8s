# =========================================
# Stage 1 - Build
# =========================================
FROM gradle:9.5.1-jdk21 AS build

WORKDIR /digital-payment/config-server

COPY . .

RUN chmod +x gradlew && \
    ./gradlew clean build -x test --no-daemon

# =========================================
# Stage 2 - Runtime
# =========================================
FROM eclipse-temurin:21-jre

WORKDIR /digital-payment/config-server

COPY --from=build \
    /digital-payment/config-server/build/libs/*.jar \
    config-server.jar

EXPOSE 9999

ENTRYPOINT ["java","-jar","config-server.jar"]
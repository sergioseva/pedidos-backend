FROM eclipse-temurin:17-jdk-alpine AS gradle_build
COPY gradlew /build/
COPY gradle /build/gradle/
COPY build.gradle.kts settings.gradle.kts /build/
COPY src /build/src/
WORKDIR /build/
RUN chmod +x gradlew && ./gradlew bootJar --no-daemon -x test

FROM eclipse-temurin:17-jre-alpine
ARG BUILD_VERSION=dev
ENV INFO_APP_VERSION=$BUILD_VERSION
VOLUME /tmp
WORKDIR /app
COPY --from=gradle_build /build/build/libs/*.jar /app/pedidos.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "pedidos.jar"]

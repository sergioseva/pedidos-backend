FROM maven:3.9-eclipse-temurin-17-alpine AS maven_build
COPY pom.xml /build/
COPY src /build/src/
WORKDIR /build/
RUN mvn package -DskipTests

FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
WORKDIR /app
COPY --from=maven_build /build/target/*.jar /app/pedidos.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "pedidos.jar"]

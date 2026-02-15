FROM maven:3.5.2-jdk-8-alpine AS MAVEN_BUILD
COPY pom.xml /build/
COPY src /build/src/
WORKDIR /build/
RUN mvn package

FROM eclipse-temurin:8-jre
VOLUME /tmp
WORKDIR /app
COPY --from=MAVEN_BUILD /build/target/*.jar /app/pedidos.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "pedidos.jar","--spring.config.location=file:///aplicaciones/pedidos/config/application.properties"]


#FROM openjdk:8-jdk-alpine
#VOLUME /tmp
#COPY target/*.jar pedidos.jar
#EXPOSE 8080
#ENTRYPOINT ["java","-jar","/pedidos.jar"]
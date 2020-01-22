FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/*.jar pedidos.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/pedidos.jar"]
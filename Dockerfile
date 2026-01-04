#Deprecated 2025
#FROM openjdk:17-jdk-alpine
FROM eclipse-temurin:17-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} intraoralbill.jar
ENTRYPOINT ["java", "-jar", "/intraoralbill.jar"]
EXPOSE 8005
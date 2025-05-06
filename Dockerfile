# Stage 1: Mit Maven bauen
FROM maven:3.9.3-eclipse-temurin-17-focal AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Nur das JAR ins Runtime-Image
FROM openjdk:17-jdk
WORKDIR /app
COPY --from=builder /app/target/*.jar ./mankomania.jar

# Spring Boot lauscht im Container auf 8080
EXPOSE 8080
ENTRYPOINT ["java","-jar","mankomania.jar"]

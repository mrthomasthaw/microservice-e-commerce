FROM maven:3.9.9-eclipse-temurin-21 AS build

#Set the working directory
WORKDIR /app

COPY pom.xml .

COPY src ./src

RUN mvn clean package -DskipTests

From eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
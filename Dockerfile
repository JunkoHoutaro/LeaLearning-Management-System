FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/Mini_Project1-0.0.1-SNAPSHOT.war /app/Mini_Project1.war

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "Mini_Project1.war"]
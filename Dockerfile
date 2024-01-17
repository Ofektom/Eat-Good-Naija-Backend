FROM openjdk:17
COPY /target/Eat-Good-Live-Project-0.0.1-SNAPSHOT.jar /app/Eat-Good-Live-Project.jar
WORKDIR /app
EXPOSE 8081
CMD ["java", "-jar", "Eat-Good-Live-Project.jar"]
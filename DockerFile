FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/moneymanagement-0.0.1-SNAPSHOT.jar moneymanagement-v1.0.jar
EXPOSE 9090
ENTRYPOINT ["java","-jar","moneymanagement-v1.0.jar"]
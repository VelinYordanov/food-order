FROM openjdk:16-jdk-alpine
RUN mkdir -p /home/java/app
WORKDIR /home/java/app
COPY ./target/food-order-0.0.1-SNAPSHOT.jar ./app.jar
CMD ["java", "-jar", "app.jar"]
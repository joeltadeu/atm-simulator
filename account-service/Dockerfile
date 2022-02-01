FROM openjdk:17-alpine3.14
ADD target/account-service.jar account-service.jar
EXPOSE 7501
ENTRYPOINT ["java", "-jar", "/account-service.jar"]
FROM openjdk:17-alpine3.14
ADD target/atm-service.jar atm-service.jar
EXPOSE 7500
ENTRYPOINT ["java", "-jar", "/atm-service.jar"]
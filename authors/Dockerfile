FROM maven:3.8.4-openjdk-17-slim as build
WORKDIR /helidon

ADD pom.xml .
RUN mvn package -DskipTests

ADD src src
RUN mvn package -DskipTests
RUN echo "done!"

FROM openjdk:17-jdk-slim
WORKDIR /helidon

COPY --from=build /helidon/target/app-authors.jar ./
COPY --from=build /helidon/target/libs ./libs

CMD ["java", "-jar", "app-authors.jar"]
EXPOSE 8080
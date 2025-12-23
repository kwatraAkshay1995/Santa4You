FROM gradle:8.6-jdk17 AS build
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon || return 0
COPY src ./src
RUN gradle clean build -x test --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
RUN mkdir -p /app/data
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
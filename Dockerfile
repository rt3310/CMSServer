FROM azul/zulu-openjdk-alpine:25-latest AS builder
WORKDIR /app

COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test --no-daemon

FROM azul/zulu-openjdk-alpine:25-jre-headless
WORKDIR /app

COPY --from=builder /app/build/libs/*-SNAPSHOT.jar app.jar

RUN apk add --no-cache curl

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
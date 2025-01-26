# Этап сборки
FROM maven:3.8.4-openjdk-11-slim as build

WORKDIR /app
COPY . /app

# Сборка проекта
RUN mvn clean install

# Этап исполнения
FROM maven:3.8.4-openjdk-17-slim AS build

WORKDIR /app

# Копируем собранный код из этапа сборки
COPY --from=build /app/target/classes /app

# Запуск Spring Boot приложения
CMD ["java", "-cp", "/app", "comprestigebankv4.Prestigebankv4Application"]

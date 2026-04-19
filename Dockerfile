# Этап 1: Сборка приложения с Maven
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Копируем pom.xml отдельно для кэширования зависимостей
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копируем исходный код и собираем JAR
COPY src ./src
RUN mvn clean package -DskipTests -B

# Этап 2: Запуск приложения
FROM amazoncorretto:17

WORKDIR /app

# Копируем собранный JAR из этапа builder
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
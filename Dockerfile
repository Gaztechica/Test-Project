# # Шаг 1: Используем официальный образ Maven на базе Java 17 для сборки и запуска
# FROM maven:3.9.6-eclipse-temurin-17-alpine

# 1. Используем официальный образ Maven с JDK 17 через зеркало Яндекса
FROM dockerhub.timeweb.cloud/library/maven:3.8.8-eclipse-temurin-17 AS build

# Шаг 2: Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Шаг 3: Копируем файл зависимостей pom.xml
COPY pom.xml .

# Шаг 4: Скачиваем все зависимости (кэшируем этот слой, чтобы сборка шла быстрее)
RUN mvn dependency:go-offline -B

# Шаг 5: Копируем весь исходный код проекта (папку src)
COPY src ./src

# Шаг 6: По умолчанию запускаем команду тестирования при старте контейнера
CMD ["mvn", "clean", "test"]

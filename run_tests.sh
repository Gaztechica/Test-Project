#!/bin/bash

 set -e

 echo "=== 1. Сборка образов и запуск всей инфраструктуры ==="
 # Docker сам соберет проект, запустит базы и начнет выполнять mvn test внутри контейнера autotests
 docker compose up --build --exit-code-from autotests

 echo "=== 2. Генерация Allure-отчета ==="
 # Результаты тестов сохранились на ПК в папку target благодаря настроенному volume
 if command -v allure &> /dev/null; then
     allure generate target/allure-results --clean -o allure-report
     echo "Отчет успешно сохранен в папку allure-report/"
 else
     echo "Предупреждение: утилита 'allure' не найдена. Результаты в target/allure-results."
 fi

 echo "=== 3. Полная очистка окружения ==="
 # Удаляем контейнеры и очищаем тома данных для чистоты следующих запусков
 docker compose down -v

@api
Feature: API аутентификации эндпоинта /auth

  @regression @matrix
  Scenario Outline: Тестирование матрицы аутентификации эндпоинта /auth (<testCaseName>)
    Given API сервис аутентификации запущен и доступен
    When Отправляется POST запрос на "/auth" с логином "<username>" и паролем "<password>"
    Then Сервер возвращает код ответа <expectedStatusCode>
    And Ответ соответствует JSON схеме "response-token.json"

    Examples:

      | testCaseName | username | password   | expectedStatusCode |
      | AUTH-001     | admin    | password123| 200                |
      | AUTH-002     | wrong_adm| password123| 200                |
      | AUTH-003     | admin    | wrong_pass | 200                |
      | AUTH-004     |          | password123| 200                |
      | AUTH-005     | admin    |            | 200                |

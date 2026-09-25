@ui
Feature: Авторизация в системе SauceDemo

  @smoke @positive
  Scenario: Успешный вход зарегистрированного пользователя (Happy Path)
    Given Пользователь открывает страницу авторизации SauceDemo
    When Пользователь вводит валидные данные стандартного пользователя
    Then Пользователь успешно перенаправлен на страницу каталога товаров
    And Текущий URL содержит "/inventory.html"

  @regression @negative
  Scenario Outline: Авторизация с некорректными или пустыми данными
    Given Пользователь открывает страницу авторизации SauceDemo
    When Пользователь вводит логин "<username>" и пароль "<password>"
    Then Отображается ошибка валидации полей

    Examples:

      | username        | password     |
      | locked_out_user | secret_sauce |
      | invalid_user    | wrong_pass   |
      |                 |              |

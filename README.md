# NauJava To-Do List

Spring Boot 3 (Java 21) приложение для задач с подзадачами, статусами, уведомлениями о просрочках, HTML/REST UI и Swagger. Стек: Spring Web, Data JPA, Security (BCrypt), Thymeleaf, SpringDoc, PostgreSQL.

## Функционал
- Регистрация/логин, роли `USER`/`ADMIN`.
- CRUD задач и подзадач, валидация дат, поиск/фильтр по пользователю и диапазону дат.
- UI на Thymeleaf: `/tasks-page`, формы создания/редактирования, управление подзадачами.
- Уведомления о просрочках каждые 30 сек.
- Отчёты HTML: `/reports/create`, `/reports/{id}` (асинхронно).
- REST для пользователей и задач (Spring Data REST + кастомные контроллеры), Swagger UI (только `ADMIN`).

## Быстрый старт
Требуется: JDK 21, Maven 3.9+, PostgreSQL, Chrome (для UI-тестов).

1) Создайте БД `TEST_NAU` (и `FOR_TEST` для профиля test).
2) При необходимости задайте переменные:
```
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/TEST_NAU
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
```
3) Базовые статусы `НОВАЯ`, `В ПРОГРЕССЕ`, `ЗАВЕРШЕНА` создаются автоматически в `Config.seedTaskStatuses`; убедитесь, что таблица пуста или значения не дублируются.

Запуск:
```
mvn spring-boot:run
```
По умолчанию: `http://localhost:8080`.

## Основные маршруты
- UI: `/login`, `/registration`, `/tasks-page`, `/notifications`, `/notifications-page`
- REST: `/tasks`, `/users`, `/between?start=yyyy-MM-dd&end=yyyy-MM-dd`, `/by-user?login={login}`, `/reports/create`, `/reports/{id}`
- Swagger UI: `/swagger-ui` (роль `ADMIN`)

## Тесты
- Профиль: `-Dspring.profiles.active=test` (БД `FOR_TEST`, ddl-auto=create-drop, без Security авто-конфиг).
```
mvn -Dspring.profiles.active=test test
```
Selenium UI-тесты требуют установленный Chrome.

# NauJava To-Do List
Управление задачами (To-Do List). Можно заводить задачи, менять статусы, устанавливать сроки выполнения, получать оповещения.
Стек: Spring Web, Data JPA, Security (BCrypt), Thymeleaf, SpringDoc, PostgreSQL.

## Функционал
- Регистрация/логин, роли `USER`/`ADMIN`, проверка уникальности имени пользователя.
- CRUD задач и подзадач, валидация дат, поиск/фильтр по пользователю и диапазону дат.
- UI на Thymeleaf: страница задач пользователя, формы создания/редактирования, управление подзадачами и форма просмотра уведомлений пользователя
- Уведомления о просрочках задач каждые 30 сек.
- Отчёты HTML: /reports/create, /reports/{id} выполняются асинхронно.
- REST для пользователей и задач (Spring Data REST + кастомные контроллеры), Swagger UI для пользователей с ролью ADMIN

## Быстрый старт
Требуется: JDK 21, Maven 3.9+, PostgreSQL, Chrome (для UI-тестов).

1) Создайте БД TEST_NAU и FOR_TEST для профиля test.
2) Задайте переменные в application.properties:
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
- Swagger UI: `/swagger-ui` (только для роли - `ADMIN`)

## Тесты
1) Задайте переменные в application-test.properties:
```
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/FOR_TEST
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
```
2) Для запуска используйте:
```
mvn -Dspring.profiles.active=test test
```
Selenium UI-тесты требуют установленный Chrome.

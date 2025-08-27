# News Management System

Система управления новостями и пользователями, построенная на Spring Boot, Hibernate, PostgreSQL и Oracle. Предназначена для создания, редактирования, поиска и отображения новостей с комментариями, с полной поддержкой CRUD-операций и архитектурной прозрачности.

---

## Назначение

Приложение реализует:

- Управление новостями:
    - Получение всех новостей (в том числе с пагинацией)
    - Поиск по заголовку и тексту
    - Получение новости по ID
    - Получение новости с постраничными комментариями
    - Создание, обновление и удаление новости

- Управление пользователями:
    - Получение пользователя по ID
    - Получение списка всех пользователей
    - Создание, обновление и удаление пользователя

- Архитектурные особенности:
    - DTO-модели и мапперы
    - Кастомные исключения: `NewsNotFoundException`, `UserNotFoundException` ...
    - Логирование: `debug` — для payload'ов, `info` — для действий, `warn` — при ошибках
    - Использование `@Transactional` для операций записи
    - Поддержка двух баз данных: PostgreSQL (основная) и Oracle (вторичная)

---

## Интерфейс

### Backend (Spring Boot, Java 21)

- REST API endpoints:
    - `/api/news` — управление новостями
    - `/api/users` — управление пользователями
    - `/api/comments` — комментарии к новостям

- DTO-модели:
    - `NewsDto`, `UserDto`, `CommentDto`, `NewsWithCommentsPagedDto`

- Мапперы:
    - `NewsMapper`, `UserMapper`, `CommentMapper`

- Репозитории:
    - `NewsRepository`, `UserRepository`, `CommentRepository`

- Исключения:
    - `NewsNotFoundException`, `UserNotFoundException` ...

---

## Настройки и запуск

### Требования

- Spring Boot 3.5.4
- JDK 21
- Maven 3.9+
- PostgreSQL 14+
- Oracle XE / 19c 

### Конфигурация базы данных

В `application.yml`:

```yaml
spring:
  profiles:
    active: test # prod

  application:
    name: springnews-api

  datasource:
    url: jdbc:postgresql://localhost:5433/springnews
    username: admin
    password: admin
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: none
    show-sql: true
    properties:
      hibernate:
        format_sql: true
    database-platform: org.hibernate.dialect.PostgreSQLDialect

  sql:
    init:
      mode: always
      schema-locations: classpath:schema.sql
      data-locations: classpath:data.sql

server:
  port: 8080

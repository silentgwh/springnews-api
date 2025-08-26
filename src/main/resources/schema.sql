-- Таблица пользователей
CREATE TABLE IF NOT EXISTS app_user (
                                        id BIGSERIAL PRIMARY KEY,
                                        username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    surname VARCHAR(50) NOT NULL,
    parent_name VARCHAR(50) NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_edit_date TIMESTAMP NOT NULL
    );

-- Таблица новостей
CREATE TABLE IF NOT EXISTS news (
                                    id BIGSERIAL PRIMARY KEY,
                                    title VARCHAR(150) NOT NULL,
    text TEXT NOT NULL CHECK (char_length(text) <= 2000),
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_edit_date TIMESTAMP NOT NULL,
    created_by_user BIGINT NOT NULL,
    updated_by_user BIGINT,
    CONSTRAINT fk_news_created_by FOREIGN KEY (created_by_user) REFERENCES app_user(id) ON DELETE CASCADE,
    CONSTRAINT fk_news_updated_by FOREIGN KEY (updated_by_user) REFERENCES app_user(id) ON DELETE SET NULL
    );

-- Таблица комментариев
CREATE TABLE IF NOT EXISTS comments (
                                        id BIGSERIAL PRIMARY KEY,
                                        text VARCHAR(300) NOT NULL,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_edit_date TIMESTAMP NOT NULL,
    created_by_user BIGINT NOT NULL,
    news_id BIGINT NOT NULL,
    CONSTRAINT fk_comment_user FOREIGN KEY (created_by_user) REFERENCES app_user(id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_news FOREIGN KEY (news_id) REFERENCES news(id) ON DELETE CASCADE
    );

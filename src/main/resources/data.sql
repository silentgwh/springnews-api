-- ============================
-- пользователи
-- ============================

INSERT INTO app_user (username, password, name, surname, parent_name, creation_date, last_edit_date)
VALUES
    ('alice', 'pass123', 'Alice', 'Smith', 'Marie', NOW() - INTERVAL '15 days', NOW() - INTERVAL '14 days'),
    ('bob',   'pass123', 'Bob',   'Johnson', 'Edward', NOW() - INTERVAL '14 days', NOW() - INTERVAL '13 days'),
    ('carol', 'pass123', 'Carol', 'Williams', 'Helen', NOW() - INTERVAL '13 days', NOW() - INTERVAL '12 days'),
    ('dave',  'pass123', 'Dave',  'Brown', 'George', NOW() - INTERVAL '12 days', NOW() - INTERVAL '11 days')
    ON CONFLICT (username) DO NOTHING;

-- ============================
-- новости
-- ============================

INSERT INTO news (title, text, created_by_user, updated_by_user, creation_date, last_edit_date)
SELECT 'Новость 1', 'Текст новости 1', u1.id, u2.id, NOW() - INTERVAL '10 days', NOW() - INTERVAL '9 days'
FROM app_user u1, app_user u2
WHERE u1.username = 'alice' AND u2.username = 'bob'
  AND NOT EXISTS (SELECT 1 FROM news WHERE title = 'Новость 1');

INSERT INTO news (title, text, created_by_user, updated_by_user, creation_date, last_edit_date)
SELECT 'Новость 2', 'Текст новости 2', u1.id, u2.id, NOW() - INTERVAL '9 days', NOW() - INTERVAL '8 days'
FROM app_user u1, app_user u2
WHERE u1.username = 'bob' AND u2.username = 'carol'
  AND NOT EXISTS (SELECT 1 FROM news WHERE title = 'Новость 2');

INSERT INTO news (title, text, created_by_user, updated_by_user, creation_date, last_edit_date)
SELECT 'Новость 3', 'Текст новости 3', u1.id, u2.id, NOW() - INTERVAL '8 days', NOW() - INTERVAL '7 days'
FROM app_user u1, app_user u2
WHERE u1.username = 'carol' AND u2.username = 'dave'
  AND NOT EXISTS (SELECT 1 FROM news WHERE title = 'Новость 3');

INSERT INTO news (title, text, created_by_user, updated_by_user, creation_date, last_edit_date)
SELECT 'Новость 4', 'Текст новости 4', u1.id, u2.id, NOW() - INTERVAL '7 days', NOW() - INTERVAL '6 days'
FROM app_user u1, app_user u2
WHERE u1.username = 'alice' AND u2.username = 'carol'
  AND NOT EXISTS (SELECT 1 FROM news WHERE title = 'Новость 4');

INSERT INTO news (title, text, created_by_user, updated_by_user, creation_date, last_edit_date)
SELECT 'Новость 5', 'Текст новости 5', u1.id, u2.id, NOW() - INTERVAL '6 days', NOW() - INTERVAL '5 days'
FROM app_user u1, app_user u2
WHERE u1.username = 'bob' AND u2.username = 'alice'
  AND NOT EXISTS (SELECT 1 FROM news WHERE title = 'Новость 5');

-- ============================
-- комментарии
-- ============================

-- Комментарии к Новость 1
INSERT INTO comments (text, created_by_user, news_id, creation_date, last_edit_date)
SELECT 'Комментарий 1 к новости 1', u.id, n.id, NOW() - INTERVAL '9 days', NOW() - INTERVAL '8 days'
FROM app_user u, news n
WHERE u.username = 'bob' AND n.title = 'Новость 1'
  AND NOT EXISTS (
    SELECT 1 FROM comments WHERE text = 'Комментарий 1 к новости 1' AND created_by_user = u.id AND news_id = n.id
    );

INSERT INTO comments (text, created_by_user, news_id, creation_date, last_edit_date)
SELECT 'Комментарий 2 к новости 1', u.id, n.id, NOW() - INTERVAL '8 days', NOW() - INTERVAL '7 days'
FROM app_user u, news n
WHERE u.username = 'carol' AND n.title = 'Новость 1'
  AND NOT EXISTS (
    SELECT 1 FROM comments WHERE text = 'Комментарий 2 к новости 1' AND created_by_user = u.id AND news_id = n.id
    );

INSERT INTO comments (text, created_by_user, news_id, creation_date, last_edit_date)
SELECT 'Комментарий 3 к новости 1', u.id, n.id, NOW() - INTERVAL '7 days', NOW() - INTERVAL '6 days'
FROM app_user u, news n
WHERE u.username = 'dave' AND n.title = 'Новость 1'
  AND NOT EXISTS (
    SELECT 1 FROM comments WHERE text = 'Комментарий 3 к новости 1' AND created_by_user = u.id AND news_id = n.id
    );

-- Комментарии к Новость 2
INSERT INTO comments (text, created_by_user, news_id, creation_date, last_edit_date)
SELECT 'Комментарий 1 к новости 2', u.id, n.id, NOW() - INTERVAL '8 days', NOW() - INTERVAL '7 days'
FROM app_user u, news n
WHERE u.username = 'alice' AND n.title = 'Новость 2'
  AND NOT EXISTS (
    SELECT 1 FROM comments WHERE text = 'Комментарий 1 к новости 2' AND created_by_user = u.id AND news_id = n.id
    );

INSERT INTO comments (text, created_by_user, news_id, creation_date, last_edit_date)
SELECT 'Комментарий 2 к новости 2', u.id, n.id, NOW() - INTERVAL '7 days', NOW() - INTERVAL '6 days'
FROM app_user u, news n
WHERE u.username = 'carol' AND n.title = 'Новость 2'
  AND NOT EXISTS (
    SELECT 1 FROM comments WHERE text = 'Комментарий 2 к новости 2' AND created_by_user = u.id AND news_id = n.id
    );

-- Комментарии к Новость 3
INSERT INTO comments (text, created_by_user, news_id, creation_date, last_edit_date)
SELECT 'Комментарий 1 к новости 3', u.id, n.id, NOW() - INTERVAL '6 days', NOW() - INTERVAL '5 days'
FROM app_user u, news n
WHERE u.username = 'alice' AND n.title = 'Новость 3'
  AND NOT EXISTS (
    SELECT 1 FROM comments WHERE text = 'Комментарий 1 к новости 3' AND created_by_user = u.id AND news_id = n.id
    );

INSERT INTO comments (text, created_by_user, news_id, creation_date, last_edit_date)
SELECT 'Комментарий 2 к новости 3', u.id, n.id, NOW() - INTERVAL '5 days', NOW() - INTERVAL '4 days'
FROM app_user u, news n
WHERE u.username = 'bob' AND n.title = 'Новость 3'
  AND NOT EXISTS (
    SELECT 1 FROM comments WHERE text = 'Комментарий 2 к новости 3' AND created_by_user = u.id AND news_id = n.id
    );


-- Комментарии к Новость 4
INSERT INTO comments (text, created_by_user, news_id, creation_date, last_edit_date)
SELECT 'Комментарий 1 к новости 4', u.id, n.id, NOW() - INTERVAL '4 days', NOW() - INTERVAL '3 days'
FROM app_user u, news n
WHERE u.username = 'carol' AND n.title = 'Новость 4'
  AND NOT EXISTS (
    SELECT 1 FROM comments WHERE text = 'Комментарий 1 к новости 4' AND created_by_user = u.id AND news_id = n.id
    );

INSERT INTO comments (text, created_by_user, news_id, creation_date, last_edit_date)
SELECT 'Комментарий 2 к новости 4', u.id, n.id, NOW() - INTERVAL '3 days', NOW() - INTERVAL '2 days'
FROM app_user u, news n
WHERE u.username = 'dave' AND n.title = 'Новость 4'
  AND NOT EXISTS (
    SELECT 1 FROM comments WHERE text = 'Комментарий 2 к новости 4' AND created_by_user = u.id AND news_id = n.id
    );

-- Комментарии к Новость 5
INSERT INTO comments (text, created_by_user, news_id, creation_date, last_edit_date)
SELECT 'Комментарий 1 к новости 5', u.id, n.id, NOW() - INTERVAL '2 days', NOW() - INTERVAL '1 days'
FROM app_user u, news n
WHERE u.username = 'alice' AND n.title = 'Новость 5'
  AND NOT EXISTS (
    SELECT 1 FROM comments WHERE text = 'Комментарий 1 к новости 5' AND created_by_user = u.id AND news_id = n.id
    );

INSERT INTO comments (text, created_by_user, news_id, creation_date, last_edit_date)
SELECT 'Комментарий 2 к новости 5', u.id, n.id, NOW() - INTERVAL '1 days', NOW()
FROM app_user u, news n
WHERE u.username = 'bob' AND n.title = 'Новость 5'
  AND NOT EXISTS (
    SELECT 1 FROM comments WHERE text = 'Комментарий 2 к новости 5' AND created_by_user = u.id AND news_id = n.id
    );

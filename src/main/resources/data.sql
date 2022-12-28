-- Обязательное заполнение словарей
-- Заполняем жанры
MERGE INTO filmorate_genre (genre_id, genre)
    VALUES (1, 'Комедия'),
           (2, 'Драма'),
           (3, 'Мультфильм'),
           (4, 'Триллер'),
           (5, 'Документальный'),
           (6, 'Боевик')
-- ON CONFLICT(genre) DO NOTHING
;

-- Заполняем рейтинги
MERGE INTO filmorate_mpa_rating (rating_id, rating)
    VALUES (1, 'G'),
           (2, 'PG'),
           (3, 'PG-13'),
           (4, 'R'),
           (5, 'NC-17')
-- ON CONFLICT(rating) DO NOTHING
;
-- Заполняем статусы дружбы
MERGE INTO filmorate_friendship_status (status_id, status_name)
    VALUES (1, 'REQUESTED'),
           (2, 'APPROVED'),
           (3, 'REJECTED')
;
-- ON CONFLICT(status_name) DO NOTHING
;

-- Заполняем данными, фухххх
-- 10 пользователей
MERGE INTO filmorate_user (
                           user_login,
                           name,
                           birthday, email)
    VALUES ('user1', 'user1', '2001-01-01', 'user1@user1'),
           ('user2', 'user2', '2002-02-02', 'user2@user2'),
           ('user3', 'user3', '2003-03-03', 'user3@user3'),
           ('user4', 'user4', '2004-04-04', 'user4@user4'),
           ('user5', 'user5', '2005-05-05', 'user5@user5'),
           ('user6', 'user6', '2006-06-06', 'user6@user6'),
           ('user7', 'user7', '2007-07-07', 'user7@user7'),
           ('user8', 'user8', '2008-08-08', 'user1@user8'),
           ('user9', 'user9', '2009-09-09', 'user9@user9'),
           ('user10', 'user10', '2010-10-10', 'user10@user10')
-- ON CONFLICT(user_login) DO UPDATE
-- SET name = EXCLUDED.name, birthday = EXCLUDED.birthday, email = EXCLUDED.email
;

-- Давай дружить!!
MERGE INTO filmorate_friendship (
                                 from_user,
                                 to_user,
                                 status_id)
    VALUES ('user1', 'user2', 1),
           ('user2', 'user3', 2),
           ('user3', 'user4', 2),
           ('user4', 'user5', 1),
           ('user5', 'user6', 2),
           ('user6', 'user7', 2),
           ('user7', 'user8', 1),
           ('user8', 'user9', 2),
           ('user9', 'user10', 3),
           ('user10', 'user1', 1),
           ('user1', 'user3', 1),
           ('user2', 'user4', 2),
           ('user3', 'user5', 3),
           ('user4', 'user6', 1),
           ('user5', 'user7', 2),
           ('user6', 'user8', 3),
           ('user7', 'user9', 2),
           ('user8', 'user10', 2),
           ('user9', 'user1', 2),
           ('user10', 'user2', 1),
           ('user1', 'user4', 1),
           ('user2', 'user5', 2),
           ('user3', 'user6', 2),
           ('user4', 'user7', 1),
           ('user5', 'user8', 2),
           ('user6', 'user9', 2),
           ('user7', 'user10', 1),
           ('user8', 'user1', 2),
           ('user9', 'user2', 3),
           ('user10', 'user3', 1),
           ('user1', 'user5', 1),
           ('user2', 'user6', 2),
           ('user3', 'user7', 3),
           ('user4', 'user8', 1),
           ('user5', 'user9', 2),
           ('user6', 'user10', 3),
           ('user7', 'user1', 1),
           ('user8', 'user2', 2),
           ('user9', 'user3', 2),
           ('user10', 'user4', 1),
           ('user1', 'user6', 1),
           ('user2', 'user7', 2),
           ('user3', 'user8', 2),
           ('user4', 'user9', 1),
           ('user5', 'user10', 2)
--,('user6', 'user1', 3), ('user7', 'user2', 1), ('user8', 'user3', 2), ('user9', 'user4', 3),( 'user10', 'user5', 1)
-- До сих пор не знаю как сделать уникальными пары значений в колонках НЕЗАВИСИМО от порядка.
-- Текущее решение только проверяет в порядке первое и второе значение. так что последняя закоментированная строка
-- спокойно инсертится и хранится. Таким оброзом дублируются статусы отношений. Как бороться не знаю.
-- Можно отфильтовывать на уровне запросов и бизнес логики, но это не гарант.
--Есть решение что по одоговоренности слева (поле from ) будет меньший айдишник.
-- ON CONFLICT(from_user, to_user) DO UPDATE
-- SET status_id = EXCLUDED.status_id
;

-- 10 фильмОв
MERGE INTO filmorate_film (
                           film_id,
                           title,
                           description,
                           release_date,
                           duration_minutes,
                           rating_mpa)
    VALUES (1, 'Фильм 1', 'Описание фильма 1', '1991-01-01', '101', 1),
           (2, 'Фильм 2', 'Описание фильма 2', '1992-02-02', '102', 2),
           (3, 'Фильм 3', 'Описание фильма 3', '1993-03-03', '103', 3),
           (4, 'Фильм 4', 'Описание фильма 4', '1994-04-04', '104', 4),
           (5, 'Фильм 5', 'Описание фильма 5', '1995-05-05', '105', 5),
           (6, 'Фильм 6', 'Описание фильма 6', '1996-06-06', '106', 1),
           (7, 'Фильм 7', 'Описание фильма 7', '1997-07-07', '107', 2),
           (8, 'Фильм 8', 'Описание фильма 8', '1998-08-08', '108', 3),
           (9, 'Фильм 9', 'Описание фильма 9', '1999-09-09', '109', 4),
           (10, 'Фильм 10', 'Описание фильма 10', '2000-10-10', '110', 5)
-- ON CONFLICT (title,
-- 	description,
-- 	release_date,
-- 	duration_minutes,
-- 	rating_mpa) DO NOTHING
--UPDATE SET title = EXCLUDED.title,
--description = EXCLUDED.description,
--release_date = EXCLUDED.release_date,
--duration_minutes = EXCLUDED.duration_minutes,
--rating_mpa = EXCLUDED.rating_mpa
;
-- Попробуем так сделать. Не знаю сколько "стоит" проверка уникальности всех полей

--Устанавливаем жанры фильмов.
MERGE INTO filmorate_film_genre (
                                 genre_id,
                                 film_id)
    VALUES (1, 1),
           (2, 2),
           (3, 3),
           (4, 4),
           (5, 5),
           (1, 6),
           (2, 7),
           (3, 8),
           (4, 9),
           (5, 10),
           (1, 3),
           (2, 4),
           (3, 5),
           (4, 6),
           (5, 7)
;
-- В этой таблице конфликтов быть не должно, всего два поля и если мы
--добавляем дубль
-- то это мы где то запуталсись и само будет проверенно

--Ставим лайки
MERGE INTO filmorate_like (
                           film_id,
                           user_login
    ) VALUES (1, 'user1'),
             (2, 'user2'),
             (3, 'user3'),
             (4, 'user4'),
             (5, 'user5'),
             (1, 'user6'),
             (2, 'user7'),
             (3, 'user8'),
             (4, 'user9'),
             (5, 'user10'),
             (6, 'user1'),
             (7, 'user2'),
             (8, 'user3'),
             (9, 'user4'),
             (10, 'user5'),
             (6, 'user6'),
             (2, 'user8'),
             (3, 'user9'),
             (3, 'user10'),
             (5, 'user1')
-- ON CONFLICT(film_id, user_login ) DO NOTHING
; -- В этой таблице тоже будут ошибки только если мы ставим второй лайк. Сделаю запись уникальной на всякий.








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






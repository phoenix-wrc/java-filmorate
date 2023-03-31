-- Список всех фильмов пользователя
SELECT film.title FROM filmorate_user AS users 
	JOIN filmorate_like AS likes ON likes.user_login = users.user_login
	JOIN filmorate_film AS film ON likes.film_id = film.film_id
WHERE users.name = 'user1'
;

-- топ 5 наиболее популярных фильмов hb большего к меньшему
SELECT film.title, COUNT(likes.user_login) AS count_like
FROM filmorate_like AS likes
         JOIN filmorate_film AS film ON likes.film_id = film.film_id
GROUP BY film.title
ORDER BY count_like DESC
LIMIT 5
;

-- Запрос общих друзей для пошльзователя user9 и user8
SELECT from_user
FROM ((SELECT ff1.from_user, ff1.status_id
       FROM filmorate_friendship AS ff1
       where ff1.to_user = 'user9'
         AND ff1.status_id = 2
       UNION
       SELECT ff2.to_user, ff2.status_id
       FROM filmorate_friendship AS ff2
       where ff2.from_user = 'user9'
         AND ff2.status_id = 2)
      UNION ALL
      (SELECT ff3.from_user, ff3.status_id
       FROM filmorate_friendship AS ff3
       where ff3.to_user = 'user8'
         AND ff3.status_id = 2
       UNION
       SELECT ff4.to_user, ff4.status_id
       FROM filmorate_friendship AS ff4
       where ff4.from_user = 'user8'
         AND ff4.status_id = 2)) AS t1
GROUP BY from_user
HAVING COUNT(from_user) > 1
		
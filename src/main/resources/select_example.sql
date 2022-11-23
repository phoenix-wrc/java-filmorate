-- Список всех фильмов пользователя
SELECT film.title FROM filmorate_user AS users 
	JOIN filmorate_like AS likes ON likes.user_login = users.user_login
	JOIN filmorate_film AS film ON likes.film_id = film.film_id
WHERE users.name = 'user1'
;

-- топ 5 наиболее популярных фильмов hb большего к меньшему
SELECT film.title , COUNT(likes.user_login) AS count_like FROM filmorate_like AS likes 
	JOIN filmorate_film AS film ON likes.film_id = film.film_id
	GROUP BY film.title
	ORDER BY count_like DESC
	LIMIT 5
;


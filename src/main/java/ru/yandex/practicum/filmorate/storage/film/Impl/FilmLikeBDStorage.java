package ru.yandex.practicum.filmorate.storage.film.Impl;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Component("FilmLikeBDStorage")
@Qualifier("FilmLikeBDStorage")
public class FilmLikeBDStorage implements FilmLikeStorage {
    @NonNull
    private final JdbcTemplate jdbcTemplate;

    @NonNull
    FilmStorage storage;

    public FilmLikeBDStorage(@NonNull JdbcTemplate jdbcTemplate
            , @Qualifier("FilmBDStorage") FilmStorage storage
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.storage = storage;
    }

    @Override
    public Set<Integer> getLikes(Integer filmId) {
        //Тут тоже всё просто
        String sql = "SELECT USER_ID FROM FILMORATE_LIKE WHERE FILM_ID = ?";
        List<Integer> filmLikes = jdbcTemplate.query(sql, (resultSet, columnIndex) ->
                resultSet.getInt("USER_ID"), filmId);
        //Пишут что метод квери не может возвращать нулы так что проверки не требуется
        log.debug("Забрали лайки у фильма {}", filmId);
        return Set.copyOf(filmLikes);
    }

    @Override
    public boolean addLike(Integer filmId, Integer userId) {
        //Тут тоже всё просто
        String sql = "INSERT INTO FILMORATE_LIKE" +
                "(FILM_ID, USER_ID)" +
                "VALUES (?, ?)";
        int row = jdbcTemplate.update(sql, filmId, userId);
        if (row == 0) {
            log.error("Лайк от пользователя {} к фильму {} не поставился", userId, filmId);
            return false;
        } else if (row > 1) {
            log.error("Лайков от пользователя {} к фильму {} слишком много ", userId, filmId);
            return false;
        } else if (row < 0) {
            log.error("Что-то пошло совсем не так. Пользователь {}, фильм {}", userId, filmId);
            return false;
        }
        log.debug("Лайк от пользователя {} к фильму {} поставился", userId, filmId);
        return true;
    }

    @Override
    public boolean removeLike(Integer filmId, Integer userId) {
        //Тут тоже всё просто
        String sql = "DELETE FROM FILMORATE_LIKE " +
//                "(FILM_ID, USER_ID)" +
                "WHERE FILM_ID = ? AND USER_ID = ?";
        int row = jdbcTemplate.update(sql, filmId, userId);
        if (row == 0) {
            log.error("Лайк от пользователя {} к фильму {} не удалился", userId, filmId);
            return false;
        } else if (row > 1) {
            log.error("Лайков от пользователя {} к фильму {} удалилось слишком много ", userId, filmId);
            return false;
        } else if (row < 0) {
            log.error("Что-то пошло совсем не так. Пользователь {}, фильм {}", userId, filmId);
            return false;
        }
        log.debug("Лайк от пользователя {} к фильму {} удалился", userId, filmId);
        return true;
    }

    @Override
    public List<Film> getTopFilms(Integer count) {
        String sql = "SELECT film.FILM_ID " +
                "FROM FILMORATE_FILM AS film " +
                "LEFT JOIN FILMORATE_LIKE AS likes " +
                "ON likes.FILM_ID = film.FILM_ID " +
                "GROUP BY film.FILM_ID " +
                "ORDER BY COUNT(likes.USER_ID) DESC " +
                "LIMIT ?;";

        List<Integer> filmsId = jdbcTemplate.query(sql, (resultSet, columnIndex) ->
                resultSet.getInt("FILM_ID"), count);

        if (filmsId.isEmpty()) {
            log.debug("Популярных фильмов нет");
            return Collections.emptyList();
        } else {
            log.debug("Найдено популярных {} фильмов", filmsId.size());
        }

        return filmsId.stream()
                .map(f -> storage.getFilm(f))
                .collect(Collectors.toList());
    }

}

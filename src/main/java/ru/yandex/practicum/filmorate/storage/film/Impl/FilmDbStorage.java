package ru.yandex.practicum.filmorate.storage.film.Impl;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.FilmMapper;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.LocalDateFormatter4FilmReleaseDate;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Types;
import java.util.*;

@Slf4j
@Component("FilmBDStorage")
@Qualifier("FilmBDStorage")
public class FilmDbStorage implements FilmStorage {
    @NonNull
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //Эти классы будут DAO — объектами доступа к данным
    @Override
    public Film add(Film film) {
        String sql = "INSERT INTO FILMORATE_FILM " +
                "(TITLE, DESCRIPTION, RELEASE_DATE, DURATION_MINUTES, RATING_MPA) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        var preparedStatementCreatorFactory = new PreparedStatementCreatorFactory( //{
                sql, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.INTEGER
        );
        preparedStatementCreatorFactory.setReturnGeneratedKeys(true);
        var psc = preparedStatementCreatorFactory.newPreparedStatementCreator(
                Arrays.asList(
                        film.getName(),
                        film.getDescription(),
                        film.getReleaseDate().format(LocalDateFormatter4FilmReleaseDate.getFormatter()),
                        film.getDuration(),
                        film.getMpa().getId())
        );
        //};
        // Решение из инета, но заработало как надо
        int rowEffected = jdbcTemplate.update(psc,
                keyHolder);
        Integer index;
        try {
            index = keyHolder.getKeyAs(Integer.class);
            log.debug("Сохранили фильм с индексом {}", index);
        } catch (NullPointerException e) {
            log.error("Новый ИД из базы не вернулся, дальше всё не будет работать");
            return film; //Незнаю что возвращать. Можно рефакторнуть и в сигнатурах задать возвращение опшинала, но потом
        }
//         Отдельно инсертим в свои таблицы жанры
        Set<Genre> genres = film.getGenres();
        if (genres != null && !genres.isEmpty()) {
            for (Genre g : genres) {
                jdbcTemplate.update("INSERT INTO FILMORATE_FILM_GENRE (GENRE_ID, FILM_ID) " +
                        "VALUES (?, ?)", g.getId(), index
                );
            }
        }

        return getFilm(index);
    }

    @Override
    public Film delete(Integer id) {
        String sql = "DELETE FROM FILMORATE_LIKE WHERE FILM_ID = ?; " +
                "DELETE FROM FILMORATE_FILM_GENRE WHERE FILM_ID = ?;" +
                "DELETE FROM FILMORATE_FILM WHERE FILM_ID = ?  ";
        //Пока так, можно переделать каскадом но нужно еще почитать доку к БД
        Film out = getFilm(id);
        int deleteRow = jdbcTemplate.update(sql, id);
        if (deleteRow == 1) {
            log.debug("Удален фильм с ИД " + id);
        } else if (deleteRow > 1) {
            log.error("Удалилось больше одного фильма по ИД" + id);
        } else if (deleteRow == 0) {
            log.error("Ни одного фильма по ИД" + id + "не удалилось");
            throw new FilmNotFoundException("Нет фильма стаким ИД для удаления");
        }
        return out;
    }

    @Override
    public Film patch(Film film) {
        String sql = "UPDATE FILMORATE_FILM " +
                "SET " +
                "TITLE = ?, " +
                "DESCRIPTION = ?, " +
                "RELEASE_DATE = ?, " +
                "DURATION_MINUTES = ?, " +
                "RATING_MPA = ? " +
                "WHERE FILM_ID = ?";

        int patchRow = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate().format(LocalDateFormatter4FilmReleaseDate.getFormatter()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        if (patchRow == 1) {
            log.debug("Обновлен фильм с ИД " + film.getId());
        } else if (patchRow > 1) {
            log.error("Обновлено больше одного фильма по ИД" + film.getId());
        } else if (patchRow == 0) {
            log.error("Ни одного фильма по ИД {} не обновилось", film.getId());
            throw new FilmNotFoundException("Не нашлось фильма с таким ИД для обновления");
        }

        // Что бы поработать с жанрами нужно получается их сначало удалить а потом снова добавить.
        // В теории можно использовать мердж, но не понятно тогда как удалить если жанр убрали.

        Set<Genre> genres = film.getGenres();
        if (genres != null && !genres.isEmpty()) {
            jdbcTemplate.update("DELETE FROM FILMORATE_FILM_GENRE " +
                    "WHERE FILM_ID = ?", film.getId());

            for (Genre g : genres) {
                jdbcTemplate.update("INSERT INTO FILMORATE_FILM_GENRE (GENRE_ID, FILM_ID) " +
                        "VALUES (?, ?)", g.getId(), film.getId()
                );
            }
        }

        Film out = getFilm(film.getId());
        if (out == null) {
            log.error("Из базы не вернулся фильм при обновлении фильма " + film.getId());
            throw new FilmNotFoundException("Не нашлось фильма после обновления");
        }
        //Логирую всё что могу, хотя не думаю что это хорошо
        return out;
    }

    @Override
    public Collection<Film> films() {
        String sql = "SELECT film.FILM_ID, film.TITLE, film.DESCRIPTION, film.RELEASE_DATE, film.DURATION_MINUTES," +
                "film.RATING_MPA, mpa.RATING FROM FILMORATE_FILM AS film " +
                "JOIN FILMORATE_MPA_RATING AS mpa " +
                "ON film.RATING_MPA = mpa.RATING_ID";
        List<Film> films = jdbcTemplate.query(sql, new FilmMapper());
        for (Film f : films) {
            f.setGenres(getGenres(f.getId()));
        }

        if (films.isEmpty()) {
            log.debug("Фильмов нет");
        } else {
            log.debug("Найдено {} фильмов", films.size());
        }
        return films;
    }

    @Override
    public Film getFilm(Integer id) {
        String sql = "SELECT film.FILM_ID, film.TITLE, film.DESCRIPTION, film.RELEASE_DATE, film.DURATION_MINUTES," +
                "film.RATING_MPA, mpa.RATING FROM FILMORATE_FILM AS film " +
                "JOIN FILMORATE_MPA_RATING AS mpa " +
                "ON film.RATING_MPA = mpa.RATING_ID " +
                "WHERE film.FILM_ID = ?";
        Film film = jdbcTemplate.queryForObject(sql, new FilmMapper(), id);
        //Основную работу делает ФилмМапер
        if (film == null) {
            log.info("Пользователь с идентификатором {} не найден.", id);
            throw new FilmNotFoundException("Что то пошло не так");
        } else {
            log.info("Найден фильм: {} {}", film.getId(), film.getName());
        }
        film.setGenres(getGenres(id));
        return film;
    }

    @Override
    public Integer size() {
        // Тут проде всё просто, считаем уоличество записей целиком. Можно ограничится подсчетом Идешников
        String sql = "SELECT Count(FILM_ID) FROM FILMORATE_FILM";
        Integer filmCount = jdbcTemplate.queryForObject(sql, Integer.class);
        log.info("Найдено фильмов: {}", filmCount);
        return filmCount;
    }

    private Set<Genre> getGenres(Integer filmId) {
        // Тут вроде всё просто
        String sql = "SELECT FFG.GENRE_ID, FG.GENRE " +
                "FROM FILMORATE_FILM_GENRE AS FFG " +
                "JOIN FILMORATE_GENRE AS FG on FG.GENRE_ID = FFG.GENRE_ID " +
                "WHERE FFG.FILM_ID = ?";
        List<Genre> genres = this.jdbcTemplate.query(sql, (rs, rowNum) -> {
            return new Genre(rs.getInt("GENRE_ID"), rs.getString("GENRE"));
        }, filmId);
        //Пишут что метод квери не может возвращать нулы так что проверки не требуется
        if (genres.isEmpty()) {
            log.error("Жанров нет, и это ошибка так как такого не должно быть");
            return Collections.emptySet();
        }
        log.debug("Забрали жанры у фильма {}", filmId);
        return Set.copyOf(genres);
    }
}



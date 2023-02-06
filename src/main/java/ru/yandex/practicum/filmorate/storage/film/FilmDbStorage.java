package ru.yandex.practicum.filmorate.storage.film;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.FilmMapper;
import ru.yandex.practicum.filmorate.model.film.LocalDateFormatter4FilmReleaseDate;
import ru.yandex.practicum.filmorate.model.film.enums.GenreFilmEnum;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;
import java.util.Set;


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
                "(title, description, release_date, duration_minutes, rating_mpa) " +
                "VALUES (?, ?, ?, ?, (SELECT RATING_ID FROM FILMORATE_MPA_RATING WHERE RATING=?))";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                    ps.setString(1, film.getName());
                    ps.setString(2, film.getDescription());
                    ps.setString(3,
                            film.getReleaseDate().format(LocalDateFormatter4FilmReleaseDate.getFormatter()));
                    ps.setString(4, film.getDuration().toString());
                    ps.setString(5, film.getMpaRating().toString());
                    return ps;
                },
                keyHolder);
        Integer index;
        try {
            index = keyHolder.getKey().intValue();
        } catch (NullPointerException e) {
            log.error("Новый ИД из базы не вернулся, дальше всё не будет работать");
            return film; //Незнаю что возвращать. Можно рефакторнуть и в сигнатурах задать возвращение опшинала, но потом
        }
        // Отдельно инсертим в свои таблицы жанры
        Set<GenreFilmEnum> genres = film.getGenres();
        for (GenreFilmEnum g : genres) {
            jdbcTemplate.update("INSERT INTO FILMORATE_FILM_GENRE (GENRE_ID, FILM_ID) " +
                    "VALUES ((SELECT GENRE_ID FROM FILMORATE_GENRE WHERE GENRE=?), ?)", g.toString(), index
            );
        }
        // И инсертим лайки, это же новые деньги
        Set<Integer> likes = film.getLikes();
        for (Integer i : likes) {
            jdbcTemplate.update("INSERT INTO FILMORATE_LIKE (FILM_ID, USER_ID) " +
                    "VALUES (?, ?)", index, i
            );
        }
        return getFilm(index);
    }

    @Override
    public Film delete(Integer id) {
        String sql = "delete from FILMORATE_FILM where FILM_ID = ?";
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
                "TITLE = '?', " +
                "DESCRIPTION = '?', " +
                "RELEASE_DATE = '?', " +
                "DURATION_MINUTES = ?, " +
                "RATING_MPA = (" +
                "SELECT RATING_ID FROM FILMORATE_MPA_RATING WHERE RATING = '?') " +
                "WHERE FILM_ID = ?";

        int patchRow = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate().format(LocalDateFormatter4FilmReleaseDate.getFormatter()),
                film.getDuration(),
                film.getMpaRating().toString(),
                film.getId()
        );

        Film out = getFilm(film.getId());
        if (patchRow == 1) {
            log.debug("Обновлен фильм с ИД " + out.getId());
        } else if (patchRow > 1) {
            log.error("Обновлено больше одного фильма по ИД" + film);
        } else if (out == null) {
            log.error("Из базы не вернулся фильм при обновлении фильма " + film.getId());
            throw new FilmNotFoundException("Не нашлось фильма после обновления");
        } else if (patchRow == 0) {
            log.error("Ни одного фильма по ИД" + film + "не обновилось");
            throw new FilmNotFoundException("Не нашлось фильма с таким ИД для обновления");
        }
        //Логирую всё что могу, хотя не думаю что это хорошо
        return out;
    }

    @Override
    public Collection<Film> films() {
        String sql = "select film.FILM_ID, film.TITLE, film.DESCRIPTION, film.RELEASE_DATE, film.DURATION_MINUTES," +
                "mpa.RATING from FILMORATE_FILM as film join FILMORATE_MPA_RATING as mpa" +
                " ON film.RATING_MPA = mpa.RATING_ID";
        List<Film> films = jdbcTemplate.query(sql, new FilmMapper());

        if (films.isEmpty()) {
            log.debug("Фильмов нет");
            throw new FilmNotFoundException("Фильмов нет");
        } else {
            log.debug("Найдено {} фильмов", films.size());
        }
        // Пока не знаю как в таком формате "всунуть" лайки и жанры в фильмы, только если дублировать код
        // Да и два сценария где это потребуется лучше сделать через отдельные методы.
        // Если потребуются фильмы по жанрам, проще выгрузку отдельную делать чем перебирать жанры всех фильмов
        // Фильмы лайкнутые конкретным пользователем тоже лучше отдельно выгружать
        return films;
    }

    @Override
    public Film getFilm(Integer id) {
        String sql = "SELECT film.FILM_ID, film.TITLE, film.DESCRIPTION, film.RELEASE_DATE, film.DURATION_MINUTES," +
                "mpa.RATING FROM FILMORATE_FILM AS film JOIN FILMORATE_MPA_RATING AS mpa" +
                " ON film.RATING_MPA = mpa.RATING_ID WHERE film.FILM_ID = ?";
        Film film = jdbcTemplate.queryForObject(sql, new FilmMapper(), id);
        //Основную работу делает ФилмМапер
        if (film == null) {
            log.info("Пользователь с идентификатором {} не найден.", id);
            throw new FilmNotFoundException("Что то пошло не так");
        } else {
            log.info("Найден фильм: {} {}", film.getId(), film.getName());
        }
        film.setGenres(getGenres(id));
        film.setUsersLikes(getLikes(id));
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

    private Set<Integer> getLikes(Integer filmId) {
        //Тут тоже всё просто
        String sql = "SELECT USER_ID FROM FILMORATE_LIKE WHERE FILM_ID = ?";
        List<Integer> filmLikes = jdbcTemplate.query(sql, ResultSet::getInt, filmId);
        if (filmLikes == null) {
            log.error("При получении лайков что-то пошло не так");
        }
        log.debug("Забрали лайки у фильма {}", filmId);
        return Set.copyOf(filmLikes);
    }

    private Set<GenreFilmEnum> getGenres(Integer filmId) {
        // Тут вроде всё просто
        String sql = "SELECT FG.GENRE FROM FILMORATE_FILM_GENRE AS FFG " +
                "JOIN FILMORATE_GENRE AS FG " +
                "ON FFG.GENRE_ID = FG.GENRE_ID " +
                "WHERE FFG.FILM_ID = ?";
        List<GenreFilmEnum> genres = this.jdbcTemplate.query(sql, (rs, rowNum) -> {
            return GenreFilmEnum.fromValue(rs.getString("GENRE"));
        }, filmId);
        if (genres == null) {
            log.error("При получении жанров что-то пошло не так");
        }
        if (genres.isEmpty()) {
            log.error("Жанров нет, и это ошибка так как такого не должно быть");
        }
        log.debug("Забрали жанры у фильма {}", filmId);
        return Set.copyOf(genres);
    }
}



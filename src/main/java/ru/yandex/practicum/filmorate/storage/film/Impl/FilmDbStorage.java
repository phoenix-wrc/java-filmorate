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
import ru.yandex.practicum.filmorate.model.film.GenresId;
import ru.yandex.practicum.filmorate.model.film.LocalDateFormatter4FilmReleaseDate;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Types;
import java.util.Arrays;
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
                "(TITLE, DESCRIPTION, RELEASE_DATE, DURATION_MINUTES, RATING_MPA) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        var preparedStatementCreatorFactory = new PreparedStatementCreatorFactory( //{
                sql, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.INTEGER
//            @Override
//            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
//                PreparedStatement ps = connection.prepareStatement(sql);
//                ps.setString(1, film.getName());
//                ps.setString(2, film.getDescription());
//                ps.setString(3,
//                        film.getReleaseDate().format(LocalDateFormatter4FilmReleaseDate.getFormatter()));
//                ps.setInt(4, film.getDuration());
//                ps.setInt(5, film.getMpa().getId());
//                return ps;
//            }
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
        log.debug(keyHolder.getKeys().toString());
        Integer index;
        try {
            index = keyHolder.getKeyAs(Integer.class);
        } catch (NullPointerException e) {
            log.error("Новый ИД из базы не вернулся, дальше всё не будет работать");
            return film; //Незнаю что возвращать. Можно рефакторнуть и в сигнатурах задать возвращение опшинала, но потом
        }
        // Отдельно инсертим в свои таблицы жанры
//        Set<GenresId> genres = film.getGenres();
//        for (GenresId g : genres) {
//            jdbcTemplate.update("INSERT INTO FILMORATE_FILM_GENRE (GENRE_ID, FILM_ID) " +
//                    "VALUES (?, ?)", g.getId(), index
//            );
//        }
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
        String sql = "SELECT film.FILM_ID, film.TITLE, film.DESCRIPTION, film.RELEASE_DATE, film.DURATION_MINUTES," +
                "film.RATING_MPA, mpa.RATING FROM FILMORATE_FILM AS film " +
                "JOIN FILMORATE_MPA_RATING AS mpa " +
                "ON film.RATING_MPA = mpa.RATING_ID";
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
//        film.setGenres(getGenres(id));
//        film.setUsersLikes(getLikes(id));
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
        List<Integer> filmLikes = jdbcTemplate.query(sql, (resultSet, columnIndex) ->
                resultSet.getInt("USER_ID"), filmId);
        if (filmLikes == null) {
            log.error("При получении лайков что-то пошло не так");
        }
        log.debug("Забрали лайки у фильма {}", filmId);
        return Set.copyOf(filmLikes);
    }

    private Set<GenresId> getGenres(Integer filmId) {
        // Тут вроде всё просто
        String sql = "SELECT FFG.GENRE_ID FROM FILMORATE_FILM_GENRE AS FFG " +
                "WHERE FFG.FILM_ID = ?";
        List<GenresId> genres = this.jdbcTemplate.query(sql, (rs, rowNum) ->
                new GenresId(rs.getInt("GENRE_ID")), filmId);
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



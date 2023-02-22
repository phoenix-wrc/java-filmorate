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
import ru.yandex.practicum.filmorate.model.film.LocalDateFormatter4FilmReleaseDate;
import ru.yandex.practicum.filmorate.storage.film.FilmGenresStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Types;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Component("FilmBDStorage")
@Qualifier("FilmBDStorage")
public class FilmDbStorage implements FilmStorage {
    @NonNull
    private final JdbcTemplate jdbcTemplate;
    private final FilmGenresStorage genresStorage;

    public FilmDbStorage(@NonNull JdbcTemplate jdbcTemplate,
                         @NonNull FilmGenresStorage genresStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genresStorage = genresStorage;
    }

    //Эти классы будут DAO — объектами доступа к данным
    @Override
    public Optional<Film> add(Film film) {
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
            return Optional.empty(); //Незнаю что возвращать. Можно рефакторнуть и в сигнатурах задать возвращение опшинала, но потом
        }
//         Отдельно инсертим в свои таблицы жанры
        genresStorage.setGenresToFilm(film.getGenres(), index);
        return getFilm(index);
    }

    @Override
    public Optional<Film> delete(Integer id) {
        String sql = "DELETE FROM FILMORATE_LIKE WHERE FILM_ID = ?; " +
                "DELETE FROM FILMORATE_FILM_GENRE WHERE FILM_ID = ?;" +
                "DELETE FROM FILMORATE_FILM WHERE FILM_ID = ?  ";
        //Пока так, можно переделать каскадом но нужно еще почитать доку к БД
        Optional<Film> out = getFilm(id);
        int deleteRow = jdbcTemplate.update(sql, id);
        if (deleteRow == 1) {
            log.debug("Удален фильм с ИД " + id);
        } else if (deleteRow > 1) {
            log.error("Удалилось больше одного фильма по ИД" + id);
        } else if (deleteRow == 0) {
            log.error("Ни одного фильма по ИД" + id + "не удалилось");
            return Optional.empty();
        }
        return out;
    }

    @Override
    public Optional<Film> patch(Film film) {
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

        return getFilm(film.getId());
    }

    @Override
    public Collection<Optional<Film>> films() {
        String sql = "SELECT film.FILM_ID, film.TITLE, film.DESCRIPTION, film.RELEASE_DATE, film.DURATION_MINUTES," +
                "film.RATING_MPA, mpa.RATING FROM FILMORATE_FILM AS film " +
                "JOIN FILMORATE_MPA_RATING AS mpa " +
                "ON film.RATING_MPA = mpa.RATING_ID";

        return jdbcTemplate.query(sql, new FilmMapper());
    }

    @Override
    public Optional<Film> getFilm(Integer id) {
        String sql = "SELECT film.FILM_ID, film.TITLE, film.DESCRIPTION, film.RELEASE_DATE, film.DURATION_MINUTES," +
                "film.RATING_MPA, mpa.RATING FROM FILMORATE_FILM AS film " +
                "JOIN FILMORATE_MPA_RATING AS mpa " +
                "ON film.RATING_MPA = mpa.RATING_ID " +
                "WHERE film.FILM_ID = ?";
        //Основную работу делает ФилмМапер
        return jdbcTemplate.queryForObject(sql, new FilmMapper(), id);
    }

    @Override
    public Optional<Integer> size() {
        // Тут проде всё просто, считаем уоличество записей целиком. Можно ограничится подсчетом Идешников
        String sql = "SELECT Count(FILM_ID) FROM FILMORATE_FILM";
        Integer filmCount = jdbcTemplate.queryForObject(sql, Integer.class);
        log.info("Найдено фильмов: {}", filmCount);
        return Optional.ofNullable(filmCount);
    }
}



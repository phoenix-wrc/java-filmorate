package ru.yandex.practicum.filmorate.storage.film.Impl;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmGenresStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Slf4j
@Component("FilmGenresStorageImpl")
@Qualifier("FilmGenresStorageImpl")
public class FilmGenresStorageImpl implements FilmGenresStorage {
    @NonNull
    private final JdbcTemplate jdbcTemplate;

    public FilmGenresStorageImpl(@NonNull JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Optional<Genre>> genres() {
        //Тут тоже всё просто
        String sql = "SELECT * FROM FILMORATE_GENRE ORDER BY GENRE_ID";
        List<Optional<Genre>> genres = jdbcTemplate.query(sql, this::getGenre);
        //Пишут что метод квери не может возвращать нулы так что проверки не требуется
        log.debug("Забрали все жанры");
        return genres;
    }

    @Override
    public Optional<Genre> genre(Integer id) {

        String sql = "SELECT * FROM FILMORATE_GENRE " +
                "WHERE GENRE_ID = ? " +
                "ORDER BY GENRE_ID";
        return jdbcTemplate.queryForObject(sql, this::getGenre, id);
    }

    public List<Optional<Genre>> getGenres(Integer filmId) {
        // Тут вроде всё просто
        String sql = "SELECT FG.GENRE_ID, FG.GENRE " +
                "FROM FILMORATE_FILM_GENRE AS FFG " +
                "JOIN FILMORATE_GENRE AS FG on FG.GENRE_ID = FFG.GENRE_ID " +
                "WHERE FFG.FILM_ID = ? " +
                "ORDER BY FFG.GENRE_ID";
        List<Optional<Genre>> genres = this.jdbcTemplate.query(sql, this::getGenre, filmId);
        //Пишут что метод квери не может возвращать нулы так что проверки не требуется
        log.debug("Забрали жанры у фильма {}", filmId);
        return genres;
    }

    public void updateGenresToFilm(List<Genre> genres, Integer id) {
        jdbcTemplate.update("DELETE FROM FILMORATE_FILM_GENRE " +
                "WHERE FILM_ID = ?", id);
        setGenresToFilm(genres, id);
    }

    @Override
    public void setGenresToFilm(List<Genre> genres, Integer filmId) {
        if (genres != null && !genres.isEmpty()) {
            Set<Genre> internalSet = new HashSet<>(genres);
            for (Genre g : internalSet) {
                jdbcTemplate.update("INSERT INTO FILMORATE_FILM_GENRE (GENRE_ID, FILM_ID) " +
                        "VALUES (?, ?)", g.getId(), filmId
                );
            }
        }
    }

    private Optional<Genre> getGenre(ResultSet rs, int rowNum) throws SQLException {
        return Optional.of(new Genre(
                rs.getInt("GENRE_ID"), rs.getString("GENRE")));
    }
}

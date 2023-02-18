package ru.yandex.practicum.filmorate.storage.film.Impl;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmGenresStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


@Slf4j
@Component
public class FilmGenresStorageImpl implements FilmGenresStorage {
    @NonNull
    private final JdbcTemplate jdbcTemplate;

    public FilmGenresStorageImpl(@NonNull JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> genres() {
        //Тут тоже всё просто
        String sql = "SELECT * FROM FILMORATE_MPA_RATING";
        List<Genre> genres = jdbcTemplate.query(sql, this::getGenre);
        //Пишут что метод квери не может возвращать нулы так что проверки не требуется
        log.debug("Забрали все жанры");
        return genres;
    }

    @Override
    public Genre genre(Integer id) {

        String sql = "SELECT * FROM FILMORATE_GENRE " +
                "WHERE GENRE_ID = ?";
        Genre genre = jdbcTemplate.queryForObject(sql, this::getGenre, id);
        if (genre == null) {
            log.info("Рэйтинг с идентификатором {} не найден.", id);
            throw new FilmNotFoundException("Что то пошло не так");
        } else {
            log.info("Найден жанр: {} {}", genre.getId(), genre.getName());
        }
        return genre;
    }


    private Genre getGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(
                rs.getInt("GENRE_ID"), rs.getString("GENRE"));
    }
}

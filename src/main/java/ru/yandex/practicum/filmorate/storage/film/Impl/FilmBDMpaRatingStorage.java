package ru.yandex.practicum.filmorate.storage.film.Impl;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmMpaRatingStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component("FilmBDMpaRatingStorage")
@Qualifier("FilmBDMpaRatingStorage")
public class FilmBDMpaRatingStorage implements FilmMpaRatingStorage {
    @NonNull
    private final JdbcTemplate jdbcTemplate;

    public FilmBDMpaRatingStorage(@NonNull JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Optional<MpaRating>> ratings() {
        //Тут тоже всё просто
        String sql = "SELECT RATING_ID, RATING FROM FILMORATE_MPA_RATING " +
                " ORDER BY RATING_ID ";
        try {
            List<Optional<MpaRating>> ratings = jdbcTemplate.query(sql, this::getRating);
            log.debug("Забрали все рэйтинги");
            return ratings;
        } catch (BadSqlGrammarException e) {
            log.error("Проблемы с базой данных: \n" + e.getMessage());
            return Collections.emptyList();
        }
        //Пишут что метод квери не может возвращать нулы так что проверки не требуется
    }

    @Override
    public Optional<MpaRating> rating(Integer id) {
        String sql = "SELECT * FROM FILMORATE_MPA_RATING " +
                "WHERE RATING_ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, this::getRating, id);
        } catch (IllegalStateException | EmptyResultDataAccessException e) {
            // Эти два лучше ловить по месту т.к. они связаны с БД. Можно их разделить т.к.
            // IllegalStateException - возникает при отсутствии инициализации баз
            // EmptyResultDataAccessException - возникает при отсутсвии данных
            log.error("Ошибка при запросе рэйтинга с ИД {}: {}", id, e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<MpaRating> getRating(ResultSet rs, int rowNum) throws SQLException {
        return Optional.of(new MpaRating(
                rs.getInt("RATING_ID"), rs.getString("RATING")));
    }
}

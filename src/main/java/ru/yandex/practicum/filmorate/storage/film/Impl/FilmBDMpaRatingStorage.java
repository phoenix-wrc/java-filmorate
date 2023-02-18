package ru.yandex.practicum.filmorate.storage.film.Impl;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.film.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmMpaRatingStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class FilmBDMpaRatingStorage implements FilmMpaRatingStorage {
    @NonNull
    private final JdbcTemplate jdbcTemplate;

    public FilmBDMpaRatingStorage(@NonNull JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<MpaRating> ratings() {
        //Тут тоже всё просто
        String sql = "SELECT RATING_ID, RATING FROM FILMORATE_MPA_RATING";
        List<MpaRating> ratings = jdbcTemplate.query(sql, this::getRating);
        //Пишут что метод квери не может возвращать нулы так что проверки не требуется
        log.debug("Забрали все рэйтинги");
        return ratings;
    }

    @Override
    public MpaRating rating(Integer id) {
        String sql = "SELECT * FROM FILMORATE_MPA_RATING " +
                "WHERE RATING_ID = ?";
        MpaRating rating = jdbcTemplate.queryForObject(sql, this::getRating, id);
        if (rating == null) {
            log.info("Рэйтинг с идентификатором {} не найден.", id);
            throw new FilmNotFoundException("Что то пошло не так");
        } else {
            log.info("Найден рейтинг: {} {}", rating.getId(), rating.getName());
        }
        return rating;
    }

    private MpaRating getRating(ResultSet rs, int rowNum) throws SQLException {
        return new MpaRating(
                rs.getInt("RATING_ID"), rs.getString("RATING"));
    }
}

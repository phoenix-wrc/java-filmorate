package ru.yandex.practicum.filmorate.model.film;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.film.enums.MpaRatingEnum;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class FilmMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getInt("FILM_ID"))
                .name(rs.getString("TITLE"))
                .description(rs.getString("DESCRIPTION"))
                .releaseDate(LocalDate.parse(rs.getString("RELEASE_DATE"),
                        LocalDateFormatter4FilmReleaseDate.getFormatter()))
                .duration(rs.getInt("DURATION_MINUTES"))
                .mpaRating(MpaRatingEnum.valueOf(rs.getString("RATING")))
                .build();
    }
}

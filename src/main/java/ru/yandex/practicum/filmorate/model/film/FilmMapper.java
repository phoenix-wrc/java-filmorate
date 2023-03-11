package ru.yandex.practicum.filmorate.model.film;

import lombok.Data;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.LocalDateFormatter4Date;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

@Data
public class FilmMapper implements RowMapper<Optional<Film>> {

    @Override
    public Optional<Film> mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Optional.of(Film.builder()
                .id(rs.getInt("FILM_ID"))
                .name(rs.getString("TITLE"))
                .description(rs.getString("DESCRIPTION"))
                .releaseDate(LocalDate.parse(rs.getString("RELEASE_DATE"),
                        LocalDateFormatter4Date.getFormatter()))
                .duration(rs.getInt("DURATION_MINUTES"))
                .mpa(new MpaRating(rs.getInt("RATING_MPA"), rs.getString("RATING")))
                .build());
    }
}

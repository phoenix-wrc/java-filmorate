package ru.yandex.practicum.filmorate.model.user;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.film.LocalDateFormatter4FilmReleaseDate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

public class UserMapper implements RowMapper<Optional<User>> {

    @Override
    public Optional<User> mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Optional.of(User.builder()
                .id(rs.getInt("USER_ID"))
                .login(rs.getString("USER_LOGIN"))
                .name(rs.getString("NAME"))
                .birthday(LocalDate.parse(rs.getString("BIRTHDAY"),
                        LocalDateFormatter4FilmReleaseDate.getFormatter()))
                .email(rs.getString("EMAIL"))
                .build());
    }
}

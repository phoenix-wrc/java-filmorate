package ru.yandex.practicum.filmorate.model.film;

import java.time.format.DateTimeFormatter;

public class LocalDateFormatter4FilmReleaseDate {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static DateTimeFormatter getFormatter() {
        return formatter;
    }
}

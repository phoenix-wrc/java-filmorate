package ru.yandex.practicum.filmorate.model;

import java.time.format.DateTimeFormatter;

public class LocalDateFormatter4Date {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static DateTimeFormatter getFormatter() {
        return formatter;
    }
}

package ru.yandex.practicum.filmorate.model.film.enums;

public enum GenreFilmEnum {
    COMEDY("Комедия"),
    DRAMA("Драма"),
    CARTOON("Мультфильм"),
    THRILLER("Триллер"),
    DOCUMENTARY("Документальный"),
    ACTION("Боевик");

    private final String name;

    GenreFilmEnum(String name) {
        this.name = name;
    }

    public static GenreFilmEnum fromValue(String v) {
        for (GenreFilmEnum c : GenreFilmEnum.values()) {
            if (c.name.equals(v)) {
                return c;
            }
        }
        return null;
    }

    public boolean equals(String name) {
        return this.toString().equals(name);
    }
}

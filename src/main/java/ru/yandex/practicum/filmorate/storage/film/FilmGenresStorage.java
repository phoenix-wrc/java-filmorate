package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.film.Genre;

import java.util.List;

public interface FilmGenresStorage {
    List<Genre> genres();

    Genre genre(Integer id);
}

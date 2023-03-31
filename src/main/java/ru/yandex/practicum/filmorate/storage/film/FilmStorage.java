package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Optional<Film> add(Film film);

    Optional<Film> delete(Integer id);

    Optional<Film> patch(Film film);

    Collection<Optional<Film>> films();

    Optional<Film> getFilm(Integer id);

    Optional<Integer> size();
}

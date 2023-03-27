package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.film.Genre;

import java.util.List;
import java.util.Optional;

public interface FilmGenresStorage {
    List<Optional<Genre>> genres();

    Optional<Genre> genre(Integer id);

    List<Optional<Genre>> getGenres(Integer filmId);

    Boolean setGenresToFilm(List<Genre> genres, Integer id);

    Boolean updateGenresToFilm(List<Genre> genres, Integer id);
}

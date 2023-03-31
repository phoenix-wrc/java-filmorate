package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FilmLikeStorage {

    Set<Optional<Integer>> getLikes(Integer filmId);

    boolean addLike(Integer filmId, Integer userId);

    boolean removeLike(Integer filmId, Integer userId);

    List<Optional<Film>> getTopFilms(Integer count);
}

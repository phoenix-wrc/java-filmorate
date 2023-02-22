package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.film.MpaRating;

import java.util.List;
import java.util.Optional;

public interface FilmMpaRatingStorage {
    List<Optional<MpaRating>> ratings();

    Optional<MpaRating> rating(Integer id);
}

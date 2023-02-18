package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.film.MpaRating;

import java.util.List;

public interface FilmMpaRatingStorage {
    List<MpaRating> ratings();

    MpaRating rating(Integer id);
}

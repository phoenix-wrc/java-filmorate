package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.Collection;

public interface FilmStorage {
	Film add(Film film);

	Film delete(Integer id);

	Film patch(Film film);

	Collection<Film> films();

	Film getFilm(Integer id);

	Integer size();
}

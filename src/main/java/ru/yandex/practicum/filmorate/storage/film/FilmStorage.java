package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

@Component
public interface FilmStorage {
	Film add(Film film);
	Film delete(Integer id);
	Film patch(Film film);

	Collection<Film> films();

	Film getFilm(Integer id);

	Integer size();
}

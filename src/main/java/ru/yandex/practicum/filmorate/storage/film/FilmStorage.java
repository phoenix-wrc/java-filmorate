package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Component
public interface FilmStorage {
	Film add(Film film);
	Film delete(Film film);
	Film patch(Film film);

	List<Film> films();

	Film getFilm(Integer id);
}

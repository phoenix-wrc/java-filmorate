package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

	private Integer currentFilmId;
	private Map<Integer, Film> films;

	public InMemoryFilmStorage() {
		currentFilmId = 1;
		films = new HashMap<>();

	}

	@Override
	public Film add(Film film) {
		//Как я понимаю что бы всё заработало нужно сделать так, а потом нам объяснят как всё сделать правильно))
		if (film.getId() == null) {
			film = new Film(getNextId(), film.getName(), film.getDescription(), film.getReleaseDate(),
					film.getDuration());
		}
		return films.put(film.getId(), film);
	}

	@Override
	public Film delete(Film film) {
		return films.remove(film.getId());
	}

	@Override
	public Film patch(Film film) {
		if (films.containsKey(film.getId())) {
			return films.put(film.getId(), film);
		} else {
			throw new ValidationException("Такого фильма не было");
		}
	}

	@Override
	public List<Film> films() {
		return (List<Film>) films.values();
	}

	@Override
	public Film getFilm(Integer id) {
		return films.get(id);
	}

	public Integer size() {
		return films.size();
	}

	private Integer getNextId() {
		return currentFilmId++;
	}
}

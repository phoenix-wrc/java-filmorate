package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
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
		} else if (films.containsKey(film.getId())) {
			throw new ValidationException("Фильм с таким ИД уже есть");
		} else {
			throw new ValidationException("Зачем передал фильм с ИД?");
		}
		Film out = films.put(film.getId(), film);
		if (out != null) {
			return out;
		}
		return film;
	}

	@Override
	public Film delete(Integer id) {
		return films.remove(id);
	}

	@Override
	public Film patch(Film film) {
		Film previousFilm;
		if (films.containsKey(film.getId())) {
			previousFilm = films.put(film.getId(), film);
		} else {
			throw new FilmNotFoundException("Такого фильма не было");
		}
		return film;
	}

	@Override
	public Collection<Film> films() {
		return films.values();
	}

	@Override
	public Film getFilm(Integer id) {
		Film out = films.get(id);
		if(out == null) {
			throw new FilmNotFoundException("Такого фильма не было");
		}
		return out;
	}

	public Integer size() {
		return films.size();
	}

	private Integer getNextId() {
		return currentFilmId++;
	}
}

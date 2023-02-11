package ru.yandex.practicum.filmorate.storage.film.Impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

	private Integer currentFilmId; //Счетчик ИДишников фильма
	private final Map<Integer, Film> films; // Временное хранилище фильмы

	public InMemoryFilmStorage() {
		currentFilmId = 1;
		films = new HashMap<>();
	}

	@Override
	public Film add(Film film) {
		//Как я понимаю что бы всё заработало нужно сделать так, а потом нам объяснят как всё сделать правильно))
		if (film.getId() == null) {
			film = Film.builder()
					.id(getNextId())
					.name(film.getName())
					.description(film.getDescription())
					.releaseDate(film.getReleaseDate())
					.duration(film.getDuration())
					.mpa(film.getMpa())
					.build();
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
		// Просто удаляем если возможно
		if (!films.containsKey(id)) {
			throw new FilmNotFoundException("Такого фильма не было и так");
		}
		return films.remove(id);
	}

	@Override
	public Film patch(Film film) {
		// на всякий случай сохраняем старый фильм, что бы что нить проверить))
		Film previousFilm;
		if (films.containsKey(film.getId())) {
			previousFilm = films.put(film.getId(), film);
		} else {
			throw new FilmNotFoundException("Такого фильма не было");
		}
		//Но возвращать нужно новый фильм
		return film;
	}

	@Override
	public Collection<Film> films() {
		//Просто возвращаем все значения.
		return films.values();
	}

	@Override
	public Film getFilm(Integer id) {
		//Давайте так возвращать
		Film out = null;
		if (films.containsKey(id)) {
			out = films.get(id);
		}
		if (out == null) {
			throw new FilmNotFoundException("Такого фильма не было");
		}
		return out;
	}

	public Integer size() {
		// Вспомогательный метод, пусть будет
		return films.size();
	}

	private Integer getNextId() {
		//внутренний метод нумерации
		return currentFilmId++;
	}
}

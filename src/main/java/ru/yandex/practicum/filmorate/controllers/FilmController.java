package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
	private Integer currentFilmId = 1;
	private final Map<Integer, Film> films = new HashMap<>();

	@PostMapping
	public Film createFilm(@Valid @RequestBody Film film) throws ValidationException {
		log.debug("Создается фильм: {}", film);
		//Как я понимаю что бы всё заработало нужно сделать так, а потом нам объяснят как всё сделать правильно))
		if(film.getId() == null) {
			film = new Film(currentFilmId, film.getName(), film.getDescription(), film.getReleaseDate(),
					film.getDuration());
			currentFilmId++;
		}
		films.put(film.getId(), film);
		return film;
	}

	@PutMapping
	public Film update(@Valid @RequestBody Film film) {
		log.debug("Обновляется пользователь: {}", film);
		if (films.containsKey(film.getId())) {
			films.put(film.getId(), film);
		} else {
			throw new ValidationException("Такого фильма не было");
		}
		return film;
	}

	@GetMapping
	public List<Film> films() {
		log.debug("Количество фильмов перед добавлением: {}", films.size());
		return List.of(films.values().toArray(new Film[0]));
	}
}

package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
	FilmService  service;
	InMemoryFilmStorage storage = new InMemoryFilmStorage();

	@Autowired
	public FilmController(FilmService service) {
		this.service = service;
	}

	@PostMapping
	public Film createFilm(@Valid @RequestBody Film film) throws ValidationException {
		log.debug("Создается фильм: {}", film);
		return storage.add(film);
	}

	@PutMapping
	public Film update(@Valid @RequestBody Film film) {
		log.debug("Обновляется пользователь: {}", film);
		return storage.patch(film);
	}

	@GetMapping
	public List<Film> films() {
		log.debug("Количество фильмов перед добавлением: {}", films().size());
		return storage.films();
	}
}

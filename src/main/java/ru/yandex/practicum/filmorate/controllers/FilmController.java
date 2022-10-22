package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
	private final FilmService  service;

	@Autowired
	public FilmController(FilmService service) {
		this.service = service;
	}

//
//	PUT /films/{id}/like/{userId} — пользователь ставит лайк фильму.
//	DELETE /films/{id}/like/{userId} — пользователь удаляет лайк.
//			GET /films/popular?count={count} — возвращает список из
//	первых count фильмов по количеству лайков. Если значение параметра count не задано, верните первые 10.

	@PutMapping("/{id}/like/{userId}")
	public boolean likeToFilm(@PathVariable Integer id, @PathVariable Integer userId) {
		log.debug("\nПользователь {} ставит лайк фильму {}", userId, id);
		return service.addLikeToFilm(id, userId);
	}

	@DeleteMapping("/{id}/like/{userId}")
	public boolean deleteLikeFromFilm(@PathVariable Integer id, @PathVariable Integer userId) {
		log.debug("\nПользователь {} убирает лайк с фильма {}", userId, id);
		return service.removeLikeFromFilm(id, userId);
	}

	@GetMapping("/popular")
	public List<Film> bestFilms(@RequestParam(required = false, defaultValue = "10") Integer count) {
		log.debug("Пользователь запросил {} лучших фильмов", count);
		List<Film> out = service.getTopFilms(count);
		return out;
	}


	@PostMapping
	public Film createFilm(@Valid @RequestBody Film film) throws ValidationException {
		log.debug("\nСоздается фильм: {}", film);
		return service.add(film);
	}

	@PutMapping
	public Film update(@Valid @RequestBody Film film) {
		log.debug("\nОбновляется пользователь: {}", film);
		return service.patch(film);
	}

	@GetMapping
	public List<Film> films() {
		log.debug("\nОтдаем все фильмы. Количество фильмов: {}", service.size());
		return new ArrayList<>(service.films());
	}

	@DeleteMapping("/{id}")
	public Film deleteFilm(@PathVariable Integer id) {
		log.debug("\nУдаляем фильм: {}", service.size());
		return service.delete(id);
	}

	@GetMapping("/{id}")
	public Film film(@PathVariable Integer id) {
		Film out = service.getFilm(id);
		log.debug("\nОтдаем фильм. {}", out);
		return out;
	}
}

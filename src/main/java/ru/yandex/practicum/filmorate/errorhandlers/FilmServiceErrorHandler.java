package ru.yandex.practicum.filmorate.errorhandlers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.LikeNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import java.util.Map;

@RestControllerAdvice(assignableTypes = FilmController.class)
public class FilmServiceErrorHandler {

	@ExceptionHandler(value = ValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, String> validationException(final ValidationException e) {
		return Map.of("Данные фильма указаны не верно", e.getMessage());
	}

	@ExceptionHandler(value = FilmNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public Map<String, String> notFoundException(final FilmNotFoundException e) {
		return Map.of("Фильм не найден", e.getMessage());
	}

	@ExceptionHandler(value = LikeNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public Map<String, String> notFoundLikeException(final LikeNotFoundException e) {
		return Map.of("Лайк не найден", e.getMessage());
	}
}

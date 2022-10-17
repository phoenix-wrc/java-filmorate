package ru.yandex.practicum.filmorate.errorHandlers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.Map;

@RestControllerAdvice(assignableTypes = UserController.class)
public class UserServiceErrorHandler {

	@ExceptionHandler(value = ValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, String> validationException(final ValidationException e) {
		return Map.of("Данные пользователя указаны не верно", e.getMessage());
	}

	@ExceptionHandler(value = UserNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public Map<String, String> notFoundException(final UserNotFoundException e) {
		return Map.of("Пользователь не найден", e.getMessage());
	}
}
package ru.yandex.practicum.filmorate.errorhandlers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;

import java.util.Map;

@RestControllerAdvice(basePackages = "ru.yandex.practicum.filmorate.controllers")
public class GenreServiceErrorHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = GenreNotFoundException.class)
    public Map<String, String> notFoundException(final GenreNotFoundException e) {
        return Map.of("Такого жанра нет", e.getMessage());
    }
}

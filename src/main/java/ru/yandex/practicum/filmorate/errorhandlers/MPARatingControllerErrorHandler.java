package ru.yandex.practicum.filmorate.errorhandlers;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.RatingNotFoundException;

import java.util.Map;

@RestControllerAdvice(basePackages = "ru.yandex.practicum.filmorate.controllers")
public class MPARatingControllerErrorHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = RatingNotFoundException.class)
    public Map<String, String> notFoundException(final RatingNotFoundException e) {
        return Map.of("Такого рэйтинга нет", e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = EmptyResultDataAccessException.class)
    public Map<String, String> incorrectResultSize(final EmptyResultDataAccessException e) {
        return Map.of("Такого рэйтинга нет", "Incorrect result size: expected 1, actual 0");
    }
}

package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.service.film.GenreService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreService service;

    @Autowired
    public GenreController(GenreService service) {
        this.service = service;
    }


    @GetMapping()
    public List<Genre> genres() {
        log.debug("Пользователь запросил список всех жанров");
        return service.genres();
    }

    @GetMapping("/{id}")
    public Genre genre(@PathVariable Integer id) {
        Genre out = service.genre(id);
        log.debug("Отдаем жанр {}", out);
        return out;
    }
}

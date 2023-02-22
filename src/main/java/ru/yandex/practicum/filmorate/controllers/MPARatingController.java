package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.film.MpaRating;
import ru.yandex.practicum.filmorate.service.film.MPARatingService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class MPARatingController {
    private final MPARatingService service;

    @Autowired
    public MPARatingController(MPARatingService service) {
        this.service = service;
    }

    @GetMapping()
    public List<MpaRating> ratings() {
        log.debug("Пользователь запросил список всех рейтингов MPA");
        return service.ratings();
    }

    @GetMapping("/{id}")
    public MpaRating rating(@PathVariable Integer id) {
        MpaRating out = service.rating(id);
        log.debug("Отдаем рейтинг {}", out);
        return out;
    }

}

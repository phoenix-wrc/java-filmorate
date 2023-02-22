package ru.yandex.practicum.filmorate.service.film;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.RatingNotFoundException;
import ru.yandex.practicum.filmorate.model.film.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmMpaRatingStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MPARatingService {
    private final FilmMpaRatingStorage storage;

    @Autowired
    public MPARatingService(@NonNull FilmMpaRatingStorage storage) {
        this.storage = storage;
    }


    public List<MpaRating> ratings() {
        return storage.ratings().stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public MpaRating rating(Integer id) {
        return storage.rating(id).orElseThrow(() ->
                new RatingNotFoundException("Рэйтинг с таким ид не найден: " + id));
    }
}

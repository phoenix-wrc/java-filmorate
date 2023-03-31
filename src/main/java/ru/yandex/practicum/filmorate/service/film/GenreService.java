package ru.yandex.practicum.filmorate.service.film;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmGenresStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GenreService {
    private final FilmGenresStorage genresStorage;

    @Autowired
    public GenreService(@NonNull FilmGenresStorage genresStorage) {
        this.genresStorage = genresStorage;
    }

    public List<Genre> genres() {
        return genresStorage.genres().stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

    }

    public Genre genre(Integer id) {
        return genresStorage.genre(id).orElseThrow(() -> new GenreNotFoundException("Нет жанра с ID " + id));
    }
}

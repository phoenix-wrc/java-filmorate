package ru.yandex.practicum.filmorate.storage.film.Impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.Set;


@Slf4j
@Component("FilmLikeBDStorage")
@Qualifier("FilmLikeBDStorage")
public class FilmLikeBDStorage implements FilmLikeStorage {
    FilmStorage storage;

    public FilmLikeBDStorage(@Qualifier("FilmBDStorage") FilmStorage storage) {
        this.storage = storage;
    }

    @Override
    public Set<Integer> getLikes(Integer filmId) {
        return null;
    }

    @Override
    public boolean addLike(Integer filmId, Integer userId) {
        return true;
    }

    @Override
    public boolean removeLike(Integer filmId, Integer userId) {
        return true;
    }

    @Override
    public List<Film> getTopFilms(Integer count) {
        return null;
    }
}

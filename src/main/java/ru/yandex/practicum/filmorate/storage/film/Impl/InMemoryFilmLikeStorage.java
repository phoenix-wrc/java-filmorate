package ru.yandex.practicum.filmorate.storage.film.Impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.LikeNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Component
public class InMemoryFilmLikeStorage implements FilmLikeStorage {
    private final FilmStorage storage;
    private Map<Integer, Set<Integer>> likes;

    public InMemoryFilmLikeStorage(@Qualifier("inMemoryFilmStorage") FilmStorage storage) {
        this.storage = storage;
    }


    @Override
    public Set<Optional<Integer>> getLikes(Integer filmId) {
        if (likes.containsKey(filmId)) {
            return Set.copyOf(likes.get(filmId)).stream()
                    .map(Optional::of)
                    .collect(Collectors.toSet());
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public boolean addLike(Integer filmId, Integer userId) {
        if (likes.containsKey(filmId)) {
            return likes.get(filmId).add(userId);
        } else {
            var returning = Optional.ofNullable(likes.put(filmId, new HashSet<>(Set.of(userId))));
            if (returning.isPresent() && returning.get().contains(userId)) {
                return true;
            } else {
                throw new FilmNotFoundException("Непонятная ошибка");
            }
        }
    }

    @Override
    public boolean removeLike(Integer filmId, Integer userId) {
        boolean isRemoved;
        if (likes.containsKey(filmId)) {
            isRemoved = likes.get(filmId).remove(userId);
        } else {
            isRemoved = false;
        }
        if (!isRemoved) {
            throw new LikeNotFoundException("Лайк от пользователя " + userId + " не найден");
        }
        return true;
    }

    @Override
    public List<Optional<Film>> getTopFilms(Integer count) {
        // Так как метод завязан на лайки логично хранить его тут
        List<Optional<Film>> out;
        if (count > 0) {
            out = likes.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size()))
                    .entrySet().stream()
                    .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                    .limit(count)
                    .map((Map.Entry<Integer, Integer> id) -> storage.getFilm(id.getKey()))
                    .collect(Collectors.toList());
        } else {
            throw new ValidationException("Параметр должен быть больше ноля");
        }
        return out;
    }


    public Integer getCountOfLikes(Integer filmId) {
        return likes.get(filmId).size();
    }
}

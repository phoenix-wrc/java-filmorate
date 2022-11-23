package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
	private final FilmStorage storage;

	@Autowired
	public FilmService(FilmStorage storage) {
		this.storage = storage;
	}

	public boolean addLikeToFilm(Integer idFilm, Integer userId) {
		Film f = storage.getFilm(idFilm);
		return f.addLike(userId);
	}

	public boolean removeLikeFromFilm(Integer idFilm, Integer userId) {
		Film f = storage.getFilm(idFilm);
		return f.removeLike(userId);
	}

	public List<Film> getTopFilms(Integer count) {
		List<Film> out;
		if (count > 0) {
			out = storage.films().stream().sorted(Comparator.comparing(Film::getCountOfLikes)
					.reversed()).limit(count).collect(Collectors.toList());
		} else {
			throw new ValidationException("Параметр должен быть больше ноля");
		}
		return out;
	}

	public Film add(Film film) {
		return storage.add(film);
	}

	public Film delete(Integer id) {
		return storage.delete(id);
	}

	public Film patch(Film film) {
		return storage.patch(film);
	}

	public List<Film> films() {
		return new ArrayList<>(storage.films());
	}

	public Integer size() {
		return storage.size();
	}

	public Film getFilm(Integer id) {
		return storage.getFilm(id);
	}
}

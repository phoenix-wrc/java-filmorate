package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class FilmService {
	private final FilmStorage storage;
	private final FilmLikeStorage likeStorage;

	@Autowired
	public FilmService(@Qualifier("FilmBDStorage") FilmStorage storage,
					   @Qualifier("FilmLikeBDStorage") FilmLikeStorage likeStorage) {
		this.storage = storage;
		this.likeStorage = likeStorage;
	}

	public boolean addLikeToFilm(Integer idFilm, Integer userId) {
		return likeStorage.addLike(idFilm, userId);
	}

	public boolean removeLikeFromFilm(Integer idFilm, Integer userId) {
		return likeStorage.removeLike(idFilm, userId);
	}

	public List<Film> getTopFilms(Integer count) {
		return likeStorage.getTopFilms(count);
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

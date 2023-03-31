package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmGenresStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
	private final FilmStorage storage;
	private final FilmLikeStorage likeStorage;
	private final FilmGenresStorage genresStorage;

	@Autowired
	public FilmService(@Qualifier("FilmBDStorage") FilmStorage storage,
					   @Qualifier("FilmLikeBDStorage") FilmLikeStorage likeStorage,
					   FilmGenresStorage genresStorage) {
		this.storage = storage;
		this.likeStorage = likeStorage;
		this.genresStorage = genresStorage;
	}

	public boolean addLikeToFilm(Integer idFilm, Integer userId) {
		return likeStorage.addLike(idFilm, userId);
	}

	public boolean removeLikeFromFilm(Integer idFilm, Integer userId) {
		return likeStorage.removeLike(idFilm, userId);
	}

	public List<Film> getTopFilms(Integer count) {
		var out = likeStorage.getTopFilms(count).stream()
				.filter(Optional::isPresent)
				.map(Optional::get)
				.peek(f -> f.setGenres(genresStorage.getGenres(f.getId()).stream()
						.filter(Optional::isPresent)
						.map(Optional::get)
						.collect(Collectors.toList())))
				.collect(Collectors.toList());

		if (out.isEmpty()) {
			throw new FilmNotFoundException("Фильмов нет совсем, даже для топа");
		}
		return out;
	}

	public Film add(Film film) {
		Film out = storage.add(film).orElseThrow(() -> {
					log.debug("Ошибка при добавлении фильма {}", film.getName());
					return new FilmNotFoundException("Ошибка при добавлении нового фильма");
				}
		);
//		out.setGenres(genresStorage.getGenres(film.getId()).stream()
//				.filter(Optional::isPresent)
//				.map(Optional::get)
//				.collect(Collectors.toSet()));
		out.setGenres(genresStorage.getGenres(out.getId()).stream()
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toList())
		);
		return out;
	}

	public Film delete(Integer id) {
		return storage.delete(id).orElseThrow(() -> {
					log.debug("Ошибка при удалении фильма с ид {}", id);
					return new FilmNotFoundException("Ошибка при удалении фильма " + id);
				}
		); // Жанры должны удаляться какадом
	}

	public Film patch(Film film) {
		var out = storage.patch(film).orElseThrow(() ->
				new FilmNotFoundException("Не нашлось фильма с таким ИД для обновления")
		);
		// Что бы поработать с жанрами нужно получается их сначало удалить а потом снова добавить.
		// В теории можно использовать мердж, но не понятно тогда как удалить если жанр убрали.
		genresStorage.updateGenresToFilm(film.getGenres(), out.getId());
		out.setGenres(genresStorage.getGenres(film.getId()).stream()
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toList())
		);
		return out;
	}

	public List<Film> films() {
		var out = storage.films().stream()
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toList());

		if (out.isEmpty()) {
			log.debug("Фильмов нет");
		} else {
			log.debug("Найдено {} фильмов", out.size());
		}
		// Отдельно заливаем жанры т.к. не знаю как по другому можно
		for (Film f : out) {
			f.setGenres(genresStorage.getGenres(f.getId())
					.stream()
					.filter(Optional::isPresent)
					.map(Optional::get)
					.collect(Collectors.toList()));
		}
		return out;
	}

	public Integer size() {
		return storage.size().orElseThrow(() ->
				new FilmNotFoundException("Ошибка при измерении количества фильмов")
		);
	}

	public Film getFilm(Integer id) {
		var out = storage.getFilm(id).orElseThrow(() ->
				new FilmNotFoundException("Ошибка при получении нового фильма")
		);
		out.setGenres(genresStorage.getGenres(id).stream()
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toList()));
		return out;
	}
}

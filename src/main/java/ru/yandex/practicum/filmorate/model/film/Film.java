package ru.yandex.practicum.filmorate.model.film;

import lombok.Data;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.LikeNotFoundException;
import ru.yandex.practicum.filmorate.validators.FilmReleaseDate;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {

	private final Integer id;

	@NotNull(message = "Не задалось имя")
	@NotEmpty(message = "Имя не должно быть из одних пробелов")
	@NotBlank(message = "Имя не должно быть пустым")
	private final String name;

	@Size(max = 200, message = "Длинна описания до 200 символов")
	private final String description;

	@FilmReleaseDate(message = "Дата релиза не может быть ранее 28.12.1895")
	private final LocalDate releaseDate;

	@Min(1)
	private final Integer duration;
	private Set<Integer> UsersLikes = new HashSet<>();
	private Set<GenreFilmEnum> genres = new HashSet<>();

	public Set<Integer> getLikes() {
		return new HashSet<>(UsersLikes);
	}

	public boolean addLike(Integer userId) {
		return UsersLikes.add(userId);
	}

	public boolean removeLike(Integer userId) {
		boolean isRemoved = UsersLikes.remove(userId);
		if (!isRemoved) {
			throw new LikeNotFoundException("Лайк от пользователя " + userId + " не найден");
		}
		return isRemoved;
	}

	public Set<GenreFilmEnum> getGenres() {
		return new HashSet<>(genres);
	}

	public boolean addGenre(GenreFilmEnum genre) {
		return genres.add(genre);
	}

	public boolean removeGenre(GenreFilmEnum genre) {
		boolean isRemoved = genres.remove(genre);
		if (!isRemoved) {
			throw new GenreNotFoundException("Жанр " + genre + " не найден");
		}
		return isRemoved;
	}
	public Integer getCountOfLikes() {
		return UsersLikes.size();
	}
}

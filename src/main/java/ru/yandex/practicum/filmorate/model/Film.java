package ru.yandex.practicum.filmorate.model;

import lombok.Data;
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
	@NotBlank(message = "Имя не долнжо быть пустым")
	private final String name;

	@Size(max = 200, message = "Длинна описания до 200 смиволов")
	private final String description;

//	@Future(payload = LocalDate.of(28,12,1895), message =
//			"Дата релиза не можжет быть ранее 28.12.1895")
	@FilmReleaseDate(message = "Дата релиза не можжет быть ранее 28.12.1895")
	private final LocalDate releaseDate;

//	@FilmDuration(message = "Должны быть проблемы при отрицательной длительности")
	@Min(1)
	private final Integer duration;
	private Set<Integer> likes = new HashSet<>();

	public Set<Integer> getLikes() {
		return new HashSet<>(likes);
	}

	public boolean addLike(Integer userId) {
		return  likes.add(userId);
	}

	public boolean removeLike(Integer userId) {
		return  likes.remove(userId);
	}

	public Integer getCountOfLikes() {
		return likes.size();
	}
//	название не может быть пустым;
//	максимальная длина описания — 200 символов;
//	дата релиза — не раньше 28 декабря 1895 года;
//	продолжительность фильма должна быть положительной.
}

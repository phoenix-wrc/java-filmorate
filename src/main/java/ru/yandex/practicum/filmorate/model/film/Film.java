package ru.yandex.practicum.filmorate.model.film;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validators.FilmReleaseDate;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
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

    @NotNull(message = "Рейтинг обязателен")
    private final MpaRating mpa;
    //	@Setter
    private Set<Genre> genres;
}

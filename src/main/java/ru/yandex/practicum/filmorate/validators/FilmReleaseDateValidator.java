package ru.yandex.practicum.filmorate.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Month;

public class FilmReleaseDateValidator implements ConstraintValidator<FilmReleaseDate, LocalDate> {


	@Override
	public void initialize(FilmReleaseDate constraintAnnotation) {
		ConstraintValidator.super.initialize(constraintAnnotation);
	}

	@Override
	public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
		return localDate.isAfter(LocalDate.of(1895, Month.DECEMBER,28));
	}
}

package ru.yandex.practicum.filmorate.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Duration;

public class FilmDurationValidator implements ConstraintValidator<FilmDuration, Duration> {


	@Override
	public void initialize(FilmDuration constraintAnnotation) {
		ConstraintValidator.super.initialize(constraintAnnotation);
	}

	@Override
	public boolean isValid(Duration duration, ConstraintValidatorContext constraintValidatorContext) {
		return !duration.isNegative();
	}

}

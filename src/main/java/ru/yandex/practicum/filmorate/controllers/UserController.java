package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
	private Integer currentUserId = 1;
	private final Map<Integer, User> users = new HashMap<>();

	@PostMapping
	public User createUser(@Valid @RequestBody User user) throws ValidationException {
		log.debug("Создается пользователь: {}", user);
		//Как я понимаю что бы всё заработало нужно сделать так, а потом нам объяснят как всё сделать правильно))
		if(user.getId() == null) {
			user = new User(currentUserId, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
			currentUserId++;
		}
		if (users.containsKey(user.getId())) {
			throw new ValidationException("Пользователь уже существует. ");
		}
		users.put(user.getId(), user);
		return user;
	}

	@PutMapping
	public User update(@Valid @RequestBody User user) throws ValidationException {
		log.debug("Обновляется пользователь: {}", user);
		if (user.getLogin().contains(" ")) {
			throw new ValidationException("Пользователь ввел логин с пробелами. ");
		}
		if (users.containsKey(user.getId())) {
			users.put(user.getId(), user);
		} else {
			throw new ValidationException("Такого фильма не было");
		}
		return user;
	}

	@GetMapping
	public List<User> users() {
		log.debug("Количество пользователей перед добавлением: {}", users.size());
		return List.of(users.values().toArray(new User[0]));
	}
}

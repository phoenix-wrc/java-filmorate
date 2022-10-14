package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
	private Integer currentUserId;
	private final Map<Integer, User> users;

	public InMemoryUserStorage() {
		users = new HashMap<>();
		currentUserId = 1;
	}

	@Override
	public User get(int id) {
		return users.get(id);
	}

	@Override
	public User add(User user) {
		if (user.getId() == null) {
			user = new User(getNextId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
			currentUserId++;
		}
		if (users.containsKey(user.getId())) {
			throw new ValidationException("Пользователь уже существует. ");
		}
		return users.put(user.getId(), user);
	}

	@Override
	public User delete(User user) {
		return users.remove(user.getId());
	}

	@Override
	public User patch(User user) {
		Integer id = user.getId();
		if (users.containsKey(id)) {
			users.put(user.getId(), user);
		} else {
			throw new ValidationException("Такого пользователя не было");
		}
		return users.get(id);
	}

	@Override
	public List<User> users() {
		return (List<User>) users.values();
	}

	private Integer getNextId() {
		return currentUserId++;
	}
}

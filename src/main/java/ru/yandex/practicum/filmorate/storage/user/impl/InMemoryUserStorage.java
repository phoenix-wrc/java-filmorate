package ru.yandex.practicum.filmorate.storage.user.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component("InMemoryUserStorage")
@Qualifier("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
	private Integer currentUserId; //Нумерователь пользователей
	private final Map<Integer, User> users; //Хранилище

	public InMemoryUserStorage() {
		users = new HashMap<>();
		currentUserId = 1;
	}

	@Override
	public User add(User user) {
		if (user.getId() == null) {
			user = new User(getNextId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
		} else if (users.containsKey(user.getId())) {
			throw new ValidationException("Пользователь уже существует. ");
		}
		User out = users.put(user.getId(), user);
		if (out != null) {
			return out;
			//Тут проверяем не было ли какого-то значения, хотя это не должно работать т.к. пробрасывется исключение
		}
		return user;
	}

	@Override
	public User delete(User user) {
		//Вроде ненужный метод, но пусть пока будет
		return users.remove(user.getId());
	}

	@Override
	public User patch(User user) {
		Integer id = user.getId();
		if (users.containsKey(id)) {
			users.put(user.getId(), user);
		} else {
			throw new UserNotFoundException("Такого пользователя не было");
		}
		return users.get(id);
	}

	@Override
	public Collection<User> users() {
		return users.values();
	}

	@Override
	public User get(Integer id) {
		User out = users.get(id);
		if (out == null) {
			throw new UserNotFoundException("Пользователя нет такого");
		}
		return out;
	}

	private Integer getNextId() {
		return currentUserId++;
	}
}

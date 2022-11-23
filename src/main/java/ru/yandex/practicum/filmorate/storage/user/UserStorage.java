package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.user.User;

import java.util.Collection;

public interface UserStorage {
	User add(User user);

	User delete(User user);

	User patch(User user);

	Collection<User> users();

	User get(Integer id);
}

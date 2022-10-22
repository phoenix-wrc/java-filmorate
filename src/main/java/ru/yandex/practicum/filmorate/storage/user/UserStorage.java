package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Component
public interface UserStorage {
	User add(User user);

	User delete(User user);

	User patch(User user);

	Collection<User> users();

	User get(Integer id);
}

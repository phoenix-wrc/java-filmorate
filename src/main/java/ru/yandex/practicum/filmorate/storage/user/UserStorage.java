package ru.yandex.practicum.filmorate.storage.user;


import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
public interface UserStorage {
	User get(int id);
	User add(User user);
	User delete(User user);
	User patch(User user);

	List<User> users();
}

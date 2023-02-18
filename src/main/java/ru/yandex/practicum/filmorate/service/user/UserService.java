package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.UserFriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
public class UserService {
	private final UserStorage storage;
	private final UserFriendshipStorage friendshipStorage;

	@Autowired
	public UserService(UserStorage storage,
					   @Qualifier("InMemoryUserFriendshipStorage") UserFriendshipStorage friendshipStorage) {
		this.storage = storage;
		this.friendshipStorage = friendshipStorage;
	}

	//Строго говоря слова friend может быть переведен и как глагол дружить(со всеми вытекающими).
	// Но используется в этом смысле очень редко так что думаем лучше)))
	public boolean undoFriendship(int id, int friendId) {
		return true;
	}

	public boolean makeUsersFriends(int id, int friendId) {
		return true;
	}

	public Collection<User> users() {
		//Просто возвращаем значения
		return storage.users();
	}

	public User patch(User user) {
		//В хранилище будет вся логика
		return storage.patch(user);
	}

	public User add(User user) {
		//В хранилище будет вся логика
		return storage.add(user);
	}

	public List<User> getCommonFriends(Integer id, Integer otherId) {
		return friendshipStorage.getCommonFriends(id, otherId);
	}

	public User getUser(Integer id) {
		User out = storage.get(id);
		if (out == null) {
			throw new UserNotFoundException("Нет такого пользователя");
		}
		return out;
	}

	public List<User> getAllFriends(Integer id) {
		return friendshipStorage.getAllFriends(id);
	}
}

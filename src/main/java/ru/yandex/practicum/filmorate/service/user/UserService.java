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
	public UserService(@Qualifier("UserBDStorage") UserStorage storage,
					   @Qualifier("UserFriendshipBDStorage") UserFriendshipStorage friendshipStorage) {
		this.storage = storage;
		this.friendshipStorage = friendshipStorage;
	}

	public boolean undoFriendship(Integer fromId, Integer toId) {
		return friendshipStorage.undoFriendship(fromId, toId);
	}

	public boolean makeUsersFriends(Integer fromId, Integer toId) {
		return friendshipStorage.makeUsersFriends(fromId, toId);
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

package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
	private final UserStorage storage;

	@Autowired
	public UserService(UserStorage storage) {
		this.storage = storage;
	}

	public boolean makeUsersFriends(int id, int friendId) {
		User firstUser = storage.get(id);
		if (firstUser == null) {
			throw new UserNotFoundException("Первого пользователя нет");
		}
		User secondUser = storage.get(friendId);
		if (secondUser == null) {
			throw new UserNotFoundException("Второго пользователя нет");
		}
		//Взяли двух пользователей, теперь будем их дружить
		boolean isFirstFriended = firstUser.addFriend(friendId);
		if (!isFirstFriended) {
			//Если что-то не так ломаем выполнение
			return isFirstFriended;
		}
		boolean isSecondFriended = secondUser.addFriend(id);
		if (!isSecondFriended) {
			//На всякий случай удаляем первое добавление.
			firstUser.deleteFriend(friendId);
			//И ломаем выполнение
			return isSecondFriended;
		}
		//Может просто заменить на тру, но будет непонятно. Вообще не уверен, что булевые значения стоит возвращать
		return isFirstFriended && isSecondFriended;
	}

	//Строго говоря слова friend может быть переведен и как глагол дружить(со всеми вытекающими).
	// Но используется в этом смысле очень редко так что думаем лучше)))
	public boolean undoFriendship(int id, int friendId) {
		User firstUser = storage.get(id);
		if (firstUser == null) {
			throw new UserNotFoundException("Первого пользователя нет");
		}
		User secondUser = storage.get(friendId);
		if (secondUser == null) {
			throw new UserNotFoundException("Второго пользователя нет");
		}
		boolean isDeletedFirst = firstUser.deleteFriend(friendId);
		boolean isDeletedSecond = secondUser.deleteFriend(id);
		//Тут если что-то не так то пофиг
		return isDeletedFirst && isDeletedSecond;
	}

	public List<User> getAllFriends(Integer id) {
		User user = storage.get(id);
		if (user == null) {
			throw new UserNotFoundException("Такого пользователя нет");
		}
		List<User> out = new ArrayList<>();
		// Делаю тут т.к. фроде собирать друзей это не функционал хранилища.
		user.getFriends().forEach(idFriend -> out.add(this.getUser(idFriend)));
		return out;
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

	public List<User> getCommonFriends(int id, int otherId) {
		User firstUser = storage.get(id);
		if (firstUser == null) {
			throw new UserNotFoundException("Первого пользователя нет");
		}
		User secondUser = storage.get(otherId);
		if (secondUser == null) {
			throw new UserNotFoundException("Второго пользователя нет");
		}
		//Вроде так работает
		Set<Integer> firstSet = firstUser.getFriends();
		List<Integer> secondSet = firstSet.stream().filter(u ->
				secondUser.getFriends().contains(u)).collect(Collectors.toList());
		List<User> out = new ArrayList<>();
		secondSet.forEach(userId -> out.add(this.getUser(userId)));
		return out;
	}

	public User getUser(Integer id) {
		User out = storage.get(id);
		if (out == null) {
			throw new UserNotFoundException("Нет такого пользователя");
		}
		return out;
	}
}

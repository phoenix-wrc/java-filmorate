package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
	UserStorage storage;

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
		boolean isFirstFriended = firstUser.addFriend(friendId);
		if(!isFirstFriended) {
			return isFirstFriended;
		}
		boolean isSecondFriended = secondUser.addFriend(id);
		if (!isSecondFriended) {
			firstUser.deleteFriend(friendId);
			//log.debug("Второй друг не подружился, вот список его друзей: ", secondUser.getFriends() );
			return isSecondFriended;
		}
		return isFirstFriended && isSecondFriended;
	}

	public boolean unfriend(int id, int friendId) {
		User firstUser = storage.get(id);
		User secondUser = storage.get(friendId);
		boolean isDeletedFirst = firstUser.deleteFriend(friendId);
		boolean isDeletedSecond = secondUser.deleteFriend(id);
		return isDeletedFirst && isDeletedSecond;
	}

	public List<User> friends(Integer id){
		User user = storage.get(id);
		List<User> out = new ArrayList<>();
		user.getFriends().forEach(idFriend -> out.add(this.getUser(idFriend)));
		return out;
	}

	public Collection<User> users() {
		return storage.users();
	}

	public User patch(User user) {
		return storage.patch(user);
	}

	public User add(User user) {
		return storage.add(user);
	}

	public List<User> getCommonFriends(int id, int otherId) {
		User firstUser = storage.get(id);
		User secondUser = storage.get(otherId);
		Set<Integer> firstSet = firstUser.getFriends();
		List<Integer> secondSet = firstSet.stream().filter(
				u -> secondUser.getFriends().contains(u)).collect(Collectors.toList());
		List<User> out = new ArrayList<>();
		secondSet.forEach(userId -> out.add(this.getUser(userId)));
		return out;
	}

	public User getUser(Integer id) {
		User out = storage.get(id);
		if(out == null) { throw new UserNotFoundException("Нет такого пользователя"); }
		return out;
	}
//	Будет отвечать за такие операции с пользователями, как добавление в друзья,
//	удаление из друзей,
//	вывод списка общих друзей.
//
//	Пока пользователям не надо одобрять заявки в друзья — добавляем сразу.
//	То есть если Лена стала другом Саши, то это значит, что Саша теперь друг Лены.


}

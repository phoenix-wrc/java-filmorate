package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class UserService {
	UserStorage storage;

	@Autowired
	public UserService(UserStorage storage) {
		this.storage = storage;
	}

	public boolean makeUsersFriends(int id, int friendId) {
		User firstUser = storage.get(id);
		User secondUser = storage.get(friendId);
		firstUser.addFriend(secondUser);
		secondUser.addFriend(firstUser);
		return true;
	}

	public boolean unfriend(int id, int friendId) {
		User firstUser = storage.get(id);
		User secondUser = storage.get(friendId);
		boolean isDeletedFirst = firstUser.deleteFriend(friendId, secondUser);
		boolean isDeletedSecond = secondUser.deleteFriend(id, firstUser);
		return isDeletedFirst || isDeletedSecond;
	}

	public Set<Integer> friends(Integer id){
		User user = storage.get(id);
		return user.getFriends().keySet();
	}

	public List<User> users() {
		return storage.users();
	}

	public User patch(User user) {
		return storage.patch(user);
	}

	public User add(User user) {
		return storage.add(user);
	}

	public Set<Integer> getCommonFriends(int id, int otherId) {
		User firstUser = storage.get(id);
		User secondUser = storage.get(otherId);
		Map<Integer, User> out = new HashMap<>(firstUser.getFriends());
		out.keySet().retainAll(secondUser.getFriends().keySet());
		return out.keySet();
	}
//	Будет отвечать за такие операции с пользователями, как добавление в друзья,
//	удаление из друзей,
//	вывод списка общих друзей.
//
//	Пока пользователям не надо одобрять заявки в друзья — добавляем сразу.
//	То есть если Лена стала другом Саши, то это значит, что Саша теперь друг Лены.


}

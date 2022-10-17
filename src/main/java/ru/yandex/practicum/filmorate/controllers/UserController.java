package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
	private UserService service;

	@Autowired
	public UserController(UserService service) {
		this.service = service;
	}

	@PostMapping
	public User createUser(@Valid @RequestBody User user) throws ValidationException {
		log.debug("Создается пользователь: {}", user);
		//Как я понимаю что бы всё заработало нужно сделать так, а потом нам объяснят как всё сделать правильно
		return service.add(user);
	}

	@PutMapping
	public User update(@Valid @RequestBody User user) throws ValidationException {
		log.debug("Обновляется пользователь: {}", user);
		return service.patch(user);
	}

	@GetMapping
	public Collection<User> users() {
		Collection<User> out = service.users();
		log.debug("Количество пользователей перед добавлением: {}", out.size());
		return out;
	}

	@GetMapping("/{id}")
	public User users(@PathVariable("id") Integer id) {
		User out = service.getUser(id);
		log.debug("Возвращаем пользователя под индексом: {}", id);
		return out;
	}

//	PUT /users/{id}/friends/{friendId} — добавление в друзья.
	@PutMapping("/{id}/friends/{friendId}")
	public boolean addFriend(@PathVariable int id,
	                      @PathVariable int friendId) {
		log.debug("Сдружаем  пользователей: {}, {}" , id, friendId);
		return service.makeUsersFriends(id, friendId);
	}

	@DeleteMapping("/{id}/friends/{friendId}")
	public boolean deleteFriend(@PathVariable int id,
                             @PathVariable int friendId) {
		log.debug("Удаляем из друзей пользователя {}, пользователя: {}", id, friendId);
		return service.unfriend(id, friendId);
	}

	@GetMapping("/{id}/friends")
	public List<User> allFriends(@PathVariable int id) {
		log.debug("Отдаем друзей пользователя: {}", id);
		return service.friends(id);
	}
	@GetMapping("/{id}/friends/common/{otherId}")
	public List<User> commonFriends(@PathVariable int id,
	                                @PathVariable int otherId) {
		log.debug("Отдаем общих друзей пользователей: {}, {}", id, otherId);
		return service.getCommonFriends(id, otherId);
	}
}

package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

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
		//Как я понимаю что бы всё заработало нужно сделать так, а потом нам объяснят как всё сделать правильно)
		return service.add(user);
	}

	@PutMapping
	public User update(@Valid @RequestBody User user) throws ValidationException {
		log.debug("Обновляется пользователь: {}", user);
		return service.patch(user);
	}

	@GetMapping
	public List<User> users() {
		List<User>  out = service.users();
		log.debug("Количество пользователей перед добавлением: {}", out.size());
		return out;
	}

//	PUT /users/{id}/friends/{friendId} — добавление в друзья.
	@PutMapping("/users/{id}/friends/{friendId}")
	public boolean addFriend(@PathVariable int id,
	                      @PathVariable int friendId) {
		log.debug("Сдружаем  пользователей: {}, {}" , id, friendId);
		return service.makeUsersFriends(id, friendId);
	}

	@DeleteMapping("/users/{id}/friends/{friendId}")
	public boolean deleteFriend(@PathVariable int id,
                             @PathVariable int friendId) {
		log.debug("Удаляем из друзей пользователя {}, пользователя: {}", id, friendId);
		return service.unfriend(id, friendId);
	}

	@GetMapping("/users/{id}/friends")
	public Set<Integer> allFriends(@PathVariable int id) {
		log.debug("Отдаем друзей пользователя: {}", id);
		return service.friends(id);
	}
	@GetMapping("/users/{id}/friends/common/{otherId}")
	public Set<Integer> commonFriends(@PathVariable int id,
	                                  @PathVariable int otherId) {
		log.debug("Отдаем общих друзей пользователей: {}, {}", id, otherId);
		return service.getCommonFriends(id, otherId);
	}
}

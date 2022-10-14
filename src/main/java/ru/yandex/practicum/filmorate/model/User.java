package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validators.NotContainSpace;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.Map;

@Data
public class User {
	private final Integer id;
	private Map<Integer, User> friends;

	@NotBlank(message = "Почта не должна быть из одних пробелов")
	@Email(message = "Почта должно быть почтой")
	private final String email;

	@NotBlank(message = "Логин не должен быть пустым")
	@NotContainSpace(message = "Логин не должен содержать пробелов")
	private final String login;

	@NotNull(message = "Не задалось имя")
	private final String name;

	@PastOrPresent(message = "Дата рождения должна быть хотя б сегодня, или раньше")
	private final LocalDate birthday;
//
//	электронная почта не может быть пустой и должна содержать символ @;
//	логин не может быть пустым и содержать пробелы;
//	имя для отображения может быть пустым — в таком случае будет использован логин;
//	дата рождения не может быть в будущем.

	public User addFriend(User user) {
		return friends.put(user.getId(), user);
	}

	public String getName() {
		if (name.isBlank() || name.isEmpty()) {
			return login;
		}
		return name;
	}


	public Map<Integer, User> getFriends() {
		return friends;
	}

	public boolean deleteFriend(Integer user, User currentyUser) {
		return friends.remove(user, currentyUser);
	}
}

package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validators.NotContainSpace;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
	private final Integer id;

	private Set<Integer> friends = new HashSet<>();

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
	public boolean addFriend(Integer user) {
		return friends.add(user);
	}

	public String getName() {
		if (name.isBlank() || name.isEmpty()) {
			return login;
		}
		return name;
	}


	public Set<Integer> getFriends() {
		return new HashSet<>(friends);
	}

	public boolean deleteFriend(Integer user) {
		return friends.remove(user);
	}
}

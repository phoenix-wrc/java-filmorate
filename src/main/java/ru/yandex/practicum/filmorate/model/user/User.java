package ru.yandex.practicum.filmorate.model.user;

import lombok.Data;
import ru.yandex.practicum.filmorate.validators.NotContainSpace;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Data
public class User {
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
	private Integer id;

    public User(Integer nextId, String email, String login, String name, LocalDate birthday) {
        this.id = nextId;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public String getName() {
        if (name.isBlank() || name.isEmpty()) {
            return login;
        }
        return name;
    }
}

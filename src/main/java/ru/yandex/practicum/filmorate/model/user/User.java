package ru.yandex.practicum.filmorate.model.user;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validators.NotContainSpace;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Data
@Builder
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

    public String getName() {
        if (name.isBlank() || name.isEmpty()) {
            return login;
        }
        return name;
    }
}

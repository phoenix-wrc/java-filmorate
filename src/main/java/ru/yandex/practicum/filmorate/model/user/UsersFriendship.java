package ru.yandex.practicum.filmorate.model.user;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UsersFriendship {

	@NotNull(message = "Не задался друг который отправил запрос")
	Integer fromUser;

	@NotNull(message = "Не задался друг который получает запрос")
	Integer toUser;

	@NotNull(message = "Не задался статус")
	FriendShipStatusEnum status;

}

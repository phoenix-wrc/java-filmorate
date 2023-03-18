package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.user.User;

import java.util.List;
import java.util.Optional;

public interface UserFriendshipStorage {
    Optional<Integer> makeUsersFriends(Integer fromId, Integer toId);

    List<Optional<User>> getCommonFriends(Integer fromId, Integer toId);

    List<Optional<User>> getAllFriends(Integer id);

    Optional<Integer> undoFriendship(Integer fromId, Integer toId);
}

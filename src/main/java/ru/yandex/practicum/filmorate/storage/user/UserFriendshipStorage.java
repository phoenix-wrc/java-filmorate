package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.user.User;

import java.util.List;

public interface UserFriendshipStorage {
    boolean makeUsersFriends(Integer fromId, Integer toId);

    List<User> getCommonFriends(Integer fromId, Integer toId);

    List<User> getAllFriends(Integer id);

    boolean undoFriendship(Integer fromId, Integer toId);
}

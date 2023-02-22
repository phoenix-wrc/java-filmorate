package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.user.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Optional<User> add(User user);

    Optional<User> delete(User user);

    Optional<User> patch(User user);

    Collection<Optional<User>> users();

    Optional<User> get(Integer id);
}

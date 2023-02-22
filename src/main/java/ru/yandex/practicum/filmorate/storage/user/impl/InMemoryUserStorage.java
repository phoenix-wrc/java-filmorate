package ru.yandex.practicum.filmorate.storage.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component("InMemoryUserStorage")
@Qualifier("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users; //Хранилище
    private Integer currentUserId; //Нумерователь пользователей

    public InMemoryUserStorage() {
        users = new HashMap<>();
        currentUserId = 1;
    }

    @Override
    public Optional<User> add(User user) {
        if (user != null && user.getId() == null) {
            user = User.builder()
                    .id(getNextId())
                    .email(user.getEmail())
                    .login(user.getLogin())
                    .name(user.getName())
                    .birthday(user.getBirthday())
                    .build();
        } else if (users.containsKey(user.getId())) {
            throw new ValidationException("Пользователь уже существует. ");
        }
        return Optional.ofNullable(users.put(user.getId(), user));
    }

    @Override
    public Optional<User> delete(User user) {
        //Вроде ненужный метод, но пусть пока будет
        return Optional.ofNullable(users.remove(user.getId()));
    }

    @Override
    public Optional<User> patch(User user) {
        Integer id = user.getId();
        if (users.containsKey(id)) {
            users.put(user.getId(), user);
        } else {
            throw new UserNotFoundException("Такого пользователя не было");
        }
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Collection<Optional<User>> users() {
        return users.values().stream()
                .map(Optional::ofNullable)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> get(Integer id) {
        User out = users.get(id);
        if (out == null) {
            throw new UserNotFoundException("Пользователя нет такого");
        }
        return Optional.of(out);
    }

    private Integer getNextId() {
        return currentUserId++;
    }
}

package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.UserFriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage storage;
    private final UserFriendshipStorage friendshipStorage;

    @Autowired
    public UserService(@Qualifier("UserBDStorage") UserStorage storage,
                       @Qualifier("UserFriendshipBDStorage") UserFriendshipStorage friendshipStorage) {
        this.storage = storage;
        this.friendshipStorage = friendshipStorage;
    }

    public boolean undoFriendship(Integer fromId, Integer toId) {
        return friendshipStorage.undoFriendship(fromId, toId);
    }

    public boolean makeUsersFriends(Integer fromId, Integer toId) {
        var out = friendshipStorage.makeUsersFriends(fromId, toId);
        if (out.isEmpty()) {
            log.error("Что-то между дружбой от {} к {} не так", fromId, toId);
            throw new UserNotFoundException("Некорректный вводные данные " + fromId + " или " + toId);
//			return false; // Назнаю пока что возвращать
        } else if (out.get() == 1) {
            log.debug("добавили дружбу от {} к {}", fromId, toId);
            return true;
        } else if (out.get() == 0) {
            log.debug("Не добавилась дружба от {} к {}", fromId, toId);
            return false;
        }
        return true;
    }

    public Collection<User> users() {
        //Просто возвращаем значения
        return storage.users().stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public User patch(User user) {
        //В хранилище будет вся логика
        return storage.patch(user).orElseThrow(() ->
                new UserNotFoundException("Ошибка при изменение пользователя"));
    }

    public User add(User user) {
        //В хранилище будет вся логика
        return storage.add(user).orElseThrow(() ->
                new UserNotFoundException("Ошибка при создании пользователя"));
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {
        return friendshipStorage.getCommonFriends(id, otherId).stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public User getUser(Integer id) {
        Optional<User> out = storage.get(id);
        if (out.isEmpty()) {
            throw new UserNotFoundException("Нет такого пользователя");
        }
        return out.get();
    }

    public List<User> getAllFriends(Integer id) {
        return friendshipStorage.getAllFriends(id).stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}

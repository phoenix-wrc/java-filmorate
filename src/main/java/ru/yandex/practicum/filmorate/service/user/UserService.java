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
        var deleteRow = friendshipStorage.undoFriendship(fromId, toId);
        if (deleteRow.isEmpty()) {
            log.error("Какие-то проблемы с удалением дружбы");
            throw new UserNotFoundException("При удалении запроса в друзья что-то поло не так");
        } else if (deleteRow.get() == 1) {
            log.debug("Удалена дружба между пользователями с ИД {}, {}", fromId, toId);
            return true;
        } else if (deleteRow.get() > 1) {
            log.error("Удалилось больше одного дружбы между пользователями по ИД {}, {}"
                    , fromId, toId);
            return false;
        } else if (deleteRow.get() == 0) {
            log.error("Ни одной дружбы по ИД {} и {} не удалилось", fromId, toId);
            throw new UserNotFoundException("При удалении запроса в друзья что-то поло не так");
        } else {
            return false;
        }
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
                new UserNotFoundException("Не удалось изменить пользователя: " + user));

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
            throw new UserNotFoundException("Пользователь с идентификатором " + id + " не найден");
        }
        log.debug("Пользователь с идентификатором {} найден", id);
        return out.get();
    }

    public List<User> getAllFriends(Integer id) {
        return friendshipStorage.getAllFriends(id).stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public User deleteUser(Integer id) {
        Optional<User> out = storage.delete(id);
        if (out.isEmpty()) {
            log.error("Ни одного фильма по ИД {} не удалилось", id);
            throw new UserNotFoundException("Нет пользователя с таким ИД для удаления");
        }
        return out.get();
    }
}

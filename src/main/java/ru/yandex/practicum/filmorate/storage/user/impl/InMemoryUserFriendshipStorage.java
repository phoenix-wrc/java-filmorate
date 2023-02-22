package ru.yandex.practicum.filmorate.storage.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.UserFriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component("InMemoryUserFriendshipStorage")
@Qualifier("InMemoryUserFriendshipStorage")
public class InMemoryUserFriendshipStorage implements UserFriendshipStorage {
    private final UserStorage storage;
    private Map<Integer, Set<Integer>> friendship;

    public InMemoryUserFriendshipStorage(@Qualifier("InMemoryUserStorage") UserStorage storage) {
        this.storage = storage;
    }

    public Optional<Integer> makeUsersFriends(Integer fromId, Integer toId) {
        isExistUser(fromId);
        isExistUser(toId);
        // Взяли двух пользователей, если нет ошибок можно работать
        boolean isFirstFriended;
        if (friendship.containsKey(fromId)) {
            isFirstFriended = friendship.get(fromId).add(toId);
        } else {
            friendship.put(fromId, new HashSet<>(Set.of(toId)));
            isFirstFriended = true;
        }

        if (!isFirstFriended) {
            //Если что-то не так ломаем выполнение
            return Optional.of(0); // Возвращаем сколько чего добавилось
        }

        boolean isSecondFriended;
        if (friendship.containsKey(toId)) {
            isSecondFriended = friendship.get(toId).add(fromId);
        } else {
            friendship.put(toId, new HashSet<>(Set.of(fromId)));
            isSecondFriended = true;
        }

        if (!isSecondFriended) {
            //На всякий случай удаляем первое добавление.
            friendship.get(fromId).remove(toId);
            //И ломаем выполнение
            return Optional.of(0); // Возвращаем сколько чего добавилось
        }
        //Может просто заменить на тру, но будет непонятно. Вообще не уверен, что булевые значения стоит возвращать
        return Optional.of(1); // Возвращаем сколько чего добавилось
    }

    public List<Optional<User>> getCommonFriends(Integer fromId, Integer toId) {
        isExistUser(fromId);
        isExistUser(toId);
        // Взяли двух пользователей, если нет ошибок можно работать
        Set<Integer> firstSet = friendship.get(fromId);
        Set<Integer> secondSet = friendship.get(toId);

        List<Integer> outSet = firstSet.stream()
                .filter(secondSet::contains)
                .collect(Collectors.toList());
        List<Optional<User>> out = new ArrayList<>();
        outSet.forEach(userId -> out.add(storage.get(userId)));
        return out;
    }

    public List<Optional<User>> getAllFriends(Integer id) {
        if (!friendship.containsKey(id)) {
            return List.of(Optional.empty());
        }
        return friendship.get(id).stream()
                .map(storage::get)
                .collect(Collectors.toList());
//        // Делаю тут т.к. фроде собирать друзей это не функционал хранилища.
    }

    public Boolean undoFriendship(Integer fromId, Integer toId) {
        isExistUser(fromId);
        isExistUser(toId);
        // Взяли двух пользователей, если нет ошибок можно работать
        boolean isDeletedFirst = friendship.get(fromId).remove(toId);
        boolean isDeletedSecond = friendship.get(toId).remove(fromId);
        //Тут если что-то не так то пофиг
        return isDeletedFirst && isDeletedSecond;
    }

    private User isExistUser(Integer id) {
        Optional<User> user = storage.get(id);
        return user.orElseThrow(() -> new UserNotFoundException("Пользователя " + id + " нет"));
    }
}

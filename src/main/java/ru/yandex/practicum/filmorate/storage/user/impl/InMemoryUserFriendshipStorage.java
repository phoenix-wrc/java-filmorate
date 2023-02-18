package ru.yandex.practicum.filmorate.storage.user.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.UserFriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component("InMemoryUserFriendshipStorage")
@Qualifier("InMemoryUserFriendshipStorage")
public class InMemoryUserFriendshipStorage implements UserFriendshipStorage {
    private final UserStorage storage;
    private Map<Integer, Set<Integer>> friendship;

    public InMemoryUserFriendshipStorage(@Qualifier("InMemoryUserStorage") UserStorage storage) {
        this.storage = storage;
    }

    public boolean makeUsersFriends(Integer fromId, Integer toId) {
        User firstUser = storage.get(fromId);
        User secondUser = storage.get(toId);
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
            return isFirstFriended;
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
            return isSecondFriended;
        }
        //Может просто заменить на тру, но будет непонятно. Вообще не уверен, что булевые значения стоит возвращать
        return isFirstFriended && isSecondFriended;
    }

    public List<User> getCommonFriends(Integer fromId, Integer toId) {
        User firstUser = storage.get(fromId);
        User secondUser = storage.get(toId);
        // Взяли двух пользователей, если нет ошибок можно работать
        Set<Integer> firstSet = friendship.get(fromId);
        Set<Integer> secondSet = friendship.get(fromId);

        List<Integer> outSet = firstSet.stream()
                .filter(secondSet::contains)
                .collect(Collectors.toList());
        List<User> out = new ArrayList<>();
        outSet.forEach(userId -> out.add(storage.get(userId)));
        return out;
    }

    public List<User> getAllFriends(Integer id) {
        if (!friendship.containsKey(id)) {
            return Collections.emptyList();
        }
        return friendship.get(id).stream()
                .map(storage::get)
                .collect(Collectors.toList());
//        List<User> out = new ArrayList<>();
//        // Делаю тут т.к. фроде собирать друзей это не функционал хранилища.
//        friendship.get(id).forEach(idFriend -> out.add(this.getUser(idFriend)));
//        return out;
    }

    public boolean undoFriendship(Integer fromId, Integer toId) {
        User firstUser = storage.get(fromId);
        User secondUser = storage.get(toId);
        // Взяли двух пользователей, если нет ошибок можно работать
        boolean isDeletedFirst = friendship.get(fromId).remove(toId);
        boolean isDeletedSecond = friendship.get(toId).remove(fromId);
        //Тут если что-то не так то пофиг
        return isDeletedFirst && isDeletedSecond;
    }
}

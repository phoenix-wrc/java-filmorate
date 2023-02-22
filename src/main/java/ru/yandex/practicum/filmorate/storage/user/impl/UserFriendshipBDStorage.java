package ru.yandex.practicum.filmorate.storage.user.impl;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.model.user.UserMapper;
import ru.yandex.practicum.filmorate.storage.user.UserFriendshipStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component("UserFriendshipBDStorage")
@Qualifier("UserFriendshipBDStorage")
public class UserFriendshipBDStorage implements UserFriendshipStorage {
    @NonNull
    private final JdbcTemplate jdbcTemplate;

    public UserFriendshipBDStorage(@NonNull JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Integer> makeUsersFriends(Integer fromId, Integer toId) {
        String sql = "INSERT INTO FILMORATE_FRIENDSHIP (FROM_USER, TO_USER) VALUES (?, ?)";

        int rowEffected;
        try {
            rowEffected = jdbcTemplate.update(sql, fromId, toId);
        } catch (DataAccessException e) {
            log.error("Некорректный вводные данные {}, {}", fromId, toId);
            return Optional.empty();
        }
        return Optional.of(rowEffected);
        // Ошибки и проверки будут в сервисе
    }

    @Override
    public List<Optional<User>> getCommonFriends(Integer fromId, Integer toId) {
        String sql = "SELECT friendship.TO_USER AS USER_ID, " +
                "FU.USER_LOGIN, " +
                "FU.NAME, " +
                "FU.BIRTHDAY, " +
                "FU.EMAIL " +
                "FROM (SELECT ff1.TO_USER " +
                "FROM filmorate_friendship AS ff1 " +
                "WHERE ff1.from_user = ? " +
                "UNION ALL " +
                "SELECT ff2.TO_USER " +
                "FROM filmorate_friendship AS ff2 " +
                "WHERE ff2.from_user = ?) " +
                "AS friendship " +
                "JOIN FILMORATE_USER AS FU " +
                "ON friendship.TO_USER = FU.USER_ID " +
                "GROUP BY USER_ID, " +
                "FU.USER_LOGIN, " +
                "FU.NAME, FU.BIRTHDAY, " +
                "FU.EMAIL " +
                "HAVING COUNT(TO_USER) > 1";
        List<Optional<User>> query = jdbcTemplate.query(sql, new UserMapper(), fromId, toId);
        log.debug("Найдено {} пользователей", query.size());
        return query;
        // Ошибки и проверки будут в сервисе
    }

    @Override
    public List<Optional<User>> getAllFriends(Integer id) {
        String sql = "SELECT USER_ID, " +
                "USER_LOGIN, " +
                "NAME, " +
                "BIRTHDAY, " +
                "EMAIL " +
                "FROM FILMORATE_USER " +
                "JOIN FILMORATE_FRIENDSHIP FF on FILMORATE_USER.USER_ID = FF.TO_USER " +
                "WHERE FROM_USER = ?  ";
        List<Optional<User>> users = jdbcTemplate.query(sql, new UserMapper(), id);
        log.debug("Найдено {} пользователей", users.size());
        return users;
    }

    @Override
    public Boolean undoFriendship(Integer fromId, Integer toId) {
        String sql = "DELETE FROM FILMORATE_FRIENDSHIP WHERE FROM_USER = ? AND TO_USER = ?";
        //Вроде каскад прописан, должно и так работать
        int deleteRow;
        try {
            deleteRow = jdbcTemplate.update(sql, fromId, toId);
        } catch (DataAccessException e) {
            log.error("Некорректный вводные данные {}, {}", fromId, toId);
            throw new UserNotFoundException("Некорректный вводные данные " + fromId + " или " + toId);
        }
        if (deleteRow == 1) {
            log.debug("Удалена дружба между пользователями с ИД {}, {}", fromId, toId);
            return true;
        } else if (deleteRow > 1) {
            log.error("Удалилось больше одного дружбы между пользователями по ИД {}, {}"
                    , fromId, toId);
            return false;
        } else if (deleteRow == 0) {
            log.error("Ни одной дружбы по ИД {} и {} не удалилось", fromId, toId);
            throw new UserNotFoundException("Нет пользователя с таким ИД для удаления");
        }
        return true;
    }
}

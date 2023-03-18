package ru.yandex.practicum.filmorate.storage.user.impl;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.model.user.UserMapper;
import ru.yandex.practicum.filmorate.storage.user.UserFriendshipStorage;

import java.util.Collections;
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
        if (fromId == null && toId == null) {
            return Optional.empty();
        }
        String sql = "INSERT INTO FILMORATE_FRIENDSHIP (FROM_USER, TO_USER) VALUES (?, ?)";

        try {
            int rowEffected = jdbcTemplate.update(sql, fromId, toId);
            return Optional.of(rowEffected);
        } catch (DuplicateKeyException e) {
            log.error("Такая дружба уже есть {}, {}", fromId, toId);
            return Optional.empty();
        } catch (DataIntegrityViolationException e) {
            log.error("Некорректный вводные данные {}, {}", fromId, toId);
            return Optional.empty();
        } catch (DataAccessException e) {
            log.error("Ошибка работы БД {}, {}", fromId, toId);
            return Optional.empty();
        }
        // Ошибки и проверки будут в сервисе
    }

    @Override
    public List<Optional<User>> getCommonFriends(Integer fromId, Integer toId) {
        if (fromId == null && toId == null) {
            return Collections.emptyList();
        }
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
        try {
            List<Optional<User>> query = jdbcTemplate.query(sql, new UserMapper(), fromId, toId);
            log.debug("Найдено {} пользователей", query.size());
            return query;
        } catch (BadSqlGrammarException e) {
            log.error("Проблемы с БД. Введены данные: {}, {}", fromId, toId);
            return Collections.emptyList();
        }
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
                "WHERE FF.FROM_USER = ? ";
        try {
            List<Optional<User>> users = jdbcTemplate.query(sql, new UserMapper(), id);
            log.debug("Найдено {} пользователей, по ИД {}", users.size(), id);
            return users;
        } catch (BadSqlGrammarException e) {
            log.error("Проблемы с БД. Введен ИД: {}", id);
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<Integer> undoFriendship(Integer fromId, Integer toId) {
        String sql = "DELETE FROM FILMORATE_FRIENDSHIP WHERE FROM_USER = ? AND TO_USER = ?";
        //Вроде каскад прописан, должно и так ра ботать
        //Вроде тут даже не нужно ловить ошибки, но на всякий оставлю конструкцию
        try {
            int deleteRow = jdbcTemplate.update(sql, fromId, toId);
            return Optional.of(deleteRow);
        } catch (DataIntegrityViolationException e) {
            log.error("Некорректный вводные данные: {}, {}", fromId, toId);
            return Optional.empty();
        } catch (DataAccessException e) {
            log.error("Проблемы с Баззой данных. Введены: {}, {}", fromId, toId);
            return Optional.empty();
        }
    }
}

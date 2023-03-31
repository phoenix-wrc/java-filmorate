package ru.yandex.practicum.filmorate.storage.user.impl;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.LocalDateFormatter4Date;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.model.user.UserMapper;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Types;
import java.util.*;

@Slf4j
@Component("UserBDStorage")
@Qualifier("UserBDStorage")
public class UserBDStorage implements UserStorage {
    @NonNull
    private final JdbcTemplate jdbcTemplate;

    public UserBDStorage(@NonNull JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<User> add(User user) {
        if (user == null) { // Чекаем чо как
            log.error("Пришел нулл для созранения в БД");
            return Optional.empty();
        }
        String sql = "INSERT INTO FILMORATE_USER " +
                "(USER_LOGIN, NAME, BIRTHDAY, EMAIL) " +
                "VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        var preparedStatementCreatorFactory = new PreparedStatementCreatorFactory( //{
                sql, Types.VARCHAR, Types.VARCHAR, Types.DATE, Types.VARCHAR
        );
        preparedStatementCreatorFactory.setReturnGeneratedKeys(true);
        var psc = preparedStatementCreatorFactory.newPreparedStatementCreator(
                Arrays.asList(
                        user.getLogin(),
                        user.getName(),
                        user.getBirthday().format(LocalDateFormatter4Date.getFormatter()),
                        user.getEmail()
                ));
        // Решение из инета, но заработало как надо
        try {
            int rowEffected = jdbcTemplate.update(psc, keyHolder);
            Integer index;
            index = keyHolder.getKeyAs(Integer.class);
            log.debug("Сохранили юзера с индексом {}", index);
            return get(index);
        } catch (NullPointerException e) {
            log.error("Новый ИД из базы не вернулся, дальше всё не будет работать");
            return Optional.empty();
        } catch (BadSqlGrammarException badSqlGrammarException) {
            log.error("Проблемы с БД. Детали: \n" + badSqlGrammarException.getMessage());
            return Optional.empty();
        } catch (DataAccessException e) {
            log.error("Проблемы с самой БД, детали: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> delete(Integer user) {
        if (user == null) { // Чекаем чо как
            log.error("Пришел нулл для удаления из БД");
            return Optional.empty();
        }
        String sql = "DELETE FROM FILMORATE_USER WHERE USER_ID = ?";
        //Вроде каскад прописан, должно и так работать
        Optional<User> out = get(user);
        try {
            int deleteRow = jdbcTemplate.update(sql, user);
            if (deleteRow == 1) {
                log.debug("Удален пользователь с ИД {}", user);
            } else if (deleteRow > 1) {
                log.error("Удалилось больше одного пользователя по ИД {}", user);
            }
            return out;
        } catch (BadSqlGrammarException badSqlGrammarException) {
            log.error("Проблемы с БД. Детали: \n" + badSqlGrammarException.getMessage());
            return Optional.empty();
        } catch (DataAccessException e) {
            log.error("Проблемы с самой БД, детали: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> patch(User user) {
        if (user == null) {
            log.error("Пользователь не пришел для обновления");
            return Optional.empty();
        } else if (user.getId() == null) {
            log.error("У пользователя нет ИД для обновления");
            return Optional.empty();
        }
        String sql = "UPDATE FILMORATE_USER " +
                "SET " +
                "USER_LOGIN = ?, " +
                "NAME = ?, " +
                "BIRTHDAY = ?, " +
                "EMAIL = ? " +
                "WHERE USER_ID = ?";
        try {
            int patchRow = jdbcTemplate.update(sql,
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday().format(LocalDateFormatter4Date.getFormatter()),
                    user.getEmail(),
                    user.getId());
            if (patchRow > 1) {
                log.error("Обновлено больше одного пользователя по ИД {}", user.getId());
                return this.get(user.getId());
            } else if (patchRow == 0) {
                log.error("Ни одного пользователя по ИД {} не обновилось", user.getId());
                return Optional.empty();
            }
            log.debug("Обновлен пользователь с ИД {}", user.getId());
            return this.get(user.getId());
        } catch (BadSqlGrammarException badSqlGrammarException) {
            log.error("Проблемы с БД. Детали: \n" + badSqlGrammarException.getMessage());
            return Optional.empty();
        } catch (DataAccessException e) {
            log.error("Проблемы с самой БД, детали:\n" + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Collection<Optional<User>> users() {
        String sql = "SELECT USER_ID, " +
                "USER_LOGIN, " +
                "NAME, " +
                "BIRTHDAY, " +
                "EMAIL " +
                "FROM FILMORATE_USER ";
        try {
            List<Optional<User>> users = jdbcTemplate.query(sql, new UserMapper());
            log.debug("Найдено {} пользователей", users.size());
            return users;
        } catch (BadSqlGrammarException e) {
            log.error("Проблемы при получении всех пользователей из БД. Детали:\n" + e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<User> get(Integer id) {
        if (id == null) { // Чекаем чо как
            log.error("Пришел нулл для удаления из БД");
            return Optional.empty();
        }
        String sql = "SELECT USER_ID, " +
                "USER_LOGIN, " +
                "NAME, " +
                "BIRTHDAY, " +
                "EMAIL " +
                "FROM FILMORATE_USER " +
                "WHERE USER_ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new UserMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            log.error("Пользователь с идентификатором {} не найден.", id);
            return Optional.empty();
        } catch (BadSqlGrammarException badSqlGrammarException) {
            log.error("Проблемы с БД. Детали: \n" + badSqlGrammarException.getMessage());
            return Optional.empty();
        }
    }
}

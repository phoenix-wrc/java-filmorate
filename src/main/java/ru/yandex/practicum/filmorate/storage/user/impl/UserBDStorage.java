package ru.yandex.practicum.filmorate.storage.user.impl;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.film.LocalDateFormatter4FilmReleaseDate;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.model.user.UserMapper;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Types;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
    public User add(User user) {
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
                        user.getBirthday().format(LocalDateFormatter4FilmReleaseDate.getFormatter()),
                        user.getEmail()
                ));
        // Решение из инета, но заработало как надо
        int rowEffected = jdbcTemplate.update(psc, keyHolder);
        Integer index;
        try {
            index = keyHolder.getKeyAs(Integer.class);
            log.debug("Сохранили юзера с индексом {}", index);
        } catch (NullPointerException e) {
            log.error("Новый ИД из базы не вернулся, дальше всё не будет работать");
            return user; //Незнаю что возвращать. Можно рефакторнуть и в сигнатурах задать возвращение опшинала,
            // но потом
        }
        return get(index);
    }

    @Override
    public User delete(User user) {
        Integer id = user.getId();
        String sql = "DELETE FROM FILMORATE_USER WHERE USER_ID = ?";
        //Вроде каскад прописан, должно и так работать
        User out = get(id);
        int deleteRow = jdbcTemplate.update(sql, id);
        if (deleteRow == 1) {
            log.debug("Удален пользователь с ИД {}", id);
        } else if (deleteRow > 1) {
            log.error("Удалилось больше одного пользователя по ИД {}", id);
        } else if (deleteRow == 0) {
            log.error("Ни одного фильма по ИД {} не удалилось", id);
            throw new UserNotFoundException("Нет пользователя с таким ИД для удаления");
        }
        return out;
    }

    @Override
    public User patch(User user) {
        String sql = "UPDATE FILMORATE_USER " +
                "SET " +
                "USER_LOGIN = ?, " +
                "NAME = ?, " +
                "BIRTHDAY = ?, " +
                "EMAIL = ? " +
                "WHERE USER_ID = ?";

        int patchRow = jdbcTemplate.update(sql,
                user.getLogin(),
                user.getName(),
                user.getBirthday().format(LocalDateFormatter4FilmReleaseDate.getFormatter()),
                user.getEmail(),
                user.getId()
        );
        if (patchRow == 1) {
            log.debug("Обновлен пользователь с ИД {}", user.getId());
        } else if (patchRow > 1) {
            log.error("Обновлено больше одного пользователя по ИД {}", user.getId());
        } else if (patchRow == 0) {
            log.error("Ни одного пользователя по ИД {} не обновилось", user.getId());
            throw new UserNotFoundException("Не нашлось фильма с таким ИД для обновления");
        }
        return this.get(user.getId());
    }

    @Override
    public Collection<User> users() {
        String sql = "SELECT USER_ID, " +
                "USER_LOGIN, " +
                "NAME, " +
                "BIRTHDAY, " +
                "EMAIL " +
                "FROM FILMORATE_USER ";
        List<User> users = jdbcTemplate.query(sql, new UserMapper());

        if (users.isEmpty()) {
            log.debug("Пользователей нет");
        } else {
            log.debug("Найдено {} пользователей", users.size());
        }

        return users;
    }

    @Override
    public User get(Integer id) {
        String sql = "SELECT USER_ID, " +
                "USER_LOGIN, " +
                "NAME, " +
                "BIRTHDAY, " +
                "EMAIL " +
                "FROM FILMORATE_USER " +
                "WHERE USER_ID = ?";
        User user;
        try {
            user = jdbcTemplate.queryForObject(sql, new UserMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            log.info("Пользователь с идентификатором {} не найден.", id);
            throw new UserNotFoundException("Что то пошло не так");
        }
        //Основную работу делает Мапер
        log.info("Найден Пользователь: {} {}", user.getId(), user.getName());
        return user;
    }
}

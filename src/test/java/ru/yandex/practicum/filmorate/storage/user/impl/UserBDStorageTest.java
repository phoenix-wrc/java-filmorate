package ru.yandex.practicum.filmorate.storage.user.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
//@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
@TestPropertySource(locations = "classpath:application.properties")
@AutoConfigureTestDatabase
class UserBDStorageTest<T> {
    private final String EMAIL = "email@mail.mail";
    private final LocalDate BIRTHDAY = LocalDate.now().minusMonths(1);
    private final String NAME = "1User";
    private final String LOGIN = "Login1";
    private final String PATCHED = "_patched";
    UserStorage userStorage;

    public UserBDStorageTest(@Qualifier("UserBDStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }
    // Т.к. у нас две реализации хранения пользователей ручками прописываем что тестируем.
    // Смысл автопривязки как-то теряется при таких расскладах((((

    // Так же на тестах часто приходится вручную прописывать скрипты т.к. нотация Транзакшин работает не очевидно

    @Test
    @Transactional
    void addRightUser() {
        Optional<User> userOptional = userStorage.add(getUserWithoutId());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("name", NAME))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("birthday", BIRTHDAY))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("login", LOGIN))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("email", EMAIL));
    }

    @Test
    void addNull() {
        Optional<User> userOptional = userStorage.add(null);

        assertThat(userOptional)
                .isEmpty();
    }

    @Test
    @Transactional
    @Sql("/drop.sql")
    void addWithoutTable() {
        Optional<User> userOptional = userStorage.add(getUserWithoutId());
        assertThat(userOptional)
                .isEmpty();
    }

    // DELETE
    @Test
    @Transactional
    @Sql("/drop.sql")
    void deleteWithoutTable() {
        Optional<User> userOptional = userStorage.delete(100);
        assertThat(userOptional)
                .isEmpty();
    }

    @Test
    @Sql({"/test-schema.sql", "/test-data.sql", "/test-user-data.sql"})
    void deleteNull() {
        Optional<User> userOptional = userStorage.delete(null);
        assertThat(userOptional)
                .isEmpty();
    }

    @Test
    @Sql({"/test-schema.sql", "/test-data.sql", "/test-user-data.sql"})
    void deleteWrongId() {
        Optional<User> userOptional = userStorage.delete(9999);
        assertThat(userOptional)
                .isEmpty();
    }

    @Test
    @Sql({"/test-schema.sql", "/test-data.sql", "/test-user-data.sql"})
    void deleteOkExistingUser() {
        Optional<User> userOptional = userStorage.delete(100);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 100)
                );
    }

    @Test
    void deleteOkAddedUser() {
        Optional<User> addedUser = userStorage.add(getUserWithoutId());
        assertThat(addedUser)
                .isPresent();
        Optional<User> userOptional = userStorage.delete(addedUser.orElseThrow(() ->
                new AssertionError("Не добавился пользователь")).getId());
        // Поменять на делет
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", addedUser.get().getId())
                );
    }


    // PATCH
    @Test
    void patchNull() {
        Optional<User> userOptional = userStorage.patch(null);
        assertThat(userOptional)
                .isEmpty();
    }

    @Test
    void patchWrongId() {
        Optional<User> userOptional = userStorage.patch(getPatchedUser(999999));
        assertThat(userOptional)
                .isEmpty();
    }

    @Test
    @Transactional
    @Sql({"/test-schema.sql", "/test-data.sql", "/test-user-data.sql"})
    void patchOkExistingUser() {
        User patchedUser = getPatchedUser(200);
        Optional<User> userOptional = userStorage.patch(patchedUser);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 200)
                                .hasFieldOrPropertyWithValue("name", NAME + PATCHED)
                );
    }

    @Test
    @Transactional
    @Sql({"/test-schema.sql", "/test-data.sql", "/test-user-data.sql"})
    void patchOkAddedUser() {
        Optional<User> addedUser = userStorage.add(getUserWithoutId());
        Optional<User> userOptional = userStorage.patch(getPatchedUser(addedUser.orElseThrow(() ->
                new AssertionError("Не добавился пользователь, тест не выполнен полностью")).getId()));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", addedUser.get().getId())
                                .hasFieldOrPropertyWithValue("name", NAME + PATCHED)
                );
    }

    @Test
    @Transactional
    @Sql("/drop.sql")
    void patchWithoutTable() {
        Optional<User> userOptional = userStorage.patch(getPatchedUser(200));
        assertThat(userOptional)
                .isEmpty();
    }

    // USERS
    @Test
    @Sql({"/test-schema.sql", "/test-data.sql", "/test-user-data.sql"})
    //Почему то работает только когда все файлы инсертятся,
    // ощущение что БД создаются уже после вызова теста ┐('～`;)┌
    public void getAllUsers() {
        var users = userStorage.users();
        assertThat(users)
                .isNotNull();
        assertThat(users.size()).isEqualTo(10);
        //В пользовательских данных у нас 10 пользователей
    }

    @Test
    @Sql({"/drop.sql", "/test-schema.sql", "/test-data.sql", "/test-user-data.sql"})
    public void getAllUsersAfterAdd() {
        userStorage.add(getUserWithoutId());
        var users = userStorage.users();
        assertThat(users)
                .isNotNull();
        assertThat(users.size()).isEqualTo(11);
        //В пользовательских данных у нас 10 пользователей
    }

    @Test
    @Transactional
    @Sql({"/drop.sql", "/test-schema.sql", "/test-data.sql"})
    public void getAllUsersWithEmptyTable() {
        var users = userStorage.users();
        assertThat(users)
                .isNotNull();
        assertThat(users.size()).isEqualTo(0);
    }

    @Test
    @Transactional
    @Sql("/drop.sql")
    public void getAllUsersWithoutTable() {
        var users = userStorage.users();
        assertThat(users)
                .isNotNull();
        assertThat(users.size()).isEqualTo(0);
    }


    //GET
    @Test
    @Sql({"/test-schema.sql", "/test-data.sql", "/test-user-data.sql"})
    //Почему то работает только когда все файлы инсертятся,
    // ощущение что БД создаются уже после вызова теста ┐('～`;)┌
    public void testFindUserById() {
        Optional<User> userOptional = userStorage.get(100);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 100)
                );
    }

    @Test
    public void testDoNotFindUserByWrongId() {
        Optional<User> userOptional = userStorage.get(999);

        assertThat(userOptional)
                .isEmpty();
    }

    @Test
    public void testDoNotFindUserByNullId() {
        Optional<User> userOptional = userStorage.get(null);

        assertThat(userOptional)
                .isEmpty();
    }

    @Test
    @Transactional
    @Sql("/drop.sql")
    public void testDoNotFindUserWithoutTable() {
        Optional<User> userOptional = userStorage.get(200);

        assertThat(userOptional)
                .isEmpty();
    }

    private User getUserWithoutId() {
        return User.builder()
                .email("email@mail.mail")
                .birthday(LocalDate.now().minusMonths(1))
                .name("1User")
                .login("Login1")
                .build();
    }

    private User getPatchedUser(Integer id) {
        return User.builder()
                .id(id)
                .email(EMAIL + PATCHED)
                .name(NAME + PATCHED)
                .birthday(BIRTHDAY)
                .login(LOGIN + PATCHED)
                .build();
    }

}
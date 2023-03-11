package ru.yandex.practicum.filmorate.storage.user.impl;

import lombok.RequiredArgsConstructor;
import org.hibernate.AssertionFailure;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserFriendshipBDStorageTest {
    private final UserFriendshipBDStorage storage;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @Sql({"/test-schema.sql", "/test-data.sql", "/test-user-data.sql"})
    void makeExistingUsersFriends() {
        Optional<Integer> isFriend = storage.makeUsersFriends(100, 700);
        assertThat(isFriend.orElseThrow(() ->
                new AssertionFailure("Дружба не появилась"))).isEqualTo(1);
    }

    @Test
    void makeNotExistUsersFriends() {
        Optional<Integer> isFriend = storage.makeUsersFriends(999, 9999);
        assertThat(isFriend).isEmpty();
    }

    @Test
    @Sql({"/test-schema.sql", "/test-data.sql", "/test-user-data.sql"})
    void makeNullUsersFriends() {
        Optional<Integer> isFriend = storage.makeUsersFriends(null, null);
        assertThat(isFriend).isEmpty();
    }

    @Test
    void getCommonFriends() {
    }

    @Test
    void getAllFriends() {
    }

    @Test
    void undoFriendship() {
    }
}
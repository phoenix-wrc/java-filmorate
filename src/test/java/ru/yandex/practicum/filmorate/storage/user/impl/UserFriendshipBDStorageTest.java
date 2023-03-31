package ru.yandex.practicum.filmorate.storage.user.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserFriendshipBDStorageTest {
    private final UserFriendshipBDStorage storage;

    @Test
    @Sql({"/drop.sql", "/test-schema.sql", "/test-data.sql", "/test-user-data.sql"})
    @Transactional
    void shouldMakeExistingUsersFriends() {
        Optional<Integer> isFriend = storage.makeUsersFriends(100, 700);
        assertThat(isFriend.orElseThrow(() ->
                new AssertionFailedError("Дружба не появилась"))).isEqualTo(1);
    }

    @Test
    @Sql({"/drop.sql", "/test-schema.sql", "/test-data.sql", "/test-user-data.sql"})
    void shouldNotMakeExistingFriendsTwiceFriend() {
        Optional<Integer> isFriend = storage.makeUsersFriends(100, 600);
        assertThat(isFriend).isEmpty();
    }

    @Test
    void shouldNotExistUsersFriends() {
        Optional<Integer> isFriend = storage.makeUsersFriends(999, 9999);
        assertThat(isFriend).isEmpty();
    }

    @Test
    @Sql({"/test-schema.sql", "/test-data.sql", "/test-user-data.sql"})
    void shouldNotMakeNullUsersFriends() {
        Optional<Integer> isFriend = storage.makeUsersFriends(null, null);
        assertThat(isFriend).isEmpty();
    }

    @Test
    @Transactional
    @Sql({"/drop.sql", "/test-schema.sql", "/test-data.sql", "/test-user-data.sql"})
    void shouldGetCommonFriends() {
        var friend = storage.getCommonFriends(100, 200);
        friend.forEach(f -> assertThat(f)
                .isPresent());
        assertThat(friend.size()).isEqualTo(4);
        storage.makeUsersFriends(100, 700);
        storage.makeUsersFriends(200, 700);

        friend = storage.getCommonFriends(100, 200);
        assertThat(friend.size()).isEqualTo(5);
    }

    @Test
    @Transactional
    @Sql({"/test-schema.sql", "/test-data.sql", "/test-user-data.sql"})
    void shouldNotGetCommonFriendWithNullId() {
        var friends = storage.getCommonFriends(null, null);
        assertThat(friends.isEmpty()).isTrue();
        friends = storage.getCommonFriends(null, 100);
        assertThat(friends.isEmpty()).isTrue();
        friends = storage.getCommonFriends(100, null);
        assertThat(friends.isEmpty()).isTrue();
    }

    @Test
    @Sql({"/test-schema.sql", "/test-data.sql", "/test-user-data.sql"})
    void shouldNotGetCommonFriendWithWrongId() {
        var friends = storage.getCommonFriends(99999, 200);
        assertThat(friends.isEmpty()).isTrue();
        friends = storage.getCommonFriends(99999, 99999);
        assertThat(friends.isEmpty()).isTrue();
        friends = storage.getCommonFriends(100, 99999);
        assertThat(friends.isEmpty()).isTrue();
    }

    @Test
    @Transactional
    @Sql({"/drop.sql"})
    void shouldNotGetCommonFriendWithoutTable() {
        var friends = storage.getCommonFriends(100, 200);
        assertThat(friends.isEmpty()).isTrue();
    }

    // getAllFriends
    @Test
    @Transactional
    @Sql({"/test-schema.sql", "/test-data.sql", "/test-user-data.sql"})
    void shouldReturnAllFriendsAfterAddNewFriendship() {
        var friend100 = storage.getAllFriends(100);
        friend100.forEach(f -> assertThat(f)
                .isPresent());
        assertThat(friend100.size()).isEqualTo(5);

        var friend700 = storage.getAllFriends(700);
        friend700.forEach(f -> assertThat(f)
                .isPresent());
        assertThat(friend700.size()).isEqualTo(4);

        storage.makeUsersFriends(100, 700);
        friend100 = storage.getAllFriends(100);
        assertThat(friend100.size()).isEqualTo(6);

        friend700 = storage.getAllFriends(700);
        assertThat(friend700.size()).isEqualTo(4);
    }

    @Test
    void shouldNotReturnFriendsWithWrongId() {
        var friend100 = storage.getAllFriends(9999);
        assertThat(friend100.size()).isEqualTo(0);
    }

    @Test
    void shouldNotReturnFriendsWithNullId() {
        var friend100 = storage.getAllFriends(null);
        assertThat(friend100.size()).isEqualTo(0);
    }


    @Test
    @Transactional
    @Sql({"/drop.sql"})
    void shouldNotReturnFriendsWithoutTable() {
        var friend100 = storage.getAllFriends(100);
        assertThat(friend100.size()).isEqualTo(0);
    }

    //    undoFriendship
    @Test
    @Sql({"/drop.sql", "/test-schema.sql", "/test-data.sql", "/test-user-data.sql"})
    @Transactional
    void shouldUnfriendExistingUsersFriends() {
        var isFriend = storage.undoFriendship(100, 600);
        assertThat(isFriend.orElseThrow(() ->
                new AssertionFailedError("Дружба не появилась"))).isEqualTo(1);
    }

    @Test
    @Sql({"/drop.sql", "/test-schema.sql", "/test-data.sql", "/test-user-data.sql"})
    void shouldNotUnfriendNotFriends() {
        var isFriend = storage.undoFriendship(100, 700);
        assertThat(isFriend.orElseThrow(() ->
                new AssertionFailedError("Дружба не появилась"))).isEqualTo(0);
    }

    @Test
    @Sql({"/drop.sql"})
    @Transactional
    void shouldNotUnfriendWithoutTable() {
        var isFriend = storage.undoFriendship(100, 700);
        assertThat(isFriend).isEmpty();
    }

    @Test
    void shouldNotUnfriendNotExistUsersFriends() {
        var isFriend = storage.undoFriendship(999, 9999);
        assertThat(isFriend.orElseThrow(() ->
                new AssertionFailedError("Дружба не появилась"))).isEqualTo(0);
    }

    @Test
    @Sql({"/test-schema.sql", "/test-data.sql", "/test-user-data.sql"})
    void shouldNotUnfriendNullUsersFriends() {
        var isFriend = storage.undoFriendship(null, null);
        assertThat(isFriend.orElseThrow(() ->
                new AssertionFailedError("Дружба не появилась"))).isEqualTo(0);
    }

}
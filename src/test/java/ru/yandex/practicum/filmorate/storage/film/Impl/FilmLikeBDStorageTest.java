package ru.yandex.practicum.filmorate.storage.film.Impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmLikeBDStorageTest {
    private final FilmLikeBDStorage storage;

//    getLikes()

    @Test
    @Sql({"/drop.sql", "/test-schema.sql", "/test-data.sql"})
    void shouldGetZeroLikesWithoutInitializationUserData() {
        var likes = storage.getLikes(200);

        assertThat(likes).isNotNull();
        likes.forEach(f -> assertThat(f)
                .isPresent()
        );
        assertThat(likes.size()).isEqualTo(0);
    }

    @Test
    @Sql({"/drop.sql", "/test-schema.sql", "/test-data.sql", "/test-user-data.sql"})
    void shouldGetAllLikesAfterInitializationUserData() {
        var likes = storage.getLikes(100);

        assertThat(likes).isNotNull();
        likes.forEach(f -> assertThat(f)
                .isPresent()
        );
        assertThat(likes.size()).isEqualTo(2);
    }

    @Test
    void shouldNotGetLikesWithNull() {
        var likes = storage.getLikes(null);

        assertThat(likes).isNotNull();
        assertThat(likes.isEmpty()).isTrue();
    }

    @Test
    void shouldNotGetLikesWithWrongId() {
        var likes = storage.getLikes(99999);

        assertThat(likes).isNotNull();
        assertThat(likes.isEmpty()).isTrue();
    }

    @Test
    @Transactional
    @Sql({"/drop.sql"})
    void shouldNotGetLikesWithoutTable() {
        var likes = storage.getLikes(100);

        assertThat(likes).isNotNull();
        assertThat(likes.isEmpty()).isTrue();
    }

    @Test
    @Transactional
    @Sql({"/test-schema.sql", "/test-data.sql", "/test-user-data.sql"})
    void shouldGetMoreLikesAfterAdd() {
        var likes = storage.getLikes(100);
        assertThat(likes.size()).isEqualTo(2);
        storage.addLike(100, 200);

        likes = storage.getLikes(100);
        assertThat(likes).isNotNull();
        likes.forEach(f -> assertThat(f)
                .isPresent()
        );
        assertThat(likes.size()).isEqualTo(3);
    }

//    addLike

    @Test
    void shouldNotAddLikeWithNull() {
        var added = storage.addLike(null, null);
        assertThat(added).isFalse();

    }

    @Test
    void shouldNotAddLikeWithWrongFilmId() {
        var added = storage.addLike(999999, 200);
        assertThat(added).isFalse();
    }

    @Test
    void shouldNotAddLikeWithWrongUserId() {
        var added = storage.addLike(100, 9999999);
        assertThat(added).isFalse();
    }

    @Test
    @Sql({"/test-schema.sql", "/test-data.sql", "/test-user-data.sql"})
    void shouldNotAddLikeTwice() {
        var likes = storage.getLikes(100);
        assertThat(likes.size()).isEqualTo(2);
        storage.addLike(100, 100);

        likes = storage.getLikes(100);
        assertThat(likes).isNotNull();
        likes.forEach(f -> assertThat(f)
                .isPresent()
        );
        assertThat(likes.size()).isEqualTo(2);
    }

    @Test
    @Sql({"/test-schema.sql", "/test-data.sql", "/test-user-data.sql"})
    @Transactional
    void shouldAddLike() {
        var likes = storage.getLikes(100);
        assertThat(likes.size()).isEqualTo(2);

        var added = storage.addLike(100, 200);
        assertThat(added).isTrue();

        likes = storage.getLikes(100);
        assertThat(likes).isNotNull();
        likes.forEach(f -> assertThat(f)
                .isPresent()
        );
        assertThat(likes.size()).isEqualTo(3);
    }

    //    removeLike
    @Test
    @Sql({"/test-schema.sql", "/test-data.sql", "/test-user-data.sql"})
    @Transactional
    void shouldRemoveLike() {
        var likes = storage.getLikes(100);
        assertThat(likes.size()).isEqualTo(2);

        var remove = storage.removeLike(100, 100);
        assertThat(remove).isTrue();

        likes = storage.getLikes(100);
        assertThat(likes).isNotNull();
        likes.forEach(f -> assertThat(f)
                .isPresent()
        );
        assertThat(likes.size()).isEqualTo(1);
    }

    @Test
    void shouldNotRemoveLikeWithWrongFilmId() {
        try {
            var remove = storage.removeLike(99999, 100);
        } catch (FilmNotFoundException e) {
            assertThat(e).hasMessage("Лайк от пользователя 100 к фильму 99999 не удалился");
        }
    }

    @Test
    void shouldNotRemoveLikeWithWrongUserId() {
        try {
            var remove = storage.removeLike(100, 999);
        } catch (FilmNotFoundException e) {
            assertThat(e).hasMessage("Лайк от пользователя 999 к фильму 100 не удалился");
        }
    }

    @Test
    void shouldNotRemoveLikeWithNull() {
        var remove = storage.removeLike(null, null);
        assertThat(remove).isFalse();
    }

    @Test
    @Transactional
    @Sql({"/drop.sql"})
    void shouldNotRemoveLikeWithoutTable() {
        var removeLike = storage.removeLike(100, 100);
        assertThat(removeLike).isFalse();
    }

    @Test
    // На всяский зачищаем БД
    @Sql({"/drop.sql", "/test-schema.sql", "/test-data.sql", "/test-user-data.sql"})
    @Transactional
    void shouldGetTopFilms() {
        var top = storage.getTopFilms(10);
        assertThat(top.get(0).orElseThrow().getId()).isEqualTo(300);

        assertThat(storage.addLike(200, 400)).isTrue();
        assertThat(storage.addLike(200, 500)).isTrue();
        assertThat(storage.addLike(200, 600)).isTrue();

        top = storage.getTopFilms(10);
        assertThat(top.get(0).orElseThrow().getId()).isEqualTo(200);
    }

    @Test
    void shouldNotGetTopFilmsWithNull() {
        try {
            var top = storage.getTopFilms(null);
        } catch (NullPointerException e) {
            assertThat(e).isInstanceOf(NullPointerException.class);
        }
    }
}
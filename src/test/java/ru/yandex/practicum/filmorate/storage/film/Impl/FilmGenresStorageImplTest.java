package ru.yandex.practicum.filmorate.storage.film.Impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmGenresStorage;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmGenresStorageImplTest {
    private final FilmGenresStorage storage;

//    void genres()

    @Test
    void shouldReturnAllGenresAfterInitialization() {
        var genres = storage.genres();

        assertThat(genres).isNotNull();
        assertThat(genres.size()).isEqualTo(6);
        // В теории количество не может поменятся, у нас нет добавления
    }

    @Test
    @Transactional
    @Rollback
    @Sql("/drop.sql")
    void shouldNotGetAllGenresAfterDrop() {
        var genres = storage.genres();

        assertThat(genres).isNotNull();
        assertThat(genres.size()).isEqualTo(0);
    }

//    void genre()

    @Test
//    @Sql({"/test-schema.sql", "/test-data.sql"})
    void shouldGetGenresByRightId() {
        var genre = storage.genre(1);
        assertThat(genre)
                .isPresent()
                .hasValueSatisfying(r ->
                        assertThat(r).hasFieldOrPropertyWithValue("id", 1));
    }

    @Test
    void shouldNotGetGenresByWrongId() {
        var genre = storage.genre(9999);
        assertThat(genre)
                .isEmpty();
    }

    @Test
//    @Sql({"/test-schema.sql", "/test-data.sql"})
    void shouldNotGetGenresByNullId() {
        var genre = storage.genre(null);
        assertThat(genre)
                .isEmpty();
    }

//    void getGenres()

    @Test
    @Sql({"/test-user-data.sql"})
    void shouldGetGenresByFilmId() {
        var genres = storage.getGenres(300);
        assertThat(genres).isNotNull();
        assertThat(genres.size()).isEqualTo(2);
    }

    @Test
    void shouldNotGetGenresByNullFilmId() {
        var genre = storage.genre(null);
        assertThat(genre)
                .isEmpty();
    }

    @Test
//    @Sql({"/test-schema.sql", "/test-data.sql"})
    @Sql({"/test-user-data.sql"})
    void shouldNotGetGenresByWrongFilmId() {
        var genre = storage.genre(10);
        assertThat(genre)
                .isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    @Sql("/drop.sql")
    void shouldNotGetGenresAfterDrop() {
        var genres = storage.getGenres(400);

        assertThat(genres).isNotNull();
        assertThat(genres.size()).isEqualTo(0);
    }

//    void updateGenresToFilm()

    @Test
    @Sql({"/test-schema.sql", "/test-data.sql", "/test-user-data.sql"})
    @Transactional
    @Rollback
    void shouldUpdateGenresByFilmId() {
        var genres = storage.getGenres(100);
        assertThat(genres).isNotNull();
        assertThat(genres.size()).isEqualTo(1);

        var update = storage.updateGenresToFilm(
                List.of(new Genre(1, ""), new Genre(2, "")),
                100);
        assertThat(update).isTrue();

        genres = storage.getGenres(100);
        assertThat(genres).isNotNull();
        assertThat(genres.size()).isEqualTo(2);
    }

    @Test
    @Sql({"/test-schema.sql", "/test-data.sql", "/test-user-data.sql"})
    @Transactional
    @Rollback
    void shouldUpdateGenresByEmptyList() {
        var genres = storage.getGenres(100);
        assertThat(genres).isNotNull();
        assertThat(genres.size()).isEqualTo(1);

        var update = storage.updateGenresToFilm(
                Collections.emptyList(),
                100);
        assertThat(update).isTrue();

        genres = storage.getGenres(100);
        assertThat(genres).isNotNull();
        assertThat(genres.size()).isEqualTo(0);
    }

    @Test
    @Sql({"/test-schema.sql", "/test-data.sql", "/test-user-data.sql"})
    void shouldNotUpdateGenresByNullGenres() {
        var update = storage.updateGenresToFilm(null, 100);
        assertThat(update).isFalse();

        var genres = storage.getGenres(100);
        assertThat(genres).isNotNull();
        assertThat(genres.size()).isEqualTo(1);
    }

    @Test
    @Sql({"/test-schema.sql", "/test-data.sql", "/test-user-data.sql"})
    void shouldNotUpdateGenresWithWrongGenresId() {
        var update = storage.updateGenresToFilm(
                List.of(new Genre(55, ""), new Genre(66, "")),
                100);
        assertThat(update).isFalse();

        var genres = storage.getGenres(100);
        assertThat(genres).isNotNull();
        assertThat(genres.size()).isEqualTo(1);
    }

    @Test
    void shouldNotUpdateGenresWithWrongFilmId() {
        var update = storage.updateGenresToFilm(
                List.of(new Genre(1, ""), new Genre(2, "")),
                99999);
        assertThat(update).isFalse();
    }

    @Test
    void shouldNotUpdateGenresByNullFilmId() {
        var update = storage.updateGenresToFilm(
                List.of(new Genre(1, ""), new Genre(2, "")),
                null);
        assertThat(update).isFalse();
    }


    @Test
    @Transactional
    @Rollback
    @Sql("/drop.sql")
    void shouldNotUpdateGenresToFilmAfterDrop() {
        var update = storage.updateGenresToFilm(
                List.of(new Genre(1, ""), new Genre(2, "")),
                100);
        assertThat(update).isFalse();
    }

    //    void setGenresToFilm()
    @Test
    @Sql({"/test-schema.sql", "/test-data.sql", "/test-user-data.sql"})
    @Transactional
    @Rollback
    void shouldSetGenresByFilmId() {
        var genres = storage.getGenres(100);
        assertThat(genres).isNotNull();
        assertThat(genres.size()).isEqualTo(1);

        var update = storage.setGenresToFilm(
                List.of(new Genre(2, ""), new Genre(3, "")),
                100);
        assertThat(update).isTrue();

        genres = storage.getGenres(100);
        assertThat(genres).isNotNull();
        assertThat(genres.size()).isEqualTo(3);
    }

    @Test
    @Sql({"/test-schema.sql", "/test-data.sql", "/test-user-data.sql"})
    void shouldNotSetGenresByNullGenres() {
        var update = storage.setGenresToFilm(null, 100);
        assertThat(update).isFalse();

        var genres = storage.getGenres(100);
        assertThat(genres).isNotNull();
        assertThat(genres.size()).isEqualTo(1);
    }

    @Test
    @Sql({"/test-schema.sql", "/test-data.sql", "/test-user-data.sql"})
    void shouldNotSetGenresWithWrongGenresId() {
        var update = storage.setGenresToFilm(
                List.of(new Genre(55, ""), new Genre(66, "")),
                100);
        assertThat(update).isFalse();

        var genres = storage.getGenres(100);
        assertThat(genres).isNotNull();
        assertThat(genres.size()).isEqualTo(1);
    }

    @Test
    void shouldNotSetGenresWithWrongFilmId() {
        var update = storage.setGenresToFilm(
                List.of(new Genre(1, ""), new Genre(2, "")),
                99999);
        assertThat(update).isFalse();
    }

    @Test
    void shouldNotSetGenresByNullFilmId() {
        var update = storage.setGenresToFilm(
                List.of(new Genre(1, ""), new Genre(2, "")),
                null);
        assertThat(update).isFalse();
    }

    @Test
    @Transactional
    @Rollback
    @Sql("/drop.sql")
    void shouldNotSetGenresToFilmAfterDrop() {
        var update = storage.setGenresToFilm(
                List.of(new Genre(2, ""), new Genre(3, "")),
                100);
        assertThat(update).isFalse();
    }
}
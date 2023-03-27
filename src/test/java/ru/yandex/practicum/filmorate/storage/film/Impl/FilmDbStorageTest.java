package ru.yandex.practicum.filmorate.storage.film.Impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
@TestPropertySource(locations = "classpath:application.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FilmDbStorageTest {
    @Qualifier(value = "FilmBDStorage")
    FilmStorage storage;

    public FilmDbStorageTest(
            @Qualifier(value = "FilmBDStorage")
            FilmStorage storage) {
        this.storage = storage;
    }


    //    void add(
//
//        assertThat(film)
//                .isEmpty()
//                .hasValueSatisfying(r ->
//                        assertThat(r).hasFieldOrPropertyWithValue("id", 1));)
    @Test
    @Transactional
    @Rollback
    @Sql({"/drop.sql", "/test-schema.sql", "/test-data.sql"})
    void shouldAddOkFilm() {
        var film = storage.add(nemFilm());

        assertThat(film)
                .isPresent()
                .hasValueSatisfying(r ->
                        assertThat(r).hasFieldOrPropertyWithValue("id", 1));

    }

    @Test
//    @Sql("/drop.sql")
    void shouldNotAddNullFilm() {
        try {
            var film = storage.add(null);
        } catch (NullPointerException e) {
            assertThat(e).isInstanceOf(NullPointerException.class);
        }
    }

    @Test
    @Transactional
    @Rollback
    @Sql({"/drop.sql", "/test-schema.sql", "/test-data.sql"})
    void shouldIgnoreIdAddingFilm() {
        var film = storage.add(Film.builder()
                .id(9999)
                .name("IgnoredID")
                .releaseDate(LocalDate.now())
                .mpa(new MpaRating(2, ""))
                .build());

        assertThat(film)
                .isPresent()
                .hasValueSatisfying(r ->
                        assertThat(r).hasFieldOrPropertyWithValue("id", 1));
    }

    @Test
    @Transactional
    @Rollback
    @Sql({"/drop.sql", "/test-schema.sql", "/test-data.sql"})
    void shouldAddTwoFilm() {
        var film1 = storage.add(nemFilm());
        var film2 = storage.add(nemFilm());

        assertThat(film1)
                .isPresent()
                .hasValueSatisfying(r1 ->
                        assertThat(r1).hasFieldOrPropertyWithValue("id", 1));
        assertThat(film2)
                .isPresent()
                .hasValueSatisfying(r2 ->
                        assertThat(r2).hasFieldOrPropertyWithValue("id", 2));
    }

    @Test
    @Transactional
    @Rollback
    @Sql("/drop.sql")
    void shouldNotAddFilmWithoutTable() {
        var film = storage.add(nemFilm());

        assertThat(film)
                .isEmpty();

    }


//    void delete()

    @Test
    @Transactional
    @Rollback
    @Sql({"/test-schema.sql", "/test-data.sql", "/test-user-data.sql"})
    void shouldDeleteOldFilm() {
        var film = storage.delete(100);

        assertThat(film)
                .isPresent()
                .hasValueSatisfying(r1 ->
                        assertThat(r1).hasFieldOrPropertyWithValue("id", 100));
    }

    @Test
    @Transactional
    @Rollback
    @Sql({"/drop.sql", "/test-schema.sql", "/test-data.sql"})
    void shouldDeleteAddedFilm() {
        var film = storage.add(nemFilm());

        assertThat(film)
                .isPresent()
                .hasValueSatisfying(r ->
                        assertThat(r).hasFieldOrPropertyWithValue("id", 1));
        var deleteFilm = storage.delete(1);

        assertThat(deleteFilm)
                .isPresent()
                .hasValueSatisfying(r1 ->
                        assertThat(r1).hasFieldOrPropertyWithValue("id", 1));
    }

    @Test
    void shouldNotDeleteFilmWithWrongId() {
        var film = storage.delete(99999);

        assertThat(film)
                .isEmpty();
    }

    @Test
    void shouldNotDeleteFilmWithNull() {
        try {
            var film = storage.delete(null);
        } catch (NullPointerException e) {
            assertThat(e).isInstanceOf(NullPointerException.class);
        }
    }

    @Test
    @Transactional
    @Rollback
    @Sql("/drop.sql")
    void shouldNotDeleteFilmWithoutTable() {
        var film = storage.delete(100);

        assertThat(film)
                .isEmpty();
    }


//    void patch()

    @Test
    @Transactional
    @Rollback
    @Sql({"/test-schema.sql", "/test-data.sql", "/test-user-data.sql"})
    void shouldPatchFOldFilm() {
        var film = storage.patch(patchFilm(storage.getFilm(100).get()));

        assertThat(film)
                .isPresent()
                .hasValueSatisfying(r1 ->
                        assertThat(r1).hasFieldOrPropertyWithValue("id", 100))
                .hasValueSatisfying(r1 ->
                        assertThat(r1.getName()).contains("patched"));

    }

    @Test
    @Transactional
    @Rollback
    @Sql("/drop.sql")
    void shouldNotPatchFilmWithoutTable() {
        var film = storage.patch(100);

        assertThat(film)
                .isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    @Sql("/drop.sql")
    void shouldNotPatchFilmWithoutTable() {
        var film = storage.patch(100);

        assertThat(film)
                .isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    @Sql("/drop.sql")
    void shouldNotPatchFilmWithoutTable() {
        var film = storage.patch(100);

        assertThat(film)
                .isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    @Sql("/drop.sql")
    void shouldNotPatchFilmWithoutTable() {
        var film = storage.patch(100);

        assertThat(film)
                .isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    @Sql("/drop.sql")
    void shouldNotPatchFilmWithoutTable() {
        var film = storage.patch(100);

        assertThat(film)
                .isEmpty();
    }

    @Test
    void films() {
    }

    @Test
    void getFilm() {
    }

    @Test
    void size() {
    }


    private Film nemFilm() {
        return Film.builder()
                .name("addedFilm")
                .description("addedFilm description")
                .releaseDate(LocalDate.now())
                .duration(100)
                .mpa(new MpaRating(1, ""))
                .build();
    }

    private Film patchFilm(Film film) {
        return Film.builder()
                .id(film.getId())
                .name(film.getName() + " patched")
                .description(film.getDescription() + " patched")
                .releaseDate(film.getReleaseDate())
                .mpa(film.getMpa())
                .build();
    }
}
package ru.yandex.practicum.filmorate.storage.film.Impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmBDMpaRatingStorageTest {
    private final FilmBDMpaRatingStorage storage;

    @Test
    void getAllRatingsAfterInitialization() {
        var ratings = storage.ratings();

        assertThat(ratings).isNotNull();
        assertThat(ratings.size()).isEqualTo(5);
        // В теории количество не может поменятся, в нас нет добавления
    }

    @Test
    @Transactional
    @Sql("/drop.sql")
    void getAllRatingsAfterDrop() {
        var ratings = storage.ratings();

        assertThat(ratings).isNotNull();
        assertThat(ratings.size()).isEqualTo(0);
    }

    @Test
    @Sql({"/test-schema.sql", "/test-data.sql"})
    void getRatingByRightId() {
        var rating = storage.rating(1);
        assertThat(rating)
                .isPresent()
                .hasValueSatisfying(r ->
                        assertThat(r).hasFieldOrPropertyWithValue("id", 1))
                .hasValueSatisfying(r ->
                        assertThat(r).hasFieldOrPropertyWithValue("id", 1));
    }

    @Test
    void getRatingByWrongId() {
        var rating = storage.rating(9999);
        assertThat(rating)
                .isEmpty();
    }

    @Test
    @Sql({"/test-schema.sql", "/test-data.sql"})
    void getRatingByNullId() {
        var rating = storage.rating(null);
        assertThat(rating)
                .isEmpty();
    }
}
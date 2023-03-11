package ru.yandex.practicum.filmorate.storage.film.Impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.SqlMergeMode;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

@SpringBootTest
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
@TestPropertySource(locations = "classpath:application.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FilmDbStorageTest {
    FilmStorage filmStorage;

    public FilmDbStorageTest(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }


    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void add() {
    }

    @Test
    void delete() {
    }

    @Test
    void patch() {
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


}
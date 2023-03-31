package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.SqlMergeMode;
import ru.yandex.practicum.filmorate.model.film.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmGenresStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmMpaRatingStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserFriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
@TestPropertySource(locations = "classpath:application.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@RequiredArgsConstructor(onConstructor_ = @Autowired)
//@SqlGroup({
//        @Sql(scripts = "/test-schema.sql",
//                config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)),
//        @Sql("/test-data.sql")})
class FilmorateApplicationTest {
    FilmStorage filmStorage;
    FilmLikeStorage likeStorage;
    FilmMpaRatingStorage mpaRatingStorage;
    FilmGenresStorage genresStorage;
    UserStorage userStorage;
    UserFriendshipStorage friendshipStorage;

    public FilmorateApplicationTest(@Qualifier("FilmBDStorage")
                                    FilmStorage filmStorage,
                                    @Qualifier("FilmLikeBDStorage")
                                    FilmLikeStorage likeStorage,
                                    @Qualifier("FilmGenresStorageImpl")
                                    FilmGenresStorage genresStorage,
                                    @Qualifier("FilmBDMpaRatingStorage")
                                    FilmMpaRatingStorage mpaRatingStorage,
                                    @Qualifier("UserBDStorage")
                                    UserStorage userStorage,
                                    @Qualifier("UserFriendshipBDStorage")
                                    UserFriendshipStorage friendshipStorage) {
        this.filmStorage = filmStorage;
        this.likeStorage = likeStorage;
        this.userStorage = userStorage;
        this.mpaRatingStorage = mpaRatingStorage;
        this.genresStorage = genresStorage;
    }

    @BeforeAll
//    @Sql({"/test-schema.sql", "/test-data.sql" })
    static void setAllUp() {
    }

    void setUpDataBase() {
    }


    @BeforeEach
    void setUp() {
        setUpDataBase();
    }

    @AfterEach
    void tearDown() {
    }


    // Rating
    @Test
    public void testFindRatingById() {

        Optional<MpaRating> userOptional = mpaRatingStorage.rating(1);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(rating ->
                        assertThat(rating).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void testDoNotFindRatingByWrongId() {

        Optional<MpaRating> userOptional = mpaRatingStorage.rating(999);

        assertThat(userOptional)
                .isEmpty();
    }


    @Test
    public void testDoNotFindRatingByNullId() {

        Optional<MpaRating> userOptional = mpaRatingStorage.rating(null);

        assertThat(userOptional)
                .isEmpty();
    }
}
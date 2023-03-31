package ru.yandex.practicum.filmorate.storage.film.Impl;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmGenresStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Component("FilmGenresStorageImpl")
@Qualifier("FilmGenresStorageImpl")
public class FilmGenresStorageImpl implements FilmGenresStorage {
    @NonNull
    private final JdbcTemplate jdbcTemplate;

    public FilmGenresStorageImpl(@NonNull JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Optional<Genre>> genres() {
        //Тут тоже всё просто
        String sql = "SELECT * FROM FILMORATE_GENRE ORDER BY GENRE_ID";
        try {
            List<Optional<Genre>> genres = jdbcTemplate.query(sql, this::getGenre);
            //Пишут что метод квери не может возвращать нулы так что проверки не требуется
            log.debug("Забрали все жанры");
            return genres;
        } catch (BadSqlGrammarException e) {
            log.error("Проблемы с БД. Детали: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<Genre> genre(Integer id) {

        String sql = "SELECT * FROM FILMORATE_GENRE " +
                "WHERE GENRE_ID = ? " +
                "ORDER BY GENRE_ID";
        try {
            return jdbcTemplate.queryForObject(sql, this::getGenre, id);
        } catch (BadSqlGrammarException e) {
            log.error("Что-то не так с базой данных. Детали: {}", e.getMessage());
            return Optional.empty();
        } catch (EmptyResultDataAccessException e) {
            log.error("Пустой возврат результата. Детали: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public List<Optional<Genre>> getGenres(Integer filmId) {
        // Тут вроде всё просто
        String sql = "SELECT FG.GENRE_ID, FG.GENRE " +
                "FROM FILMORATE_FILM_GENRE AS FFG " +
                "JOIN FILMORATE_GENRE AS FG on FG.GENRE_ID = FFG.GENRE_ID " +
                "WHERE FFG.FILM_ID = ? " +
                "ORDER BY FFG.GENRE_ID";
        try {
            List<Optional<Genre>> genres = this.jdbcTemplate.query(sql, this::getGenre, filmId);
            //Пишут что метод квери не может возвращать нулы так что проверки не требуется
            log.debug("Забрали жанры у фильма {}", filmId);
            return genres;
        } catch (BadSqlGrammarException e) {
            log.error("Что-то не так с базой данных. Детали: {}", e.getMessage());
            return Collections.emptyList();
        } catch (EmptyResultDataAccessException e) {
            log.error("Пустой возврат результата. Детали: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public Boolean updateGenresToFilm(List<Genre> newGenres, Integer id) {
        List<Genre> genres = new ArrayList<>();
        if (newGenres != null && !newGenres.isEmpty()) {
            List<Integer> allGenresId = this.genres().stream()
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(Genre::getId)
                    .collect(Collectors.toList());
            genres = newGenres.stream()
                    .filter(g -> allGenresId.contains(g.getId()))
                    .collect(Collectors.toList());
            if (genres.isEmpty()) {
                log.error("Передали список с не валидными жанрами");
                return false;
                // Просто что бы если нам присылают полную абру-кадабру ни чего не происходило
            }
            // не уверен что так корретно, но ни где в логике нет информации о том какие жанры у нас существуют,
            // так что прдется перепроверять прям на месте. Ошибки конечно выбрасываются на как откатывать изменения
            // по которым мы зачищаем предыдущие жанровые принадлежности пока не понятно.
            // Если только всё запихивать в один запрос. Хм... Нужно будет попробовать и глянуть.
        } else if (newGenres == null) {
            return false;
        }
        try {
            int row = jdbcTemplate.update("DELETE FROM FILMORATE_FILM_GENRE " +
                    "WHERE FILM_ID = ?", id);
            log.debug("Удаленно {} записей из таблицы FILMORATE_FILM_GENRE по ИД фильма {}", row, id);
        } catch (BadSqlGrammarException e) {
            log.error("Что-то не так с базой данных. Детали: {}", e.getMessage());
            return false;
        } catch (EmptyResultDataAccessException e) {
            log.debug("Пустой возврат результата. Детали: {}", e.getMessage());
        } catch (DataIntegrityViolationException e) {
            log.debug("Броблемы с переданными ИД. Детали: {}", e.getMessage());
            return false;
        }
        return setGenresToFilm(genres, id);
    }

    @Override
    public Boolean setGenresToFilm(List<Genre> genres, Integer filmId) {
        if (genres == null) {
            return false;
        }
        try {
//                Set<Genre> internalSet = new HashSet<>(genres);
//                for (Genre g : internalSet) { // Зачем так делал, не понню зачем
            for (Genre g : genres) {
                jdbcTemplate.update("INSERT INTO FILMORATE_FILM_GENRE (GENRE_ID, FILM_ID) " +
                        "VALUES (?, ?)", g.getId(), filmId
                );
            }
            return true;
        } catch (BadSqlGrammarException e) {
            log.error("Что-то не так с базой данных. Детали: {}", e.getMessage());
            return false;
        } catch (EmptyResultDataAccessException e) {
            log.debug("Пустой возврат результата. Детали: {}", e.getMessage());
            return false;
        } catch (DataIntegrityViolationException e) {
            log.debug("Проблемы с переданными ИД. Детали: {}", e.getMessage());
            return false;
        }
    }

    private Optional<Genre> getGenre(ResultSet rs, int rowNum) throws SQLException {
        return Optional.of(new Genre(
                rs.getInt("GENRE_ID"), rs.getString("GENRE")));
    }
}

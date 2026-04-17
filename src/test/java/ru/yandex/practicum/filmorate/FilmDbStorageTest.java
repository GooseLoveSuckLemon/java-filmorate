package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.Film.FilmDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class FilmDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private FilmDbStorage filmStorage;

    @BeforeEach
    void setUp() {
        filmStorage = new FilmDbStorage(jdbcTemplate);
        jdbcTemplate.execute("MERGE INTO rating_mpa (rating_id, rating_name) VALUES (1, 'G')");
        jdbcTemplate.execute("MERGE INTO genres (genre_id, genre_name) VALUES (1, 'Комедия')");
    }

    @Test
    void testAddFilm() {
        Film film = createTestFilm();
        Film savedFilm = filmStorage.addFilm(film);

        assertThat(savedFilm).isNotNull();
        assertThat(savedFilm.getFilmId()).isPositive();
        assertThat(savedFilm.getName()).isEqualTo("Test Film");
    }

    @Test
    void testGetFilmById() {
        Film film = createTestFilm();
        Film savedFilm = filmStorage.addFilm(film);

        Film foundFilm = filmStorage.getFilmById(savedFilm.getFilmId());

        assertThat(foundFilm).isNotNull();
        assertThat(foundFilm.getFilmId()).isEqualTo(savedFilm.getFilmId());
        assertThat(foundFilm.getName()).isEqualTo("Test Film");
    }

    @Test
    void testGetAllFilms() {
        Film film1 = createTestFilm();
        Film film2 = createTestFilm();
        film2.setName("Test Film 2");

        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);

        List<Film> films = filmStorage.getAllFilms();

        assertThat(films).hasSize(2);
    }

    @Test
    void testAddLike() {
        jdbcTemplate.execute("INSERT INTO users (email, login, user_name, birthday) VALUES ('test@test.com', 'testuser', 'Test User', '1990-01-01')");

        Film film = createTestFilm();
        Film savedFilm = filmStorage.addFilm(film);

        filmStorage.addLike(savedFilm.getFilmId(), 1);

        Film filmWithLikes = filmStorage.getFilmById(savedFilm.getFilmId());
        assertThat(filmWithLikes.getLikes()).contains(1);
    }

    private Film createTestFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);
        film.setMpa(new MpaRating(1, "G"));

        Set<Genre> genres = new HashSet<>();
        genres.add(new Genre(1, "Комедия"));
        film.setGenres(genres);

        return film;
    }
}
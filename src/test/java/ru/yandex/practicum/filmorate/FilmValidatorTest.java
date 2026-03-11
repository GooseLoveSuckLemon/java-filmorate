package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.backend.Validation.FilmValidator;
import ru.yandex.practicum.filmorate.backend.Validation.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@SpringBootTest
class FilmValidatorTest {
    private final FilmValidator filmValidator = new FilmValidator();

    @Test
    void validateFilm_passedTest() {
        Film film = new Film();
        film.setName("Void Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(100);

        Assertions.assertDoesNotThrow(() -> filmValidator.validateFilm(film));
    }

    @Test
    void validateFilm_beforeTime() {
        Film film = new Film();
        film.setName("Void Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(100);

        ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> filmValidator.validateFilm(film)
        );

        Assertions.assertTrue(
                exception.getErrors().contains("Дата релиза не может быть раньше 28 декабря 1895 года")
        );
    }

    @Test
    void validateFilm_negativeDuration() {
        Film film = new Film();
        film.setName("Void Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(-1);

        ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> filmValidator.validateFilm(film)
        );

        Assertions.assertTrue(
                exception.getErrors().contains("Продолжительность фильма должна быть больше нуля")
        );
    }

    @Test
    void validateFilm_zeroDuration() {
        Film film = new Film();
        film.setName("Void Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(0);

        ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> filmValidator.validateFilm(film)
        );

        Assertions.assertTrue(
                exception.getErrors().contains("Продолжительность фильма должна быть больше нуля")
        );
    }

    @Test
    void validateFilm_veryLongDuration() {
        Film film = new Film();
        film.setName("Void Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(Integer.MAX_VALUE);

        Assertions.assertDoesNotThrow(() -> filmValidator.validateFilm(film));
    }

    @Test
    void validateFilm_nullName() {
        Film film = new Film();
        film.setName(null);
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of((LocalDate.now().plusYears(1).getYear()), 4, 26));
        film.setDuration(100);

        ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> filmValidator.validateFilm(film)
        );

        Assertions.assertTrue(
                exception.getErrors().contains("Название не может быть пустым")
        );
    }

    @Test
    void validateFilm_correctDescription() {
        Film film = new Film();
        film.setName("Void Name");
        film.setDescription("a".repeat(200));
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(100);

        Assertions.assertDoesNotThrow(() -> filmValidator.validateFilm(film));
    }

    @Test
    void validateFilm_exceedingDescription() {
        Film film = new Film();
        film.setName("Void Name");
        film.setDescription("a".repeat(201));
        film.setReleaseDate(LocalDate.of((LocalDate.now().plusYears(1).getYear()), 4, 26));
        film.setDuration(0);

        ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> filmValidator.validateFilm(film)
        );

        Assertions.assertTrue(
                exception.getErrors().contains("Превышена максимальная длина описания — 200 символов")
        );
    }


    @Test
    void validateFilm_nullDescription() {
        Film film = new Film();
        film.setName("Void Name");
        film.setDescription(null);
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(100);

        Assertions.assertDoesNotThrow(() -> filmValidator.validateFilm(film));
    }
}







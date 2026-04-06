package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.Validation.FilmValidator;
import ru.yandex.practicum.filmorate.Validation.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
    class FilmValidatorTest {

        private final FilmValidator validator = new FilmValidator();

        @Test
        void validateFilm_withValidFilm_shouldPass() {
            Film film = new Film();
            film.setName("Valid Film");
            film.setDescription("Valid description");
            film.setReleaseDate(LocalDate.of(2020, 1, 1));
            film.setDuration(120);

            assertDoesNotThrow(() -> validator.validateFilm(film));
        }

        @Test
        void validateFilm_withEmptyName_shouldThrowException() {
            Film film = new Film();
            film.setName("");
            film.setDescription("Description");
            film.setReleaseDate(LocalDate.of(2020, 1, 1));
            film.setDuration(120);

            assertThrows(ValidationException.class, () -> validator.validateFilm(film));
        }

        @Test
        void validateFilm_withNullName_shouldThrowException() {
            Film film = new Film();
            film.setName(null);
            film.setDescription("Description");
            film.setReleaseDate(LocalDate.of(2020, 1, 1));
            film.setDuration(120);

            assertThrows(ValidationException.class, () -> validator.validateFilm(film));
        }

        @Test
        void validateFilm_withNegativeDuration_shouldThrowException() {
            Film film = new Film();
            film.setName("Valid Film");
            film.setDescription("Description");
            film.setReleaseDate(LocalDate.of(2020, 1, 1));
            film.setDuration(-10);

            assertThrows(ValidationException.class, () -> validator.validateFilm(film));
        }

        @Test
        void validateFilm_withZeroDuration_shouldThrowException() {
            Film film = new Film();
            film.setName("Valid Film");
            film.setDescription("Description");
            film.setReleaseDate(LocalDate.of(2020, 1, 1));
            film.setDuration(0);

            assertThrows(ValidationException.class, () -> validator.validateFilm(film));
        }

        @Test
        void validateFilm_withReleaseDateBefore1895_shouldThrowException() {
            Film film = new Film();
            film.setName("Valid Film");
            film.setDescription("Description");
            film.setReleaseDate(LocalDate.of(1800, 1, 1));
            film.setDuration(120);

            assertThrows(ValidationException.class, () -> validator.validateFilm(film));
        }

        @Test
        void validateFilm_withDescriptionLongerThan200_shouldThrowException() {
            Film film = new Film();
            film.setName("Valid Film");
            String longDescription = "a".repeat(201); // 201 символ
            film.setDescription(longDescription);
            film.setReleaseDate(LocalDate.of(2020, 1, 1));
            film.setDuration(120);

            assertThrows(ValidationException.class, () -> validator.validateFilm(film));
        }

        @Test
        void validateFilm_withDescriptionExactly200_shouldPass() {
            Film film = new Film();
            film.setName("Valid Film");
            String exactDescription = "a".repeat(200); // ровно 200 символов
            film.setDescription(exactDescription);
            film.setReleaseDate(LocalDate.of(2020, 1, 1));
            film.setDuration(120);

            assertDoesNotThrow(() -> validator.validateFilm(film));
        }
    }






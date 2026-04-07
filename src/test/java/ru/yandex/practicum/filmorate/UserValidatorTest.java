package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.Validation.UserValidator;
import ru.yandex.practicum.filmorate.Validation.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserValidatorTest {

    private final UserValidator validator = new UserValidator();

    @Test
    void validateUser_withValidUser_shouldPass() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertDoesNotThrow(() -> validator.validateUser(user));
    }

    @Test
    void validateUser_withNullBirthday_shouldThrowException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setBirthday(null);

        assertThrows(ValidationException.class, () -> {
            validator.validateUser(user);
        });
    }

    @Test
    void validateUser_withFutureBirthday_shouldThrowException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        // Используем действительно будущую дату (текущий год + 1)
        user.setBirthday(LocalDate.now().plusYears(1));

        assertThrows(ValidationException.class, () -> {
            validator.validateUser(user);
        });
    }

    @Test
    void validateUser_withEmptyEmail_shouldThrowException() {
        User user = new User();
        user.setEmail("");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> validator.validateUser(user));
    }

    @Test
    void validateUser_withNullEmail_shouldThrowException() {
        User user = new User();
        user.setEmail(null);
        user.setLogin("testuser");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> validator.validateUser(user));
    }

    @Test
    void validateUser_withEmailWithoutAt_shouldThrowException() {
        User user = new User();
        user.setEmail("testexample.com");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> validator.validateUser(user));
    }

    @Test
    void validateUser_withEmptyLogin_shouldThrowException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> validator.validateUser(user));
    }

    @Test
    void validateUser_withNullLogin_shouldThrowException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin(null);
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> validator.validateUser(user));
    }

    @Test
    void validateUser_withLoginWithSpaces_shouldThrowException() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("test user");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> validator.validateUser(user));
    }

    @Test
    void validateUser_withNullName_shouldSetNameToLogin() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName(null);
        user.setBirthday(LocalDate.of(1990, 1, 1));

        validator.validateUser(user);

        assertEquals("testuser", user.getName());
    }

    @Test
    void validateUser_withEmptyName_shouldSetNameToLogin() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        validator.validateUser(user);

        assertEquals("testuser", user.getName());
    }

    @Test
    void validateUser_withPastBirthday_shouldPass() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertDoesNotThrow(() -> validator.validateUser(user));
    }

    @Test
    void validateUser_withTodayBirthday_shouldPass() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.now());

        assertDoesNotThrow(() -> validator.validateUser(user));
    }
}
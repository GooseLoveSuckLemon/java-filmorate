package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.Validation.UserValidator;
import ru.yandex.practicum.filmorate.Validation.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@SpringBootTest
class UserValidatorTest {
    private final UserValidator userValidator = new UserValidator();

    @Test
    void validateUser_passedTest() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.of(2006, 4, 26));

        Assertions.assertDoesNotThrow(
                () -> userValidator.validateUser(user)
        );
    }

    @Test
    void validateUser_nonEmail() {
        User user = new User();
        user.setEmail(null);
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.of(2006, 4, 26));

        ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> userValidator.validateUser(user)
        );

        Assertions.assertTrue(
                exception.getErrors().contains("Электронная почта не может быть пустой")
        );
    }

    @Test
    void validateUser_badEmail() {
        User user = new User();
        user.setEmail("testgmail.com");
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.of(2006, 4, 26));

        ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> userValidator.validateUser(user)
        );

        Assertions.assertTrue(
                exception.getErrors().contains("Электронная почта должна содержать символ @")
        );
    }

    @Test
    void validateUser_nonLogin() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setLogin(null);
        user.setName("name");
        user.setBirthday(LocalDate.of(2006, 4, 26));

        ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> userValidator.validateUser(user)
        );

        Assertions.assertTrue(
                exception.getErrors().contains("Логин не может быть пустым")
        );
    }

    @Test
    void validateUser_badLogin() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setLogin("log in");
        user.setName("name");
        user.setBirthday(LocalDate.of(2006, 4, 26));


        ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> userValidator.validateUser(user)
        );

        Assertions.assertTrue(
                exception.getErrors().contains("Логин не может содержать пробелы")
        );
    }

    @Test
    void validateUser_nullName() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setLogin("login");
        user.setName(null);
        user.setBirthday(LocalDate.of(2006, 4, 26));

        Assertions.assertDoesNotThrow(() -> userValidator.validateUser(user));
    }

    @Test
    void validateUser_badBirthday() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setLogin("log in");
        user.setName("name");
        user.setBirthday(LocalDate.of((LocalDate.now().plusYears(1).getYear()), 4, 26));

        ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> userValidator.validateUser(user)
        );

        Assertions.assertTrue(
                exception.getErrors().contains("Некорректная дата: дата рождения не может быть в будущем")
        );
    }

    @Test
    void validateUser_birthdayNow() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.now());

        Assertions.assertDoesNotThrow(() -> userValidator.validateUser(user));
    }
}













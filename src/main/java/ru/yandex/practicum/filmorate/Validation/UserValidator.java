package ru.yandex.practicum.filmorate.Validation;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.Validation.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class UserValidator {
    public void validateUser(User user) {
        log.debug("Начало валидации пользователя: ID={}, логин='{}'", user.getUserId(), user.getLogin());
        List<String> errors = new ArrayList<>();

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            String errorMsg = "Электронная почта не может быть пустой";
            errors.add(errorMsg);

            log.warn("Ошибка валидации пользователя ID={}: {}", user.getUserId(), errorMsg);

        } else if (!user.getEmail().contains("@")) {
            String errorMsg = "Электронная почта должна содержать символ @";
            errors.add(errorMsg);

            log.warn("Ошибка валидации пользователя ID={}: {}", user.getUserId(), errorMsg);
        }

        if (user.getLogin() == null || user.getLogin().isBlank()) {
            String errorMsg = "Логин не может быть пустым";
            errors.add(errorMsg);

            log.warn("Ошибка валидации пользователя ID={}: {}", user.getUserId(), errorMsg);

        } else if (user.getLogin().contains(" ")) {
            String errorMsg = "Логин не может содержать пробелы";
            errors.add(errorMsg);

            log.warn("Ошибка валидации пользователя ID={}: {}", user.getUserId(), errorMsg);
        }

        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            String errorMsg = "Некорректная дата: дата рождения не может быть в будущем";
            errors.add(errorMsg);

            log.warn("Ошибка валидации пользователя ID={}: {}", user.getUserId(), errorMsg);
        }

        if (!errors.isEmpty()) {
            log.error("Валидация пользователя ID={} не пройдена. Количество ошибок: {}", user.getUserId(), errors.size());
            throw new ValidationException(errors);
        }
        log.debug("Валидация пользователя ID={} успешно завершена", user.getUserId());
    }
}

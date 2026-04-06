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
        log.debug("Начало валидации пользователя: ID={}, логин='{}', birthday={}",
                user.getId(), user.getLogin(), user.getBirthday());
        List<String> errors = new ArrayList<>();

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            String errorMsg = "Электронная почта не может быть пустой";
            errors.add(errorMsg);
            log.warn("Ошибка валидации пользователя ID={}: {}", user.getId(), errorMsg);
        } else if (!user.getEmail().contains("@")) {
            String errorMsg = "Электронная почта должна содержать символ @";
            errors.add(errorMsg);
            log.warn("Ошибка валидации пользователя ID={}: {}", user.getId(), errorMsg);
        }

        if (user.getLogin() == null || user.getLogin().isBlank()) {
            String errorMsg = "Логин не может быть пустым";
            errors.add(errorMsg);
            log.warn("Ошибка валидации пользователя ID={}: {}", user.getId(), errorMsg);
        } else if (user.getLogin().contains(" ")) {
            String errorMsg = "Логин не может содержать пробелы";
            errors.add(errorMsg);
            log.warn("Ошибка валидации пользователя ID={}: {}", user.getId(), errorMsg);
        }

        if (user.getBirthday() == null) {
            String errorMsg = "Дата рождения не может быть пустой";
            errors.add(errorMsg);
            log.warn("Ошибка валидации пользователя ID={}: {}", user.getId(), errorMsg);
        } else {
            LocalDate today = LocalDate.now();
            log.debug("Сравнение дат: birthday={}, today={}, isAfter={}",
                    user.getBirthday(), today, user.getBirthday().isAfter(today));

            if (user.getBirthday().isAfter(today)) {
                String errorMsg = "Дата рождения не может быть в будущем";
                errors.add(errorMsg);
                log.warn("Ошибка валидации пользователя ID={}: {} (birthday={}, today={})",
                        user.getId(), errorMsg, user.getBirthday(), today);
            }
        }

        if (errors.isEmpty()) {
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
                log.debug("Имя пользователя ID={} установлено как логин: '{}'", user.getId(), user.getLogin());
            } else {
                log.debug("Имя пользователя ID={}: '{}'", user.getId(), user.getName());
            }
        }

        if (!errors.isEmpty()) {
            log.error("Валидация пользователя ID={} не пройдена. Количество ошибок: {}", user.getId(), errors.size());
            throw new ValidationException(errors);
        }
        log.debug("Валидация пользователя ID={} успешно завершена", user.getId());
    }
}
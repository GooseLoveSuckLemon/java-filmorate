package ru.yandex.practicum.filmorate.storage.User;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Validation.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.Validation.UserValidator;
import ru.yandex.practicum.filmorate.model.User;
import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer,User> users = new HashMap<>();
    private int id = 1;
    private final UserValidator userValidator = new UserValidator();

    @Override
    public User addUser(User user) {
        log.info("Создание нового пользователя: логин='{}'", user.getLogin());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Имя пользователя не указано, установлено по умолчанию: '{}'", user.getName());
        }
        userValidator.validateUser(user);
        user.setUserId(id++);
        users.put(user.getUserId(), user);
        log.info("Пользователь успешно создан с ID: {}", user.getUserId());
        return user;
    }

    @Override
    public Map<Integer, User> deleteUser(int id) {
        log.info("Удаление пользователя c ID: {}", id);
        if (!users.containsKey(id)) {
            String errorMsg = "Пользователь с ID " + id + " не найден";
            log.error(errorMsg);
            throw new NotFoundException(errorMsg);
        }
        users.remove(id);
        log.info("Пользователь с ID {} успешно удалён!", id);
        return users;
    }

    @Override
    public User updateUser(User updatedUser) {
        log.info("Обновление пользователя с ID: {}", updatedUser.getUserId());

        User existingUser = users.get(updatedUser.getUserId());
        if (existingUser != null) {
            log.info("Существующий пользователь: ID={}, login={}, name={}",
                    existingUser.getUserId(), existingUser.getLogin(), existingUser.getName());
        }

        if (!users.containsKey(updatedUser.getUserId())) {
            String errorMsg = "Пользователь с ID " + updatedUser.getUserId() + " не найден";
            log.error(errorMsg);
            throw new NotFoundException(errorMsg);
        }

        if (updatedUser.getName() == null || updatedUser.getName().isBlank()) {
            updatedUser.setName(updatedUser.getLogin());
            log.debug("Имя пользователя не указано при обновлении, установлено по умолчанию: '{}'", updatedUser.getName());
        }

        userValidator.validateUser(updatedUser);

        users.put(updatedUser.getUserId(), updatedUser);

        log.info("Пользователь с ID {} успешно обновлён. Всего пользователей: {}",
                updatedUser.getUserId(), users.size());

        users.forEach((key, value) ->
                log.debug("В хранилище: ID={}, login={}", key, value.getLogin()));

        return updatedUser;
    }

    @Override
    public User getUserById(int id) {
        log.info("Поиск пользователя с ID {}", id);
        if (!users.containsKey(id)) {
            String errorMsg = "Пользователь с ID " + id + " не найден";
            log.error(errorMsg);
            throw new NotFoundException(errorMsg);
        }
        log.info("Пользователь с ID {} найден", id);
        return users.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        int userCount = users.size();
        log.info("Запрос всех пользователей. Найдено: {} записей", userCount);

        users.forEach((id, user) ->
                log.debug("Пользователь в хранилище: ID={}, login={}", id, user.getLogin()));

        return new ArrayList<>(users.values());
    }
}

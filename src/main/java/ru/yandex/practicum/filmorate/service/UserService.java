package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.User.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.User.UserStorage;

import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserDbStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = (UserDbStorage) userStorage;
    }

    public void addFriend(int userId, int friendId) {
        log.debug("Добавление в друзья: пользователя {} и пользователя {}", userId, friendId);

        if (userId == friendId) {
            log.warn("Нельзя добавлять в друзья самого себя!");
            throw new IllegalStateException("Нельзя добавить самого себя в друзья");
        }

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        userStorage.addFriend(userId, friendId);
        log.info("Пользователь {} добавлен в друзья к {}", friendId, userId);
    }

    public void removeFriends(int userId, int friendId) {
        log.debug("Удаление из друзей: пользователя {} и пользователя {}", userId, friendId);

        if (userId == friendId) {
            log.warn("Нельзя удалить из друзей самого себя!");
            throw new IllegalStateException("Нельзя удалить самого себя из друзей");
        }

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        userStorage.removeFriend(userId, friendId);
        log.info("Пользователь {} удален из друзей у {}", friendId, userId);
    }

    public List<User> getUserFriends(int userId) {
        log.debug("Получение списка друзей пользователя {}", userId);
        userStorage.getUserById(userId);
        return userStorage.getUserFriends(userId);
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        log.debug("Получение общих друзей пользователей {} и {}", userId, otherId);
        userStorage.getUserById(userId);
        userStorage.getUserById(otherId);
        return userStorage.getCommonFriends(userId, otherId);
    }
}
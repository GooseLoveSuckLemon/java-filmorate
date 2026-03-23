package ru.yandex.practicum.filmorate.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.User.UserStorage;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(int userId, int friendId) {
        log.debug("Добавление в друзья: пользователя {} и пользователя {}", userId, friendId);

        if (userId == friendId) {
            log.warn("Нельзя добавлять в друзья самого себя!");
            throw new IllegalStateException();
        }

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        log.info("Пользователи {} и {} теперь друзья", userId, friendId);
    }

    public void removeFriends(int userId, int friendId) {
        log.debug("Удаление из друзей: пользователя {} и пользователя {}", userId, friendId);

        if (userId == friendId) {
            log.warn("Нельзя удалить из друзей самого себя!");
            throw new IllegalStateException();
        }

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        log.info("Пользователи {} и {} больше не друзья", userId, friendId);
    }

    public List<User> getUserFriends(int userId) {
        log.debug("Получение списка друзей пользователя {}", userId);

        User user = userStorage.getUserById(userId);
        return user.getFriends().stream()
                .map(id -> userStorage.getUserById(id))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        log.debug("Получение общих друзей пользователей {} и {}", userId, otherId);

        User user = userStorage.getUserById(userId);
        User other = userStorage.getUserById(otherId);

        return user.getFriends().stream()
                .filter(other.getFriends()::contains)
                .map(id -> userStorage.getUserById(id))
                .collect(Collectors.toList());
    }
}

package ru.yandex.practicum.filmorate.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Friend.FriendStatus;
import ru.yandex.practicum.filmorate.model.User.User;
import ru.yandex.practicum.filmorate.storage.User.UserStorage;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public boolean isFriend(User user, int friendId) {
        return user.getFriends().containsKey(friendId) &&
                user.getFriends().get(friendId) == FriendStatus.ACCEPT;
    }

    public void sendFriendRequest(int userId, int friendId) {
        log.debug("Отправка запроса на дружбу от {} к {}", userId, friendId);

        if (userId == friendId) {
            log.warn("Нельзя добавлять в друзья самого себя!");
            throw new IllegalStateException();
        }

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        if (isFriend(user, friendId)) {
            log.warn("Пользователи {} и {} уже друзья", userId, friendId);
            throw new IllegalStateException("Пользователи уже друзья");
        }

        if (user.getFriends().containsKey(friendId)) {
            log.warn("Заявка в друзья уже отправлена пользователю {}", friendId);
            throw new IllegalStateException("Заявка уже отправлена");
        }

        user.getFriends().put(friendId, FriendStatus.SEND);
        friend.getFriends().put(userId, FriendStatus.SEND);

        log.info("Запрос на добавление в друзья от пользователя {} к {}", userId, friendId, " отправлен");
    }

    public void passedFriend(int userId, int friendId) {
        log.debug("Подтверждение заявки на дружбу: {} подтверждает заявку от {}", userId, friendId);

        if (userId == friendId) {
            log.warn("Нельзя подтвердить дружбу с самим собой");
            throw new IllegalArgumentException("Нельзя подтвердить дружбу с самим собой");
        }

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        if (!user.getFriends().containsKey(friendId)) {
            log.warn("Нет входящей заявки от пользователя {} к пользователю {}", friendId, userId);
            throw new IllegalStateException("Нет входящей заявки от этого пользователя");
        }

        if(user.getFriends().get(friendId) == FriendStatus.ACCEPT) {
            log.warn("Заявка от {} к {} уже подтверждена", friendId, userId);
            throw new IllegalStateException("Заявка уже подтверждена");
        }

        user.getFriends().put(friendId, FriendStatus.ACCEPT);
        friend.getFriends().put(userId, FriendStatus.ACCEPT);

        log.info("Пользователи {} и {}", userId, friendId, " теперь друзья");
    }

    public void rejectFriendRequest(int userId, int friendId) {
        log.debug("Отклонение заявки на дружбу: {} отклоняет заявку от {}", userId, friendId);

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        if (!user.getFriends().containsKey(friendId)) {
            log.warn("Нет входящей заявки от пользователя {} к пользователю {}", friendId, userId);
            throw new IllegalStateException("Нет входящей заявки от этого пользователя");
        }

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        log.info("Заявка на дружбу отклонена: {} отклонил заявку от {}", userId, friendId);
    }

    public void removeFriends(int userId, int friendId) {
        log.debug("Удаление из друзей: у пользователя {} пользователя {}", userId, friendId);

        if (userId == friendId) {
            log.warn("Нельзя удалить из друзей самого себя!");
            throw new IllegalStateException();
        }

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        if (!isFriend(user, friendId)) {
            log.warn("Пользователи {} и {} не являются друзьями", userId, friendId);
            throw new IllegalStateException("Пользователи не являетесь друзьями");
        }

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        log.info("Пользователи {} и {} больше не друзья", userId, friendId);
    }

    public List<User> getUserFriends(int userId) {
        log.debug("Получение списка друзей пользователя {}", userId);

        User user = userStorage.getUserById(userId);
        return user.getFriends().entrySet().stream()
                .filter(entry -> entry.getValue() == FriendStatus.ACCEPT)
                .map(entry -> userStorage.getUserById(entry.getKey()))
                .collect(Collectors.toList());
    }

    public List<User> getFriendRequests(int userId) {
        log.debug("Получение входящих заявок в друзья для пользователя {}", userId);

        User user = userStorage.getUserById(userId);
        return user.getFriends().entrySet().stream()
                .filter(entry -> entry.getValue() == FriendStatus.SEND)
                .map(entry -> userStorage.getUserById(entry.getKey()))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        log.debug("Получение общих друзей пользователей {} и {}", userId, otherId);

        User user = userStorage.getUserById(userId);
        User other = userStorage.getUserById(otherId);

        Set<Integer> userFriends = user.getFriends().entrySet().stream()
                .filter(entry -> entry.getValue() == FriendStatus.ACCEPT)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        Set<Integer> otherFriend = other.getFriends().entrySet().stream()
                .filter(entry -> entry.getValue() == FriendStatus.ACCEPT)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        return userFriends.stream()
                .filter(otherFriend::contains)
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }
}

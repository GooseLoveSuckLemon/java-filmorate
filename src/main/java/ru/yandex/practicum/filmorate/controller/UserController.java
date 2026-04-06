package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Validation.UserValidator;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.User.UserStorage;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@Validated
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody User user) {
        log.info("Создание пользователя: {}", user.getLogin());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        UserValidator validator = new UserValidator();
        validator.validateUser(user);

        User savedUser = userStorage.addUser(user);
        log.info("Пользователь '{}' успешно создан, ID: {}", user.getLogin(), savedUser.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        log.info("Обновление пользователя с ID: {}", user.getId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        UserValidator validator = new UserValidator();
        validator.validateUser(user);

        User updatedUser = userStorage.updateUser(user);
        log.info("Пользователь с ID {} успешно обновлён", user.getId());
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUserById(@PathVariable int id, @Validated @RequestBody User user) {
        log.info("Обновление пользователя с ID: {}", id);
        user.setId(id);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        User updatedUser = userStorage.updateUser(user);
        log.info("Пользователь с ID {} успешно обновлён", id);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        log.info("Получение всех пользователей");
        List<User> users = userStorage.getAllUsers();
        log.debug("Возвращено {} пользователей", users.size());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        log.info("Получение пользователя по ID: {}", id);
        User user = userStorage.getUserById(id);
        log.debug("Пользователь с ID {} найден!", id);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        log.info("Удаление пользователя с ID {}", id);
        userStorage.deleteUser(id);
        log.info("Пользователь с ID {} успешно удалён", id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Добавление друга: пользователь с id {} добавляет пользователя с id {}", id, friendId);
        userService.addFriend(id, friendId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> removeFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Удаление друга: пользователь с id {} удаляет пользователя с id {}", id, friendId);
        userService.removeFriends(id, friendId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<List<User>> getUserFriends(@PathVariable int id) {
        log.info("Получение списка друзей пользователя с id {}", id);
        List<User> friends = userService.getUserFriends(id);
        return ResponseEntity.ok(friends);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<List<User>> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("Получение общих друзей пользователей id {} и id {}", id, otherId);
        List<User> commonFriends = userService.getCommonFriends(id, otherId);
        return ResponseEntity.ok(commonFriends);
    }
}
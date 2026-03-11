package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.backend.UserBackend;
import ru.yandex.practicum.filmorate.model.User;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j

public class UserController {
    private final UserBackend userBackend;

    public UserController(UserBackend userBackend) {
        this.userBackend = userBackend;
    }

    @PostMapping
    public ResponseEntity<User> addUser(@Validated @RequestBody User user) {
        log.info("Создание пользователя: {}", user.getLogin());
        User savedUser = userBackend.addUser(user);
        log.info("Пользователь '{}' успешно создан, ID: {}", user.getLogin(), savedUser.getId());
        return ResponseEntity.status(201).body(savedUser);
    }

    @PutMapping()
    public ResponseEntity<User> updateUser(@Validated @RequestBody User user) {
        log.info("Обновление пользователя с ID: {}", user.getId());
        User updatedUser = userBackend.updateUser(user);
        log.info("Пользователь с ID {} успешно обновлён", user.getId());
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        log.info("Получение всех пользователей");
        List<User> users = userBackend.getAllUsers();
        log.debug("Возвращено {} пользователей", users.size());
        return ResponseEntity.ok(users);
    }
}

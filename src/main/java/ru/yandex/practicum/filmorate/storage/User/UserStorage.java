package ru.yandex.practicum.filmorate.storage.User;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {
    User addUser(User user);

    User updateUser(User updatedUser);

    Map<Integer, User> deleteUser(int userId);

    List<User> getAllUsers();

    User getUserById(int userId);
}

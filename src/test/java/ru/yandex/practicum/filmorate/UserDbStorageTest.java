package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.Validation.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.User.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private UserDbStorage userStorage;

    @BeforeEach
    void setUp() {
        userStorage = new UserDbStorage(jdbcTemplate);
    }

    @Test
    void testAddUser() {
        User user = createTestUser();
        User savedUser = userStorage.addUser(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUserId()).isPositive();
        assertThat(savedUser.getEmail()).isEqualTo("test@test.com");
    }

    @Test
    void testGetUserById() {
        User user = createTestUser();
        User savedUser = userStorage.addUser(user);

        User foundUser = userStorage.getUserById(savedUser.getUserId());

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUserId()).isEqualTo(savedUser.getUserId());
        assertThat(foundUser.getLogin()).isEqualTo("testuser");
    }

    @Test
    void testGetAllUsers() {
        User user1 = createTestUser();
        User user2 = createTestUser();
        user2.setEmail("test2@test.com");
        user2.setLogin("testuser2");

        userStorage.addUser(user1);
        userStorage.addUser(user2);

        List<User> users = userStorage.getAllUsers();

        assertThat(users).hasSize(2);
    }

    @Test
    void testAddFriend() {
        User user1 = createTestUser();
        User user2 = createTestUser();
        user2.setEmail("friend@test.com");
        user2.setLogin("friend");

        user1 = userStorage.addUser(user1);
        user2 = userStorage.addUser(user2);

        userStorage.addFriend(user1.getUserId(), user2.getUserId());

        userStorage.addFriend(user2.getUserId(), user1.getUserId());

        List<User> friends = userStorage.getUserFriends(user1.getUserId());
        assertThat(friends).hasSize(1);
        assertThat(friends.get(0).getUserId()).isEqualTo(user2.getUserId());
    }

    @Test
    void testGetCommonFriends() {
        User user1 = createTestUser();
        User user2 = createTestUser();
        user2.setEmail("user2@test.com");
        user2.setLogin("user2");
        User user3 = createTestUser();
        user3.setEmail("user3@test.com");
        user3.setLogin("user3");

        user1 = userStorage.addUser(user1);
        user2 = userStorage.addUser(user2);
        user3 = userStorage.addUser(user3);

        userStorage.addFriend(user1.getUserId(), user2.getUserId());
        userStorage.addFriend(user2.getUserId(), user1.getUserId());
        userStorage.addFriend(user1.getUserId(), user3.getUserId());
        userStorage.addFriend(user3.getUserId(), user1.getUserId());
        userStorage.addFriend(user2.getUserId(), user3.getUserId());
        userStorage.addFriend(user3.getUserId(), user2.getUserId());

        List<User> commonFriends = userStorage.getCommonFriends(user1.getUserId(), user2.getUserId());
        assertThat(commonFriends).hasSize(1);
        assertThat(commonFriends.get(0).getUserId()).isEqualTo(user3.getUserId());
    }

    @Test
    void testRemoveFriend() {
        User user1 = createTestUser();
        User user2 = createTestUser();
        user2.setEmail("friend@test.com");
        user2.setLogin("friend");

        user1 = userStorage.addUser(user1);
        user2 = userStorage.addUser(user2);

        userStorage.addFriend(user1.getUserId(), user2.getUserId());
        userStorage.addFriend(user2.getUserId(), user1.getUserId());

        List<User> friends = userStorage.getUserFriends(user1.getUserId());
        assertThat(friends).hasSize(1);

        userStorage.removeFriend(user1.getUserId(), user2.getUserId());

        friends = userStorage.getUserFriends(user1.getUserId());
        assertThat(friends).isEmpty();
    }

    @Test
    void testDeleteUser() {
        User user = createTestUser();
        User savedUser = userStorage.addUser(user);

        User foundUser = userStorage.getUserById(savedUser.getUserId());
        assertThat(foundUser).isNotNull();

        userStorage.deleteUser(savedUser.getUserId());

        org.junit.jupiter.api.Assertions.assertThrows(
                NotFoundException.class,
                () -> userStorage.getUserById(savedUser.getUserId())
        );
    }

    @Test
    void testUpdateUser() {
        User user = createTestUser();
        User savedUser = userStorage.addUser(user);

        savedUser.setName("Updated Name");
        savedUser.setEmail("updated@test.com");

        User updatedUser = userStorage.updateUser(savedUser);

        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@test.com");
    }

    private User createTestUser() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return user;
    }
}

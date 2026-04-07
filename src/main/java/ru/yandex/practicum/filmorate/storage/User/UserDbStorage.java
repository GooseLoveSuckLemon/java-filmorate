package ru.yandex.practicum.filmorate.storage.User;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.Validation.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository("userStorage")
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        String sql = "INSERT INTO users (email, login, user_name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"user_id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, java.sql.Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        user.setUserId(keyHolder.getKey().intValue());

        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE users SET email = ?, login = ?, user_name = ?, birthday = ? WHERE user_id = ?";
        int updated = jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                java.sql.Date.valueOf(user.getBirthday()),
                user.getUserId()
        );

        if (updated == 0) {
            throw new NotFoundException("Пользователь с ID " + user.getUserId() + " не найден");
        }

        return getUserById(user.getUserId());
    }

    @Override
    public Map<Integer, User> deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        int deleted = jdbcTemplate.update(sql, userId);

        if (deleted == 0) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }

        return getAllUsersMap();
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        List<User> users = jdbcTemplate.query(sql, new UserRowMapper());
        for (User user : users) {
            loadFriends(user);
        }
        return users;
    }

    private Map<Integer, User> getAllUsersMap() {
        List<User> users = getAllUsers();
        Map<Integer, User> userMap = new HashMap<>();
        for (User user : users) {
            userMap.put(user.getUserId(), user);
        }
        return userMap;
    }

    @Override
    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        List<User> users = jdbcTemplate.query(sql, new UserRowMapper(), userId);

        if (users.isEmpty()) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }

        User user = users.get(0);
        loadFriends(user);

        return user;
    }

    private void loadFriends(User user) {
        String sql = "SELECT friend_id FROM friends WHERE user_id = ?";
        List<Integer> friends = jdbcTemplate.query(sql, (rs, rowNum) ->
                rs.getInt("friend_id"), user.getUserId());
        user.setFriends(new HashSet<>(friends));
    }

    public void addFriend(int userId, int friendId) {
        String checkSql = "SELECT COUNT(*) FROM friends WHERE user_id = ? AND friend_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, userId, friendId);

        if (count == 0) {
            String sql = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
            jdbcTemplate.update(sql, userId, friendId);
        }
    }

    public void removeFriend(int userId, int friendId) {
        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    public List<User> getUserFriends(int userId) {
        String sql = "SELECT u.* FROM users u " +
                "JOIN friends f ON u.user_id = f.friend_id " +
                "WHERE f.user_id = ?";
        List<User> friends = jdbcTemplate.query(sql, new UserRowMapper(), userId);
        for (User friend : friends) {
            loadFriends(friend);
        }
        return friends;
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        String sql = "SELECT u.* FROM users u " +
                "WHERE u.user_id IN (SELECT f1.friend_id FROM friends f1 WHERE f1.user_id = ?) " +
                "AND u.user_id IN (SELECT f2.friend_id FROM friends f2 WHERE f2.user_id = ?)";
        List<User> commonFriends = jdbcTemplate.query(sql, new UserRowMapper(), userId, otherId);
        for (User friend : commonFriends) {
            loadFriends(friend);
        }
        return commonFriends;
    }

    private class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setUserId(rs.getInt("user_id"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setName(rs.getString("user_name"));
            java.sql.Date birthday = rs.getDate("birthday");
            if (birthday != null) {
                user.setBirthday(birthday.toLocalDate());
            }
            return user;
        }
    }
}
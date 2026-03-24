package ru.yandex.practicum.filmorate.model.User;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.Friend.FriendStatus;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
public class User {
    private int userId;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    private Map<Integer, FriendStatus> friends = new HashMap<>();

    public void addFriend(int friendsId, FriendStatus status) {
        friends.put(friendsId, status);
    }

    public void removeFriends(int friendsId) {
        friends.remove(friendsId);
    }

    public void acceptStatus(int friendId) {
        friends.put(friendId, FriendStatus.ACCEPT);
    }
}

package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public void delete(User user) {
        userStorage.delete(user);
    }

    public User findById(Long id) {
        return userStorage.findById(id);
    }

    public void addFriends(Long userId, Long friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);
        user.getFriends().add(friend);
        friend.getFriends().add(user);

        log.debug("Added user {} to friends {}", user, friend);
    }

    public void deleteFriend(Long userId, Long friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);
        user.getFriends().remove(friend);
        friend.getFriends().remove(user);

        log.debug("Removed user {} from friends {}", user, friend);
    }

    public Set<User> getCommonFriends(Long userId, Long friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);
        Set<User> commonFriends = user.getFriends().stream()
                .filter(friend.getFriends()::contains)
                .collect(Collectors.toSet());

        if (commonFriends.isEmpty()) {
            return Set.of();
        }

        return commonFriends;
    }

    public Set<User> findFriends(Long id) {
        return userStorage.findById(id).getFriends();
    }
}

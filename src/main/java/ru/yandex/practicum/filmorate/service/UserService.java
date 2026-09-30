package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EmptyStringException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
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
        if (userStorage.findAll().isEmpty()) {
            throw new NotFoundException("No users found");
        }
        return userStorage.findAll();
    }

    public Optional<User> create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        return userStorage.create(user);
    }

    public Optional<User> update(User newUser) {
        if (newUser.getId() == null) {
            throw new EmptyStringException("Id должен быть указан");
        }
        if (userStorage.findById(newUser.getId()).isPresent()) {
            return userStorage.update(newUser);
        }
        throw new NotFoundException("Фильм с id = " + newUser.getId() + " не найден");
    }

    public void delete(User user) {
        if (userStorage.findById(user.getId()).isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
        }
        userStorage.delete(user);
    }

    public Optional<User> findById(Long id) {
        if (userStorage.findById(id).isPresent()) {
            return userStorage.findById(id);
        } else {
            throw new NotFoundException("User with id = " + id + " not found");
        }
    }

    public void addFriends(Long userId, Long friendId) {
        if (!userId.equals(friendId)) {
            Optional<User> user = userStorage.findById(userId);
            Optional<User> friend = userStorage.findById(friendId);
            if (user.isPresent() && friend.isPresent()) {
                user.get().getFriends().add(friend.get());
                friend.get().getFriends().add(user.get());
            }

            log.debug("Added user {} to friends {}", user, friend);
        }
    }

    public void deleteFriend(Long userId, Long friendId) {
        if (!userId.equals(friendId)) {
            Optional<User> user = userStorage.findById(userId);
            Optional<User> friend = userStorage.findById(friendId);
            if (user.isPresent() && friend.isPresent()) {
                user.get().getFriends().remove(friend.get());
                friend.get().getFriends().remove(user.get());
            }

            log.debug("Removed user {} from friends {}", user, friend);
        }
    }

    public Set<User> getCommonFriends(Long userId, Long friendId) {
        Set<User> commonFriends = new HashSet<>();
        if (!userId.equals(friendId)) {
            Optional<User> user = userStorage.findById(userId);
            Optional<User> friend = userStorage.findById(friendId);
            if (user.isPresent() && friend.isPresent()) {
                commonFriends = user.get().getFriends().stream()
                        .filter(friend.get().getFriends()::contains)
                        .collect(Collectors.toSet());
            }
        } else {
            commonFriends = userStorage.findById(userId).get().getFriends();
        }
        return commonFriends;
    }

    public Set<User> findFriends(Long id) {
        if (userStorage.findById(id).isEmpty()) {
            throw new NotFoundException("User with id = " + id + " not found");
        }
        return userStorage.findById(id).get().getFriends();
    }
}

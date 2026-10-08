package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.EmptyStringException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class UserService {
    private UserDbStorage userStorage;

    @Autowired
    public UserService(UserDbStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
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
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id = " + userId + " not found"));
        userStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("User with id = " + friendId + " not found"));
        userStorage.addFriend(userId, friendId, 1);   // 1 = CONFIRMED
    }

    public void deleteFriend(Long userId, Long friendId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id = " + userId + " not found"));
        userStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("User with id = " + friendId + " not found"));
        userStorage.deleteFriend(userId, friendId);
    }

    public Collection<User> getCommonFriends(Long userId, Long friendId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id = " + userId + " not found"));
        userStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("User with id = " + friendId + " not found"));

        Set<User> common = new HashSet<>(userStorage.getFriends(userId));
        common.retainAll(new HashSet<>(userStorage.getFriends(friendId)));
        return common;
    }

    public Collection<User> findFriends(Long id) {
        userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id = " + id + " not found"));
        return userStorage.getFriends(id);   // ← читаем из БД, не из памяти
    }
}

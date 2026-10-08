package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.dao.UserRepository;
import ru.yandex.practicum.filmorate.exception.EmptyStringException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

@Slf4j
@Service
public class UserService {
    private UserRepository userRepository;

    @Autowired
    public UserService(UserDbStorage userStorage) {
        this.userRepository = userStorage;
    }

    public Collection<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        return userRepository.create(user);
    }

    public Optional<User> update(User newUser) {
        if (newUser.getId() == null) {
            throw new EmptyStringException("Id должен быть указан");
        }
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        if (userRepository.findById(newUser.getId()).isPresent()) {
            return userRepository.update(newUser);
        }
        throw new NotFoundException("Фильм с id = " + newUser.getId() + " не найден");
    }

    public void delete(User user) {
        if (userRepository.findById(user.getId()).isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
        }
        userRepository.delete(user);
    }

    public Optional<User> findById(Long id) {
        if (userRepository.findById(id).isPresent()) {
            return userRepository.findById(id);
        } else {
            throw new NotFoundException("User with id = " + id + " not found");
        }
    }

    public void addFriends(Long userId, Long friendId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id = " + userId + " not found"));
        userRepository.findById(friendId)
                .orElseThrow(() -> new NotFoundException("User with id = " + friendId + " not found"));
        userRepository.addFriend(userId, friendId, 1);   // 1 = CONFIRMED
    }

    public void deleteFriend(Long userId, Long friendId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id = " + userId + " not found"));
        userRepository.findById(friendId)
                .orElseThrow(() -> new NotFoundException("User with id = " + friendId + " not found"));
        userRepository.deleteFriend(userId, friendId);
    }

    public Collection<User> getCommonFriends(Long userId, Long friendId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id = " + userId + " not found"));
        userRepository.findById(friendId)
                .orElseThrow(() -> new NotFoundException("User with id = " + friendId + " not found"));

        return userRepository.getCommonFriends(userId, friendId);
    }

    public Collection<User> findFriends(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id = " + id + " not found"));
        return userRepository.getFriends(id);   // ← читаем из БД, не из памяти
    }
}

package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<Long, User>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return Optional.of(user);
    }

    @Override
    public Optional<User> update(User newUser) {
        User oldUser = users.get(newUser.getId());
        if (newUser.getName() != null && !newUser.getName().isBlank()
                && !newUser.getName().equals(oldUser.getName())) {
            oldUser.setName(newUser.getName());
        }
        if (newUser.getLogin() != null && !newUser.getLogin().isBlank()
                && !newUser.getLogin().equals(oldUser.getLogin())) {
            oldUser.setLogin(newUser.getLogin());
        }
        if (newUser.getEmail() != null && !newUser.getEmail().isBlank()
                && !newUser.getEmail().equals(oldUser.getEmail())) {
            oldUser.setEmail(newUser.getEmail());
        }
        if (newUser.getBirthday() != null && newUser.getBirthday().isBefore(LocalDate.now())
                && !newUser.getBirthday().equals(oldUser.getBirthday())) {
            oldUser.setBirthday(newUser.getBirthday());
        }
        return Optional.ofNullable(oldUser);
    }

    @Override
    public void delete(User user) {
        users.remove(user.getId());
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}

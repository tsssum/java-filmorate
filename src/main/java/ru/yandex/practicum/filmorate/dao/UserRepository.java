package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(long id);

    Collection<User> findAll();

    Optional<User> create(User user);

    Collection<User> getFriends(long userId);

    Collection<User> getCommonFriends(Long userId, Long friendId);

    void addFriend(long userId, long friendId, long statusId);

    void deleteFriend(long userId, long friendId);

    void delete(User user);

    Optional<User> update(User user);
}

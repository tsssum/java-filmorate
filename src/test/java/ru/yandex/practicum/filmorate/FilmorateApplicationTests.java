package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.GENRE;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, FilmDbStorage.class, GenreRepository.class, MpaDbStorage.class})
class FilmoRateApplicationTests {

    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final GenreRepository genreRepository;
    private final MpaDbStorage mpaDbStorage;

    private User newUser(String login) {
        return new User(0, login + "@mail.ru", login, login, LocalDate.of(1990, 1, 1));
    }

    private Film newFilm(String title) {
        return new Film(0, title, "desc", LocalDate.of(2000, 1, 1),
                new HashSet<>(Set.of(GENRE.DRAMA)), 1, "120");
    }

    @Test
    void testCreateUser() {
        User saved = userStorage.create(newUser("user1")).orElseThrow();
        assertThat(saved.getId()).isPositive();
    }

    @Test
    void testFindUserById() {
        User saved = userStorage.create(newUser("user2")).orElseThrow();
        Optional<User> found = userStorage.findById(saved.getId());
        assertThat(found)
                .isPresent()
                .hasValueSatisfying(u -> assertThat(u).hasFieldOrPropertyWithValue("id", saved.getId()));
    }

    @Test
    void testFindAllUsers() {
        userStorage.create(newUser("user3"));
        assertThat(userStorage.findAll()).isNotEmpty();
    }

    @Test
    void testUpdateUser() {
        User saved = userStorage.create(newUser("user4")).orElseThrow();
        saved.setName("Updated");
        userStorage.update(saved);
        Optional<User> found = userStorage.findById(saved.getId());
        assertThat(found).isPresent()
                .hasValueSatisfying(u -> assertThat(u).hasFieldOrPropertyWithValue("name", "Updated"));
    }

    @Test
    void testAddAndDeleteFriend() {
        User u1 = userStorage.create(newUser("u1")).orElseThrow();
        User u2 = userStorage.create(newUser("u2")).orElseThrow();

        userStorage.addFriend(u1.getId(), u2.getId(), 1);
        userStorage.deleteFriend(u1.getId(), u2.getId());
    }

    @Test
    void testDeleteUser() {
        User saved = userStorage.create(newUser("user5")).orElseThrow();
        userStorage.delete(saved);
        assertThat(userStorage.findById(saved.getId())).isEmpty();
    }


    @Test
    void testCreateFilm() {
        Film saved = filmStorage.create(newFilm("Film1")).orElseThrow();
        assertThat(saved.getId()).isPositive();
    }

    @Test
    void testFindFilmById() {
        Film saved = filmStorage.create(newFilm("Film2")).orElseThrow();
        Optional<Film> found = filmStorage.findById(saved.getId());
        assertThat(found)
                .isPresent()
                .hasValueSatisfying(f -> assertThat(f).hasFieldOrPropertyWithValue("id", saved.getId()));
    }

    @Test
    void testFindAllFilms() {
        filmStorage.create(newFilm("Film3"));
        assertThat(filmStorage.findAll()).isNotEmpty();
    }

    @Test
    void testUpdateFilm() {
        Film saved = filmStorage.create(newFilm("Film4")).orElseThrow();
        saved.setName("Updated Film");
        filmStorage.update(saved);
        Optional<Film> found = filmStorage.findById(saved.getId());
        assertThat(found).isPresent()
                .hasValueSatisfying(f -> assertThat(f).hasFieldOrPropertyWithValue("name", "Updated Film"));
    }

    @Test
    void testAddLike() {
        User u = userStorage.create(newUser("liker")).orElseThrow();
        Film f = filmStorage.create(newFilm("Film5")).orElseThrow();
        filmStorage.addLike(u.getId(), f.getId());
    }

    @Test
    void testFindGenresByFilmId() {
        Film saved = filmStorage.create(newFilm("Film6")).orElseThrow();
        Set<GENRE> genres = filmStorage.findGenresByFilmId(saved.getId());
        assertThat(genres).contains(GENRE.DRAMA);
    }

    @Test
    void testDeleteFilm() {
        Film saved = filmStorage.create(newFilm("Film7")).orElseThrow();
        filmStorage.delete(saved);
        assertThat(filmStorage.findById(saved.getId())).isEmpty();
    }


    @Test
    void testGenreGetAll() {
        assertThat(genreRepository.getAll()).isNotEmpty();
    }

    @Test
    void testGenreGetById() {
        assertThat(genreRepository.getById(1)).isPresent();
    }


    @Test
    void testMpaGetAll() {
        assertThat(mpaDbStorage.getAll()).isNotEmpty();
    }

    @Test
    void testMpaGetById() {
        assertThat(mpaDbStorage.getById(1)).isPresent();
    }
}
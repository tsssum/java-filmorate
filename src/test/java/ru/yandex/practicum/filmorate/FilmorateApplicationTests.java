package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmorateApplicationTests {

    private FilmController filmController;
    private UserController userController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        userController = new UserController();
    }

    // ФИЛЬМЫ

    @Test
    void createFilmValidDataSuccess() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Film created = filmController.create(film);
        assertNotNull(created.getId());
        assertEquals(1, filmController.findAll().size());
        assertEquals("Test Film", created.getName());
    }

    @Test
    void createFilmDescriptionExactlyMaxLengthSuccess() {
        Film film = new Film();
        film.setName("Test");
        film.setDescription("a".repeat(200));
        film.setReleaseDate(LocalDate.now());
        film.setDuration(90);

        assertDoesNotThrow(() -> filmController.create(film));
    }

    @Test
    void createFilmReleaseDateBeforeMinThrowsException() {
        Film film = new Film();
        film.setName("Test");
        film.setDescription("Desc");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(90);

        assertThrows(DateException.class, () -> filmController.create(film));
    }

    @Test
    void createFilmReleaseDateExactlyMinSuccess() {
        Film film = new Film();
        film.setName("Test");
        film.setDescription("Desc");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(90);

        assertDoesNotThrow(() -> filmController.create(film));
    }

    @Test
    void updateFilmValidDataSuccess() {
        Film film = new Film();
        film.setName("Old");
        film.setDescription("Old desc");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(100);
        Film created = filmController.create(film);

        Film updateData = new Film();
        updateData.setId(created.getId());
        updateData.setName("New Name");
        updateData.setDescription("New desc");

        Film updated = filmController.update(updateData);
        assertEquals("New Name", updated.getName());
        assertEquals("New desc", updated.getDescription());
        assertEquals(created.getReleaseDate(), updated.getReleaseDate());
        assertEquals(created.getDuration(), updated.getDuration());
    }

    @Test
    void updateFilmNotFoundThrowsException() {
        Film updateData = new Film();
        updateData.setId(999L);
        updateData.setName("New");

        assertThrows(NotFoundException.class, () -> filmController.update(updateData));
    }

    @Test
    void updateFilmIdNullThrowsException() {
        Film updateData = new Film();
        updateData.setId(null);

        assertThrows(EmptyStringException.class, () -> filmController.update(updateData));
    }

    // ЮЗЕРЫ

    @Test
    void createUserValidDataSuccess() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("testLogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User created = userController.create(user);
        assertNotNull(created.getId());
        assertEquals(1, userController.findAll().size());
        assertEquals("testLogin", created.getLogin());
    }

    @Test
    void createUserNameEmptySetsToLogin() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("login123");
        user.setName("");
        user.setBirthday(LocalDate.now());

        User created = userController.create(user);
        assertEquals("login123", created.getName());
    }

    @Test
    void createUserNameNullSetsToLogin() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("login123");
        user.setName(null);
        user.setBirthday(LocalDate.now());

        User created = userController.create(user);
        assertEquals("login123", created.getName());
    }

    @Test
    void createUserBirthdayTodaySuccess() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("login");
        user.setName("Name");
        user.setBirthday(LocalDate.now());

        assertDoesNotThrow(() -> userController.create(user));
    }

    @Test
    void updateUserValidDataSuccess() {
        User user = new User();
        user.setEmail("old@mail.ru");
        user.setLogin("oldLogin");
        user.setName("Old Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User created = userController.create(user);

        User updateData = new User();
        updateData.setId(created.getId());
        updateData.setEmail("new@mail.ru");
        updateData.setLogin("newLogin");
        updateData.setName("New Name");
        updateData.setBirthday(LocalDate.of(2000, 1, 1));

        User updated = userController.update(updateData);
        assertEquals("new@mail.ru", updated.getEmail());
        assertEquals("newLogin", updated.getLogin());
        assertEquals("New Name", updated.getName());
        assertEquals(LocalDate.of(2000, 1, 1), updated.getBirthday());
    }

    @Test
    void updateUserNotFoundThrowsException() {
        User updateData = new User();
        updateData.setId(999L);
        updateData.setEmail("new@mail.ru");

        assertThrows(NotFoundException.class, () -> userController.update(updateData));
    }

    @Test
    void updateUserIdNullThrowsException() {
        User updateData = new User();
        updateData.setId(null);

        assertThrows(EmptyStringException.class, () -> userController.update(updateData));
    }

}
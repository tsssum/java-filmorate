package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@lombok.Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = "friends")
public class User {
    Long id;
    @NotBlank
    @Email
    String email;
    @NotBlank
    String login;
    String name;
    @NotNull
    @PastOrPresent
    LocalDate birthday;
    @JsonIgnore
    Set<User> friends;

    @Autowired
    public User(long userId, String email, String login, String userName, LocalDate birthday) {
        this.id = userId;
        this.email = email;
        this.login = login;
        this.name = userName;
        this.birthday = birthday;
        this.friends = new HashSet<>();
    }
}

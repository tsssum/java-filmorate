package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.Set;

@lombok.Data
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
}

package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum GENRE {
    COMEDY(1, "Комедия"),
    DRAMA(2, "Драма"),
    CARTOON(3, "Мультфильм"),
    THRILLER(4, "Триллер"),
    DOCUMENTARY(5, "Документальный"),
    ACTION(6, "Боевик");

    private final int id;
    private final String name;

    GENRE(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @JsonProperty("id")
    public int getId() {
        return id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public static GENRE fromId(int id) {
        return Arrays.stream(values()).filter(g -> g.id == id).findFirst().orElse(null);
    }

    @JsonCreator
    public static GENRE fromJson(@JsonProperty("id") Integer id) {
        if (id == null) return null;
        GENRE g = fromId(id);
        if (g == null) throw new NotFoundException("Жанр с id=" + id + " не найден");
        return g;
    }
}
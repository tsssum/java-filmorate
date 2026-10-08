package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum GENRE {
    DRAMA(1, "Драма"),
    COMEDY(2, "Комедия"),
    ACTION(3, "Боевик"),
    HORROR(4, "Ужасы"),
    THRILLER(5, "Триллер"),
    SPORT(6, "Спорт");

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
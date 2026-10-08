package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum GENRE {
    DRAMA(1, "DRAMA"),
    COMEDY(2, "COMEDY"),
    ACTION(3, "ACTION"),
    HORROR(4, "HORROR"),
    THRILLER(5, "THRILLER"),
    SPORT(6, "SPORT");

    private final int id;
    private final String name;

    GENRE(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static GENRE fromId(int id) {
        return Arrays.stream(values())
                .filter(g -> g.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown genre id: " + id));
    }

    @JsonProperty("id")
    public int getId() {
        return id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonCreator
    public static GENRE fromJson(@JsonProperty("id") Integer id) {
        return id == null ? null : fromId(id);
    }

}
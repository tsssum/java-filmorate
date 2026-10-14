package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum MPA {
    G(1, "G"),
    PG(2, "PG"),
    PG_13(3, "PG-13"),
    R(4, "R"),
    NC_17(5, "NC-17");

    private final int id;
    private final String name;

    MPA(int id, String name) {
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

    public static MPA fromId(int id) {
        return Arrays.stream(values()).filter(m -> m.id == id).findFirst().orElse(null);
    }

    @JsonCreator
    public static MPA fromJson(@JsonProperty("id") Integer id) {
        if (id == null) return null;
        MPA mpa = fromId(id);
        if (mpa == null) throw new NotFoundException("Рейтинг с id=" + id + " не найден");
        return mpa;
    }
}
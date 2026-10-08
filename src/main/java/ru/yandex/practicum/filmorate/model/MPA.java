package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public enum MPA {
    G(1),
    PG(2),
    PG_13(3),
    R(4),
    NC_17(5);

    private final int id;

    MPA(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static MPA fromId(int id) {
        return Arrays.stream(values())
                .filter(m -> m.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown MPA id: " + id));
    }

    @JsonCreator
    public static MPA fromJson(@JsonProperty("id") Integer id) {
        return id == null ? null : fromId(id);
    }
}

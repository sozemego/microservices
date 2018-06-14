package com.soze.users.dto;

import java.util.Objects;

public class UserDto {

    private final String id;
    private final String name;

    public UserDto(String id, String name) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

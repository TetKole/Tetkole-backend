package com.tetkole.restservice.models;

import java.util.Optional;

public enum Role {
    USER,
    ADMIN,
    MODERATOR,
    CONTRIBUTOR,
    READER;

    public static Optional<Role> getValueFromString(String stringValue) {
        for(Role r: Role.values()) {
            if(r.name().toLowerCase().equals(stringValue)) {
                return Optional.of(r);
            }
        }

        return Optional.empty();
    }
}

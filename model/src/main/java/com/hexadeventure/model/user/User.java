package com.hexadeventure.model.user;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class User {
    private final String id;
    private final String email;
    private final String username;
    private final String password;
    private String mapId;
    
    /**
     * Creates a new user.
     * @param email The user email.
     * @param username The user username.
     * @param password The user password.
     * @throws IllegalArgumentException If the email is empty or null.
     */
    public User(String email, String username, String password) throws IllegalArgumentException {
        this(UUID.randomUUID().toString(), email, username, password, null);
    }
    
    /**
     * Creates a new user.
     * @param id The user id.
     * @param email The user email.
     * @param username The user username.
     * @param password The user password.
     * @throws IllegalArgumentException If the email is empty or null.
     */
    public User(String id, String email, String username, String password,
                String mapId) throws IllegalArgumentException {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.mapId = mapId;
    }
}

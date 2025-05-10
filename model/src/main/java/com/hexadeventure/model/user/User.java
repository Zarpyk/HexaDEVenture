package com.hexadeventure.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class User {
    // Define a custom min date to support database mapping that does not support LocalDateTime.MIN
    public static final LocalDateTime MIN_DATE = LocalDateTime.of(1970, 1, 1, 0, 0);
    
    private final String id;
    private final String email;
    private final String username;
    private final String password;
    private String mapId;
    private int wins;
    private int playedGames;
    private int playedTime;
    private LocalDateTime currentGameStartTime;
    private int travelledDistance;
    private int collectedResources;
    
    /**
     * Creates a new user.
     * @param email The user email.
     * @param username The user username.
     * @param password The user password.
     * @throws IllegalArgumentException If the email is empty or null.
     */
    public User(String email, String username, String password) throws IllegalArgumentException {
        id = UUID.randomUUID().toString();
        this.email = email;
        this.username = username;
        this.password = password;
        currentGameStartTime = User.MIN_DATE;
    }
}

package com.hexadeventure.application.port.out.persistence;

import com.hexadeventure.model.user.User;

import java.util.Optional;

public interface UserRepository {
    /**
     * Finds a user by their email.
     *
     * @param email the email of the user to find
     * @return an Optional containing the found user, or empty if no user is found
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Saves a user to the repository.
     *
     * @param user the user to save
     */
    void save(User user);
    
    /**
     * Deletes all users from the repository.
     */
    void deleteAll();
    
    /**
     * Sets the map ID of a user.
     *
     * @param email the email of the user
     * @param mapId the map ID to set
     */
    void updateMapIdByEmail(String email, String mapId);
    
    /**
     * Deletes a user by their email.
     *
     * @param email the email of the user to delete
     */
    void deleteByEmail(String email);
}

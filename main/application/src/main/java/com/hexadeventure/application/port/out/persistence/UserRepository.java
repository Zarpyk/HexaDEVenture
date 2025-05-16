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
     * Finds a user by their ID.
     *
     * @param userId the ID of the user to find
     * @return an Optional containing the found user, or empty if no user is found
     */
    Optional<User> findById(String userId);
    
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
     * Deletes a user by their email.
     *
     * @param email the email of the user to delete
     */
    void deleteByEmail(String email);
}

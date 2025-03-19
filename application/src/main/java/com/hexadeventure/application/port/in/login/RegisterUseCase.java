package com.hexadeventure.application.port.in.login;

import com.hexadeventure.model.user.User;

public interface RegisterUseCase {
    /**
     * Register a new user to the system
     * @param user the user to register
     */
    void register(User user);
    
    /**
     * Unregister a user from the system and delete all its data
     * @param email the email of the user to unregister
     */
    void unregister(String email);
}

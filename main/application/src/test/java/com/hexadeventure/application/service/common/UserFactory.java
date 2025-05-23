package com.hexadeventure.application.service.common;

import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.model.user.User;

import java.util.Optional;

import static org.mockito.Mockito.when;

public class UserFactory {
    public static final String EMAIL = "test@test.com";
    public static final String USERNAME = "Test User";
    public static final String PASSWORD = "Test_Password1234@";
    public static final User USER = new User(EMAIL, USERNAME, PASSWORD);
    
    public static User createTestUser(UserRepository userRepository) {
        User user = new User(EMAIL, USERNAME, PASSWORD);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        return user;
    }
}

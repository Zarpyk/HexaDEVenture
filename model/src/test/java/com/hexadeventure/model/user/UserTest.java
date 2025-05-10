package com.hexadeventure.model.user;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {
    private static final String EMAIL = "test@test.com";
    private static final String USERNAME = "testUser";
    private static final String PASSWORD = "Test_Password1234@";
    
    @Test
    public void givenAEmailUsernameAndPassword_whenCreatingAUser_thenCreatesAUser() {
        User user = new User(EMAIL, USERNAME, PASSWORD);
        
        assertThat(user.getId()).isNotNull();
        assertThat(user.getEmail()).isEqualTo(EMAIL);
        assertThat(user.getUsername()).isEqualTo(USERNAME);
        assertThat(user.getPassword()).isEqualTo(PASSWORD);
    }
}

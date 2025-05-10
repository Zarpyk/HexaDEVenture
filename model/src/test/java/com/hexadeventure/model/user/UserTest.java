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
    
    /*TODO move this to application layer @Test
    public void givenAnEmptyOrNullEmail_whenCreatingAUser_thenThrowsAnException() {
        ThrowableAssert.ThrowingCallable invokation = () -> new User("", USERNAME, PASSWORD);
        assertThat(invokation).isInstanceOf(IllegalArgumentException.class);
        
        invokation = () -> new User(null, USERNAME, PASSWORD);
        assertThat(invokation).isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    public void givenAWrongEmail_whenCreatingAUser_thenThrowsAnException() {
        ThrowableAssert.ThrowingCallable invokation = () -> new User("wrongEmail", USERNAME, PASSWORD);
        assertThat(invokation).isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    public void givenAEmptyOrNullUserName_whenCreatingAUser_thenThrowsAnException() {
        ThrowableAssert.ThrowingCallable invokation = () -> new User(EMAIL, "", PASSWORD);
        assertThat(invokation).isInstanceOf(IllegalArgumentException.class);
        
        invokation = () -> new User(EMAIL, null, PASSWORD);
        assertThat(invokation).isInstanceOf(IllegalArgumentException.class);
    }
    
    @ParameterizedTest
    @ValueSource(ints = {51, Integer.MAX_VALUE})
    public void givenAUserNameWithMoreThan50Characters_whenCreatingAUser_thenThrowsAnException(int length) {
        ThrowableAssert.ThrowingCallable invokation = () -> new User(EMAIL, "a".repeat(length), PASSWORD);
        assertThat(invokation).isInstanceOf(IllegalArgumentException.class);
    }*/
}

package com.hexadeventure.common;

import com.hexadeventure.model.user.User;

public class UserFactory {
    public static final String EMAIL = "test@test.com";
    public static final String USERNAME = "Test User";
    public static final String PASSWORD = "Test_Password1234@";
    public static final User USER = new User(EMAIL, USERNAME, PASSWORD);
}

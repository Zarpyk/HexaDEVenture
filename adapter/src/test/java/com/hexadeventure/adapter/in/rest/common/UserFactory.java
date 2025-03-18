package com.hexadeventure.adapter.in.rest.common;

import com.hexadeventure.adapter.in.rest.login.UserDTO;

public class UserFactory {
    public static final String EMAIL = "test@test.com";
    public static final String USERNAME = "Test User";
    public static final String PASSWORD = "test_password1234";
    public static final UserDTO USER_DTO = new UserDTO(EMAIL, USERNAME, PASSWORD);
}

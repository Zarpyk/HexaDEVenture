package com.hexadeventure.bootstrap.e2e.common;

import com.hexadeventure.adapter.in.rest.users.dto.in.UserDTO;
import io.restassured.response.Response;

public class UserFactory {
    public static final String EMAIL = "test@test.com";
    public static final String USERNAME = "Test User";
    public static final String PASSWORD = "Test_Password1234@";
    public static final UserDTO USER_DTO = new UserDTO(EMAIL, USERNAME, PASSWORD);
    
    public static void createTestUser(int port) {
        Response response = RestCommon.postWithBody(port, "/register", USER_DTO, false);
    }
    
    public static void deleteTestUser(int port) {
        RestCommon.post(port, "/unregister");
    }
}

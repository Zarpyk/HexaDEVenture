package com.hexadeventure.bootstrap.e2e.common;

public class UserFactory {
    public static final String EMAIL = "test@test.com";
    public static final String USERNAME = "Test User";
    public static final String PASSWORD = "Test_Password1234@";
    
    public static void createTestUser(int port) {
        //Response response = RestCommon.postWithBody(port, "/register", USER_DTO, false);
    }
    
    public static void deleteTestUser(int port) {
        //RestCommon.post(port, "/unregister");
    }
}

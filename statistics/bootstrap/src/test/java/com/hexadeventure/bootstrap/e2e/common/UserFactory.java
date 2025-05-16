package com.hexadeventure.bootstrap.e2e.common;

import com.hexadeventure.adapter.out.game.UserInfoDTO;

public class UserFactory {
    public static final String EMAIL = "test@test.com";
    public static final String USERNAME = "Test User";
    public static final String PASSWORD = "Test_Password1234@";
    
    private static final class UserDTO {
        public String email = EMAIL;
        public String username = USERNAME;
        public String password = PASSWORD;
    }
    
    private static final UserDTO USER_DTO = new UserDTO();
    
    public static String createTestUser(int port) {
        RestCommon.postWithBody(port, "/register", USER_DTO, false);
        UserInfoDTO userInfoDTO = RestCommon.get(port, "/userInfo", true)
                                            .then().extract()
                                            .body().as(UserInfoDTO.class);
        return userInfoDTO.id();
    }
    
    public static void deleteTestUser(int port) {
        RestCommon.post(port, "/unregister");
    }
}

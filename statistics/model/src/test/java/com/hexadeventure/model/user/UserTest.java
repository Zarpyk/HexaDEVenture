package com.hexadeventure.model.user;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {
    @Test
    public void givenArg_whenCreatingAUserInfo_thenCreatesAUserInfo() {
        UserInfo user = new UserInfo(UUID.randomUUID().toString(),
                                     "",
                                     "",
                                     0,
                                     0,
                                     0,
                                     null,
                                     0,
                                     0);
        
        assertThat(user).isNotNull();
    }
}

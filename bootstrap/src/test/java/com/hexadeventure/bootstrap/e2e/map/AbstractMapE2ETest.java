package com.hexadeventure.bootstrap.e2e.map;

import com.hexadeventure.adapter.in.rest.game.StartGameDTO;
import com.hexadeventure.bootstrap.e2e.common.RestCommon;
import com.hexadeventure.bootstrap.e2e.common.UserFactory;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

public abstract class AbstractMapE2ETest {
    
    @LocalServerPort
    private int port;
    
    @BeforeEach
    public void beforeEach() {
        UserFactory.createTestUser(port);
    }
    
    @AfterEach
    public void afterEach() {
        UserFactory.deleteTestUser(port);
    }
    
    @Test
    public void givenUser_whenDontHaveStartedGame_thenReturnCreated() {
        StartGameDTO startGameDTO = new StartGameDTO(1234, 1000);
        
        Response response = RestCommon.postWithBody(port, "/start", startGameDTO);
        response.then().statusCode(HttpStatus.CREATED.value());
    }
}
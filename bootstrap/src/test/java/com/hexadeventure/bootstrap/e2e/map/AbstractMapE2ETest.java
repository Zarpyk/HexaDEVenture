package com.hexadeventure.bootstrap.e2e.map;

import com.hexadeventure.adapter.in.rest.game.dto.in.StartGameDTO;
import com.hexadeventure.application.service.game.GameService;
import com.hexadeventure.bootstrap.e2e.common.RestCommon;
import com.hexadeventure.bootstrap.e2e.common.UserFactory;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

public abstract class AbstractMapE2ETest {
    private static final long TEST_SEED = 1234;
    private static final int TEST_SIZE = GameService.MIN_MAP_SIZE;
    
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
        StartGameDTO startGameDTO = new StartGameDTO(TEST_SEED, TEST_SIZE);
        
        Response response = RestCommon.postWithBody(port, "/start", startGameDTO);
        response.then().statusCode(HttpStatus.CREATED.value());
    }
}
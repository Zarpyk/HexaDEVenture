package com.hexadeventure.bootstrap.e2e.users;

import com.hexadeventure.adapter.in.rest.users.dto.out.AverageDistanceDTO;
import com.hexadeventure.adapter.in.rest.users.dto.out.AverageTimeDTO;
import com.hexadeventure.adapter.in.rest.users.dto.out.WinRateDTO;
import com.hexadeventure.bootstrap.e2e.common.GameServiceTestContainer;
import com.hexadeventure.bootstrap.e2e.common.RestCommon;
import com.hexadeventure.bootstrap.e2e.common.UserFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class StatsE2ETest implements GameServiceTestContainer {
    
    @LocalServerPort
    private int port;
    
    private static int gameServicePort;
    
    private String userId;
    
    @BeforeAll
    public static void beforeAll() {
        gameServicePort = GameServiceTestContainer.port();
    }
    
    @BeforeEach
    public void beforeEach() {
        userId = UserFactory.createTestUser(gameServicePort);
    }
    
    @AfterEach
    public void afterEach() {
        UserFactory.deleteTestUser(gameServicePort);
    }
    
    @Test
    public void givenUser_whenGetWinRate_thenReturnOkWithDTO() {
        RestCommon.get(port, "/winRate?userId=" + userId, false)
                  .then().statusCode(HttpStatus.OK.value())
                  .extract().body().as(WinRateDTO.class);
    }
    
    @Test
    public void givenRandomUser_whenGetWinRate_thenReturnBadRequest() {
        String randomUserId = "random";
        RestCommon.get(port, "/winRate?userId=" + randomUserId, false)
                  .then().statusCode(HttpStatus.BAD_REQUEST.value());
    }
    
    @Test
    public void givenUser_whenGetAverageTime_thenReturnOkWithDTO() {
        RestCommon.get(port, "/averageTime?userId=" + userId, false)
                  .then().statusCode(HttpStatus.OK.value())
                  .extract().body().as(AverageTimeDTO.class);
    }
    
    @Test
    public void givenRandomUser_whenGetAverageTime_thenReturnBadRequest() {
        String randomUserId = "random";
        RestCommon.get(port, "/averageTime?userId=" + randomUserId, false)
                  .then().statusCode(HttpStatus.BAD_REQUEST.value());
    }
    
    @Test
    public void givenUser_whenGetAverageDistance_thenReturnOkWithDTO() {
        RestCommon.get(port, "/averageDistance?userId=" + userId, false)
                  .then().statusCode(HttpStatus.OK.value())
                  .extract().body().as(AverageDistanceDTO.class);
    }
    
    @Test
    public void givenRandomUser_whenGetAverageDistance_thenReturnBadRequest() {
        String randomUserId = "random";
        RestCommon.get(port, "/averageDistance?userId=" + randomUserId, false)
                  .then().statusCode(HttpStatus.BAD_REQUEST.value());
    }
}

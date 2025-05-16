package com.hexadeventure.adapter.in.rest.users;

import com.hexadeventure.adapter.in.rest.common.RestCommon;
import com.hexadeventure.adapter.in.rest.users.dto.out.AverageDistanceDTO;
import com.hexadeventure.adapter.in.rest.users.dto.out.AverageTimeDTO;
import com.hexadeventure.adapter.in.rest.users.dto.out.WinRateDTO;
import com.hexadeventure.application.exceptions.UserNotFoundException;
import com.hexadeventure.application.port.in.users.StatsUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StatsTest {
    private final static String USER_ID = UUID.randomUUID().toString();
    
    private final StatsUseCase statsUseCase = mock(StatsUseCase.class);
    
    @BeforeEach
    public void beforeEach() {
        RestCommon.Setup(new StatsController(statsUseCase));
    }
    
    @Test
    public void givenAUser_whenGettingAverageTime_thenReturnsTheAverageTime() {
        when(statsUseCase.getWinRate(USER_ID)).thenReturn(0d);
        RestCommon.get("/winRate?userId=" + USER_ID, false)
                  .then()
                  .statusCode(HttpStatus.OK.value())
                  .extract().body().as(WinRateDTO.class);
    }
    
    @Test
    public void givenAUser_whenGettingAverageTime_thenReturnAverageTime() {
        when(statsUseCase.getAverageTime(USER_ID)).thenReturn(0d);
        RestCommon.get("/averageTime?userId=" + USER_ID, false)
                  .then()
                  .statusCode(HttpStatus.OK.value())
                  .extract().body().as(AverageTimeDTO.class);
    }
    
    @Test
    public void givenAUser_whenGettingAverageDistance_thenReturnAverageDistance() {
        when(statsUseCase.getAverageDistance(USER_ID)).thenReturn(0d);
        RestCommon.get("/averageDistance?userId=" + USER_ID, false)
                  .then()
                  .statusCode(HttpStatus.OK.value())
                  .extract().body().as(AverageDistanceDTO.class);
    }
    
    @Test
    void givenInvalidUserId_whenGettingWinRate_thenReturnBadRequest() {
        when(statsUseCase.getWinRate(anyString())).thenThrow(new UserNotFoundException());
        RestCommon.get("/winRate?userId=invalid", false)
                  .then()
                  .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}

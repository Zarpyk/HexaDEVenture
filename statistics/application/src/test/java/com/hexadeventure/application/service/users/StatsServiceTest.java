package com.hexadeventure.application.service.users;

import com.hexadeventure.application.exceptions.UserNotFoundException;
import com.hexadeventure.application.port.out.game.GameRestPort;
import com.hexadeventure.model.user.UserInfo;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StatsServiceTest {
    private final static UserInfo USER_INFO = new UserInfo(UUID.randomUUID().toString(),
                                                           "test@test.com",
                                                           "Test User",
                                                           2,
                                                           3,
                                                           1000,
                                                           null,
                                                           4000,
                                                           200);
    private final static UserInfo USER_INFO_EMPTY = new UserInfo(UUID.randomUUID().toString(),
                                                                 "test@test.com",
                                                                 "Test User",
                                                                 0,
                                                                 0,
                                                                 0,
                                                                 null,
                                                                 0,
                                                                 0);
    
    private final GameRestPort gameRestPort = mock(GameRestPort.class);
    
    private final StatsService statsService = new StatsService(gameRestPort);
    
    @Test
    public void givenUserId_whenGetWinRate_thenReturnWinRate() {
        when(gameRestPort.getUserInfo(USER_INFO.getId())).thenReturn(USER_INFO);
        
        double winRate = statsService.getWinRate(USER_INFO.getId());
        
        assertThat(winRate).isEqualTo((double) USER_INFO.getWins() / USER_INFO.getPlayedGames());
    }
    
    @Test
    public void givenUserWith0Play_whenGetWinRate_thenReturn0() {
        when(gameRestPort.getUserInfo(USER_INFO_EMPTY.getId())).thenReturn(USER_INFO_EMPTY);
        
        double winRate = statsService.getWinRate(USER_INFO_EMPTY.getId());
        
        assertThat(winRate).isEqualTo(0);
    }
    
    @Test
    public void givenInvalidUserId_whenGetWinRate_thenThrowException() {
        when(gameRestPort.getUserInfo(USER_INFO.getId())).thenReturn(null);
        
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> statsService.getWinRate(USER_INFO.getId()));
    }
    
    @Test
    public void givenUserId_whenGetAverageTime_thenReturnAverageTime() {
        when(gameRestPort.getUserInfo(USER_INFO.getId())).thenReturn(USER_INFO);
        
        double averageTime = statsService.getAverageTime(USER_INFO.getId());
        
        assertThat(averageTime).isEqualTo((double) USER_INFO.getPlayedTime() /
                                          USER_INFO.getPlayedGames());
    }
    
    @Test
    public void givenUserWith0Play_whenGetAverageTime_thenReturn0() {
        when(gameRestPort.getUserInfo(USER_INFO_EMPTY.getId())).thenReturn(USER_INFO_EMPTY);
        
        double averageTime = statsService.getAverageTime(USER_INFO_EMPTY.getId());
        
        assertThat(averageTime).isEqualTo(0);
    }
    
    @Test
    public void givenInvalidUserId_whenGetAverageTime_thenThrowException() {
        when(gameRestPort.getUserInfo(USER_INFO.getId())).thenReturn(null);
        
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> statsService.getAverageTime(USER_INFO.getId()));
    }
    
    @Test
    public void givenUserId_whenGetAverageDistance_thenReturnTravelledDistance() {
        when(gameRestPort.getUserInfo(USER_INFO.getId())).thenReturn(USER_INFO);
        
        double travelledDistance = statsService.getAverageDistance(USER_INFO.getId());
        
        assertThat(travelledDistance).isEqualTo((double) USER_INFO.getTravelledDistance() /
                                                USER_INFO.getPlayedGames());
    }
    
    @Test
    public void givenUserWith0Play_whenGetAverageDistance_thenReturn0() {
        when(gameRestPort.getUserInfo(USER_INFO_EMPTY.getId())).thenReturn(USER_INFO_EMPTY);
        
        double travelledDistance = statsService.getAverageDistance(USER_INFO_EMPTY.getId());
        
        assertThat(travelledDistance).isEqualTo(0);
    }
    
    @Test
    public void givenInvalidUserId_whenGetAverageDistance_thenThrowException() {
        when(gameRestPort.getUserInfo(USER_INFO.getId())).thenReturn(null);
        
        assertThatExceptionOfType(UserNotFoundException.class)
                .isThrownBy(() -> statsService.getAverageDistance(USER_INFO.getId()));
    }
}

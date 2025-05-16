package com.hexadeventure.application.service.users;


import com.hexadeventure.application.exceptions.UserNotFoundException;
import com.hexadeventure.application.port.in.users.StatsUseCase;
import com.hexadeventure.application.port.out.game.GameRestPort;
import com.hexadeventure.model.user.UserInfo;

public class StatsService implements StatsUseCase {
    private final GameRestPort gameRestPort;
    
    public StatsService(GameRestPort gameRestPort) {
        this.gameRestPort = gameRestPort;
    }
    
    @Override
    public double getWinRate(String userId) {
        UserInfo userInfo = gameRestPort.getUserInfo(userId);
        if(userInfo == null) throw new UserNotFoundException();
        if (userInfo.getPlayedGames() == 0) return 0;
        return (double) userInfo.getWins() / userInfo.getPlayedGames();
    }
    
    @Override
    public double getAverageTime(String userId) {
        UserInfo userInfo = gameRestPort.getUserInfo(userId);
        if(userInfo == null) throw new UserNotFoundException();
        if (userInfo.getPlayedGames() == 0) return 0;
        return (double) userInfo.getPlayedTime() / userInfo.getPlayedGames();
    }
    
    @Override
    public double getAverageDistance(String userId) {
        UserInfo userInfo = gameRestPort.getUserInfo(userId);
        if(userInfo == null) throw new UserNotFoundException();
        if (userInfo.getPlayedGames() == 0) return 0;
        return (double) userInfo.getTravelledDistance() / userInfo.getPlayedGames();
    }
}

package com.hexadeventure.adapter.out.game;

import com.hexadeventure.model.user.UserInfo;

import java.time.LocalDateTime;

public record UserInfoDTO(String id,
                          int wins,
                          int playedGames,
                          int playedTime,
                          LocalDateTime currentGameStartTime,
                          int travelledDistance,
                          int collectedResources) {
    public UserInfo toModel() {
        return new UserInfo(id,
                            wins,
                            playedGames,
                            playedTime,
                            currentGameStartTime,
                            travelledDistance,
                            collectedResources);
    }
}

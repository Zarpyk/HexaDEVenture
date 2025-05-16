package com.hexadeventure.adapter.in.rest.users.dto.out;

import com.hexadeventure.model.user.User;

import java.time.LocalDateTime;

public record UserInfoDTO(String id,
                          int wins,
                          int playedGames,
                          int playedTime,
                          LocalDateTime currentGameStartTime,
                          int travelledDistance,
                          int collectedResources) {
    public static UserInfoDTO fromModel(User model) {
        return new UserInfoDTO(model.getId(),
                               model.getWins(),
                               model.getPlayedGames(),
                               model.getPlayedTime(),
                               model.getCurrentGameStartTime(),
                               model.getTravelledDistance(),
                               model.getCollectedResources());
    }
}

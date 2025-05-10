package com.hexadeventure.adapter.out.persistence.users.jpa;

import com.hexadeventure.model.user.User;

public class UserJpaMapper {
    public static UserJpaEntity toEntity(User user) {
        return new UserJpaEntity(user.getId(),
                                 user.getEmail(),
                                 user.getUsername(),
                                 user.getPassword(),
                                 user.getMapId(),
                                 user.getWins(),
                                 user.getPlayedGames(),
                                 user.getPlayedTime(),
                                 user.getCurrentGameStartTime(),
                                 user.getTravelledDistance(),
                                 user.getCollectedResources());
    }
    
    public static User toModel(UserJpaEntity entity) {
        return new User(entity.getId(),
                        entity.getEmail(),
                        entity.getUsername(),
                        entity.getPassword(),
                        entity.getMapId(),
                        entity.getWins(),
                        entity.getPlayedGames(),
                        entity.getPlayedTime(),
                        entity.getCurrentGameStartTime(),
                        entity.getTravelledDistance(),
                        entity.getCollectedResources());
    }
}

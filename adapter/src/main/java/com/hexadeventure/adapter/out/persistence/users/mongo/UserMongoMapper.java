package com.hexadeventure.adapter.out.persistence.users.mongo;

import com.hexadeventure.model.user.User;

public class UserMongoMapper {
    @SuppressWarnings("DuplicatedCode")
    public static UserMongoEntity toEntity(User user) {
        UserMongoEntity entity = new UserMongoEntity();
        entity.setId(user.getId());
        entity.setEmail(user.getEmail());
        entity.setUsername(user.getUsername());
        entity.setPassword(user.getPassword());
        entity.setMapId(user.getMapId());
        entity.setWins(user.getWins());
        entity.setPlayedGames(user.getPlayedGames());
        entity.setPlayedTime(user.getPlayedTime());
        entity.setCurrentGameStartTime(user.getCurrentGameStartTime());
        entity.setTravelledDistance(user.getTravelledDistance());
        entity.setCollectedResources(user.getCollectedResources());
        return entity;
    }
    
    public static User toModel(UserMongoEntity entity) {
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

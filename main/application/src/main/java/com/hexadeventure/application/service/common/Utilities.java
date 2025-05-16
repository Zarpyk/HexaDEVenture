package com.hexadeventure.application.service.common;

import com.hexadeventure.application.exceptions.GameNotStartedException;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.model.map.GameMap;
import com.hexadeventure.model.user.User;

import java.util.Optional;

public class Utilities {
    public static GameMap getUserGameMap(String email, UserRepository userRepository,
                                         GameMapRepository gameMapRepository) {
        User user = getUser(email, userRepository);
        return getGameMap(user, gameMapRepository);
    }
    
    public static User getUser(String email, UserRepository userRepository) {
        Optional<User> user = userRepository.findByEmail(email);
        assert user.isPresent();
        return user.get();
    }
    
    public static GameMap getGameMap(User user, GameMapRepository gameMapRepository) {
        // Check if the game has started
        if(user.getMapId() == null) throw new GameNotStartedException();
        
        Optional<GameMap> map = gameMapRepository.findById(user.getMapId());
        assert map.isPresent();
        return map.get();
    }
}

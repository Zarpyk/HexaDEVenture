package com.hexadeventure.application.service.common;

import com.hexadeventure.application.exceptions.GameNotStartedException;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.model.map.GameMap;
import com.hexadeventure.model.user.User;

import java.util.Optional;

public class Utilities {
    public static GameMap getGameMap(String email, UserRepository userRepository, GameMapRepository gameMapRepository) {
        Optional<User> user = userRepository.findByEmail(email);
        assert user.isPresent();
        
        // Check if the game has started
        if(user.get().getMapId() == null) throw new GameNotStartedException();
        
        Optional<GameMap> map = gameMapRepository.findById(user.get().getMapId());
        assert map.isPresent();
        return map.get();
    }
}

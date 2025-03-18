package com.hexadeventure.application.port.out.persistence;

import com.hexadeventure.model.map.GameMap;

import java.util.Optional;

public interface GameMapRepository {
    /**
     * Finds a GameMap by its ID.
     *
     * @param id the ID of the GameMap to find
     * @return an Optional containing the found GameMap, or an empty Optional if not found
     */
    Optional<GameMap> findById(String id);
    
    /**
     * Saves a new GameMap.
     *
     * @param newMap the GameMap to save
     */
    void save(GameMap newMap);
}

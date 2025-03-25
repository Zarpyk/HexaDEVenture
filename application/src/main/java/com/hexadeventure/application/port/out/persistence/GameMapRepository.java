package com.hexadeventure.application.port.out.persistence;

import com.hexadeventure.model.map.GameMap;
import com.hexadeventure.model.map.Vector2C;

import java.util.Collection;
import java.util.Optional;

public interface GameMapRepository {
    /**
     * Finds a GameMap by its ID. <br>
     * If the GameMap is found, it will not contain any chunks.
     *
     * @param id the ID of the GameMap to find
     * @return an Optional containing the found GameMap, or an empty Optional if not found
     */
    Optional<GameMap> findById(String id);
    
    /**
     * Finds a GameMap by its ID and retrieves the specified chunks.
     *
     * @param id the ID of the GameMap to find
     * @param positions the positions of the chunks to retrieve
     * @return an Optional containing the found GameMap with the specified chunks, or an empty Optional if not found
     */
    Optional<GameMap> findByIdAndGetChunks(String id, Collection<Vector2C> positions);
    
    /**
     * Saves a new GameMap.
     *
     * @param gameMap the GameMap to save
     */
    void save(GameMap gameMap);
    
    /**
     * Deletes a GameMap by its ID.
     *
     * @param mapId the ID of the GameMap to delete
     */
    void deleteById(String mapId);
}

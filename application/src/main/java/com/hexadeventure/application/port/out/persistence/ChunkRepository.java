package com.hexadeventure.application.port.out.persistence;

import com.hexadeventure.model.map.Chunk;
import com.hexadeventure.model.map.Vector2C;

import java.util.Collection;
import java.util.List;

public interface ChunkRepository {
    /**
     * Retrieves chunks for a given map at specified positions.
     *
     * @param mapId The unique identifier of the map
     * @param positions List of vector positions where chunks should be found
     * @return List of chunks at the specified positions, or null if no chunks are found with the given mapId
     */
    List<Chunk> findChunks(String mapId, Collection<Vector2C> positions);
    
    /**
     * Saves a list of chunks for a specific map. <br>
     *
     * @param mapId The unique identifier of the map
     * @param chunks List of chunks to be saved
     */
    void saveChunks(String mapId, Collection<Chunk> chunks);
    
    /**
     * Deletes all chunks associated with a specific map.
     *
     * @param mapId The unique identifier of the map to delete chunks from
     */
    void deleteByMapId(String mapId);
}

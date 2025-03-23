package com.hexadeventure.application.port.out.pathfinder;

import com.hexadeventure.model.map.Vector2;

import java.util.Map;
import java.util.Queue;

public interface AStarPathfinder {
    /**
     * Generates a path from the start position to the end position on the given map.
     *
     * @param start the start position
     * @param end the end position
     * @param mapCost the cost of each cell in the map
     * @return a queue of Vector2 representing the path from start to end
     */
    Queue<Vector2> generatePath(Vector2 start, Vector2 end, Map<Vector2, Integer> mapCost);
}

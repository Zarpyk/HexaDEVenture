package com.hexadeventure.adapter.out.pathfinder;

import com.hexadeventure.model.map.Vector2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class AStarAdapterTest {
    private final static Vector2 start = new Vector2(0, 0);
    private final static Vector2 end = new Vector2(4, 4);
    private final static int[][] costMapValues = {
            {1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1},
            };
    // Map to hash map with the array position as Vector2 key
    private final static Map<Vector2, Integer> costMap = new HashMap<>();
    
    private final static int pathLength = 9;
    
    private final static int[][] costMapWithObstaclesValues = {
            {1, 1, 10, 1, 1},
            {1, 1, 10, 1, 1},
            {1, 1, 10, 1, 1},
            {1, 1, 10, 10, 10},
            {1, 1, 1, 10, 1},
            };
    private final static Map<Vector2, Integer> costMapWithObstacles = new HashMap<>();
    private final static int pathLengthWithObstacles = 9;
    private final static Vector2 mapWithObstaclesCheckPosition = new Vector2(4, 2);
    
    private final static int[][] noSolutionCostMapValues = {
            {1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1},
            {-1, -1, -1, -1, -1},
            {1, 1, 1, 1, 1},
            };
    private final static Map<Vector2, Integer> noSolutionCostMap = new HashMap<>();
    
    
    private final AStarAdapter aStarAdapter = new AStarAdapter();
    
    static {
        for (int i = 0; i < costMapValues.length; i++) {
            for (int j = 0; j < costMapValues[i].length; j++) {
                costMap.put(new Vector2(i, j), costMapValues[i][j]);
            }
        }
        for (int i = 0; i < costMapWithObstaclesValues.length; i++) {
            for (int j = 0; j < costMapWithObstaclesValues[i].length; j++) {
                costMapWithObstacles.put(new Vector2(i, j), costMapWithObstaclesValues[i][j]);
            }
        }
        for (int i = 0; i < noSolutionCostMapValues.length; i++) {
            for (int j = 0; j < noSolutionCostMapValues[i].length; j++) {
                noSolutionCostMap.put(new Vector2(i, j), noSolutionCostMapValues[i][j]);
            }
        }
    }
    
    @Test
    public void whenGeneratePath_thenPathIsReturn() {
        Queue<Vector2> path = aStarAdapter.generatePath(start, end, costMap);
        assertThat(path).isNotNull();
        assertThat(path.size()).isEqualTo(pathLength);
        assertThat(path.peek()).isEqualTo(start);
        Vector2 finalPosition = path.poll();
        while (!path.isEmpty()) {
            finalPosition = path.poll();
        }
        assertThat(finalPosition).isEqualTo(end);
    }
    
    @Test
    public void whenGeneratePathWithObstacles_thenPathIsReturn() {
        Queue<Vector2> path = aStarAdapter.generatePath(start, end, costMapWithObstacles);
        assertThat(path).isNotNull();
        assertThat(path.size()).isEqualTo(pathLengthWithObstacles);
        assertThat(path.peek()).isEqualTo(start);
        boolean checkPositionFound = false;
        Vector2 finalPosition = path.poll();
        while (!path.isEmpty()) {
            finalPosition = path.poll();
            if(finalPosition.equals(mapWithObstaclesCheckPosition)) {
                checkPositionFound = true;
            }
        }
        assertThat(checkPositionFound).isTrue();
        assertThat(finalPosition).isEqualTo(end);
    }
    
    @Test
    public void whenGeneratePathWithNoSolution_thenNullIsReturn() {
        Queue<Vector2> path = aStarAdapter.generatePath(start, end, noSolutionCostMap);
        assertThat(path).isNull();
    }
}

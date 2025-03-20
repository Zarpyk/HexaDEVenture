package com.hexadeventure.model.map;

import com.hexadeventure.common.UserFactory;
import com.hexadeventure.model.enemies.Enemy;
import com.hexadeventure.model.map.enemies.EnemyCell;
import com.hexadeventure.model.map.obstacles.ObstacleCell;
import com.hexadeventure.model.map.obstacles.ObstacleType;
import com.hexadeventure.model.map.resources.ResourceCell;
import com.hexadeventure.model.map.resources.ResourceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

public class MapTest {
    private final static long SEED = 1234;
    private final static int SIZE = 10;
    
    @Test
    public void givenASize_whenCreatingAMap_thenCreatesAMapWithCorrectSize() {
        GameMap gameMap = new GameMap(UserFactory.EMAIL, SEED, SIZE);
        assertThat(gameMap.getMapSize()).isEqualTo(SIZE);
    }
    
    @ParameterizedTest(name = "Given a {0} threshold, then create EmptyCell")
    @ValueSource(doubles = {-1, GameMap.EMPTY_THRESHOLD - 0.01})
    public void givenAThresholdMoreThanObstacle_whenCreateCell_thenReturnsEmptyCell(double noiseValue) {
        GameMap gameMap = new GameMap(UserFactory.EMAIL, SEED, SIZE);
        Vector2 position = new Vector2(1, 1);
        gameMap.createCell(noiseValue, position.x, position.y);
        
        assertThat(gameMap.getCell(position).getType()).isEqualTo(CellType.EMPTY);
    }
    
    @ParameterizedTest(name = "Given a {0} threshold, then create ObstacleCell")
    @ValueSource(doubles = {GameMap.EMPTY_THRESHOLD, 1.0})
    public void givenAPositionAndLessThanObstacleThresholdNoise_whenCreateCell_thenReturnsObstableCell(
            double noiseValue) {
        GameMap gameMap = new GameMap(UserFactory.EMAIL, SEED, SIZE);
        Vector2 position = new Vector2(1, 1);
        gameMap.createCell(noiseValue, position.x, position.y);
        
        assertThat(gameMap.getCell(position).getType()).isEqualTo(CellType.OBSTACLE);
    }
    
    @Test
    public void givenAnObstacleCell_whenAddingItToTheMap_thenAddsTheObstacleCell() {
        GameMap gameMap = new GameMap(UserFactory.EMAIL, SEED, SIZE);
        Vector2 position = new Vector2(1, 1);
        ObstacleCell cell = new ObstacleCell(position, ObstacleType.WALL);
        gameMap.setCell(position, cell);
        
        assertThat(gameMap.getCell(position).getType()).isEqualTo(CellType.OBSTACLE);
        assertThat(((ObstacleCell) gameMap.getCell(position)).getObstacleType()).isEqualTo(ObstacleType.WALL);
    }
    
    @Test
    public void givenAResourceCell_whenAddingItToTheMap_thenAddsTheResourceCell() {
        GameMap gameMap = new GameMap(UserFactory.EMAIL, SEED, SIZE);
        Vector2 position = new Vector2(1, 1);
        ResourceCell cell = new ResourceCell(position, ResourceType.WOOD, 5);
        gameMap.setCell(position, cell);
        
        assertThat(gameMap.getCell(position).getType()).isEqualTo(CellType.RESOURCE);
        assertThat(((ResourceCell) gameMap.getCell(position)).getResourceType()).isEqualTo(ResourceType.WOOD);
        assertThat(((ResourceCell) gameMap.getCell(position)).getQuantity()).isEqualTo(5);
    }
    
    @Test
    public void givenAnEnemyCell_whenAddingItToTheMap_thenAddsTheEnemyCell() {
        GameMap gameMap = new GameMap(UserFactory.EMAIL, SEED, SIZE);
        Vector2 position = new Vector2(1, 1);
        Enemy enemy = new Enemy();
        EnemyCell cell = new EnemyCell(position, enemy);
        gameMap.setCell(position, cell);
        
        assertThat(gameMap.getCell(position).getType()).isEqualTo(CellType.ENEMY);
    }
    
    @Test
    public void givenAnCoordinate_whenInitMainCharacter_thenAddsTheCharacterToTheMap() {
        GameMap gameMap = new GameMap(UserFactory.EMAIL, SEED, SIZE);
        Vector2 position = new Vector2(1, 1);
        
        gameMap.initMainCharacter(position);
        
        assertThat(gameMap.getMainCharacter()).isNotNull();
        assertThat(gameMap.getMainCharacter().getPosition()).isEqualTo(position);
    }
}

package com.hexadeventure.model.map;

import com.hexadeventure.common.UserFactory;
import com.hexadeventure.model.enemies.Enemy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class MapTest {
    private final static long SEED = 1234;
    private final static int SIZE = 10;
    
    @Test
    public void givenASize_whenCreatingAMap_thenCreatesAMapWithCorrectSize() {
        GameMap gameMap = new GameMap(UserFactory.EMAIL, SEED, SIZE);
        assertThat(gameMap.getMapSize()).isEqualTo(SIZE);
    }
    
    @ParameterizedTest(name = "Given a {0} threshold, then create GROUND")
    @ValueSource(doubles = {-1, GameMap.EMPTY_THRESHOLD - 0.01})
    public void givenAThresholdMoreThanObstacle_whenCreateCell_thenReturnsEmptyCell(double noiseValue) {
        GameMap gameMap = new GameMap(UserFactory.EMAIL, SEED, SIZE);
        Vector2 position = new Vector2(1, 1);
        gameMap.createCell(noiseValue, position.x, position.y);
        
        assertThat(gameMap.getCell(position).getType()).isEqualTo(CellType.GROUND);
    }
    
    @ParameterizedTest(name = "Given a {0} threshold, then create WALL")
    @ValueSource(doubles = {GameMap.EMPTY_THRESHOLD, 1.0})
    public void givenAPositionAndLessThanObstacleThresholdNoise_whenCreateCell_thenReturnsObstableCell(
            double noiseValue) {
        GameMap gameMap = new GameMap(UserFactory.EMAIL, SEED, SIZE);
        Vector2 position = new Vector2(1, 1);
        gameMap.createCell(noiseValue, position.x, position.y);
        
        assertThat(gameMap.getCell(position).getType()).isEqualTo(CellType.WALL);
    }
    
    @Test
    public void givenAnGroundType_whenAddingItToTheMap_thenAddsTheGroundCell() {
        GameMap gameMap = new GameMap(UserFactory.EMAIL, SEED, SIZE);
        Vector2 position = new Vector2(1, 1);
        CellType cellType = CellType.GROUND;
        gameMap.setCell(position, cellType);
        
        assertThat(gameMap.getCell(position).getType()).isEqualTo(CellType.GROUND);
    }
    
    @Test
    public void givenAnPathType_whenAddingItToTheMap_thenAddsThePathCell() {
        GameMap gameMap = new GameMap(UserFactory.EMAIL, SEED, SIZE);
        Vector2 position = new Vector2(1, 1);
        CellType cellType = CellType.PATH;
        gameMap.setCell(position, cellType);
        
        assertThat(gameMap.getCell(position).getType()).isEqualTo(CellType.PATH);
    }
    
    @Test
    public void givenAnWallType_whenAddingItToTheMap_thenAddsTheWallCell() {
        GameMap gameMap = new GameMap(UserFactory.EMAIL, SEED, SIZE);
        Vector2 position = new Vector2(1, 1);
        CellType cellType = CellType.WALL;
        gameMap.setCell(position, cellType);
        
        assertThat(gameMap.getCell(position).getType()).isEqualTo(CellType.WALL);
    }
    
    @Test
    public void givenAResource_whenAddingItToTheMap_thenAddsTheResourceToThePosition() {
        GameMap gameMap = new GameMap(UserFactory.EMAIL, SEED, SIZE);
        Vector2 position = new Vector2(1, 1);
        gameMap.addResource(position, 0, new Random());
        
        assertThat(gameMap.getResource(position)).isNotNull();
    }
    
    @Test
    public void givenAnEnemyCell_whenAddingItToTheMap_thenAddsTheEnemyCell() {
        GameMap gameMap = new GameMap(UserFactory.EMAIL, SEED, SIZE);
        Vector2 position = new Vector2(1, 1);
        Enemy enemy = new Enemy(position);
        gameMap.addEnemy(position, enemy);
        
        assertThat(gameMap.getEnemy(position)).isNotNull();
        assertThat(gameMap.getEnemy(position)).isEqualTo(enemy);
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

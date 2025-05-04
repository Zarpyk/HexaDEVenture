package com.hexadeventure.model.map;

import com.hexadeventure.common.EnemyFactory;
import com.hexadeventure.common.GameMapFactory;
import com.hexadeventure.common.WeaponFactory;
import com.hexadeventure.model.enemies.Enemy;
import com.hexadeventure.model.inventory.characters.EnemyPattern;
import com.hexadeventure.model.inventory.weapons.WeaponSetting;
import com.hexadeventure.model.inventory.weapons.WeaponType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;

import static org.assertj.core.api.Assertions.assertThat;

public class MapTest {
    @Test
    public void givenASize_whenCreatingAMap_thenCreatesAMapWithCorrectSize() {
        GameMap gameMap = GameMapFactory.createGameMap();
        assertThat(gameMap.getSize()).isEqualTo(GameMapFactory.SIZE);
    }
    
    @ParameterizedTest(name = "Given a {0} threshold, then create GROUND")
    @ValueSource(doubles = {-1, CellData.EMPTY_THRESHOLD - 0.01})
    public void givenThresholdLessThanEmpty_whenCreateCell_thenReturnsGroundCell(double noiseValue) {
        GameMap gameMap = GameMapFactory.createGameMap();
        Vector2 position = new Vector2(1, 1);
        gameMap.createCell(noiseValue, position);
        
        assertThat(gameMap.getCell(position).getType()).isEqualTo(CellType.GROUND);
    }
    
    @ParameterizedTest(name = "Given a {0} threshold, then create GROUND")
    @ValueSource(doubles = {CellData.EMPTY_THRESHOLD, CellData.EMPTY2_THRESHOLD - 0.01})
    public void givenThresholdLessThanEmpty2_whenCreateCell_thenReturnsGround2Cell(double noiseValue) {
        GameMap gameMap = GameMapFactory.createGameMap();
        Vector2 position = new Vector2(1, 1);
        gameMap.createCell(noiseValue, position);
        
        assertThat(gameMap.getCell(position).getType()).isEqualTo(CellType.GROUND2);
    }
    
    @ParameterizedTest(name = "Given a {0} threshold, then create WALL")
    @ValueSource(doubles = {CellData.EMPTY2_THRESHOLD, 1.0})
    public void givenThresholdMoreThanEmpty2_whenCreateCell_thenReturnsWallCell(double noiseValue) {
        GameMap gameMap = GameMapFactory.createGameMap();
        Vector2 position = new Vector2(1, 1);
        gameMap.createCell(noiseValue, position);
        
        assertThat(gameMap.getCell(position).getType()).isEqualTo(CellType.WALL);
    }
    
    @Test
    public void givenAnGroundType_whenAddingItToTheMap_thenAddsTheGroundCell() {
        GameMap gameMap = GameMapFactory.createGameMap();
        Vector2 position = new Vector2(1, 1);
        CellType cellType = CellType.GROUND;
        gameMap.setCell(position, cellType);
        
        assertThat(gameMap.getCell(position).getType()).isEqualTo(CellType.GROUND);
    }
    
    @Test
    public void givenAnPathType_whenAddingItToTheMap_thenAddsThePathCell() {
        GameMap gameMap = GameMapFactory.createGameMap();
        Vector2 position = new Vector2(1, 1);
        CellType cellType = CellType.PATH;
        gameMap.setCell(position, cellType);
        
        assertThat(gameMap.getCell(position).getType()).isEqualTo(CellType.PATH);
    }
    
    @Test
    public void givenAnWallType_whenAddingItToTheMap_thenAddsTheWallCell() {
        GameMap gameMap = GameMapFactory.createGameMap();
        Vector2 position = new Vector2(1, 1);
        CellType cellType = CellType.WALL;
        gameMap.setCell(position, cellType);
        
        assertThat(gameMap.getCell(position).getType()).isEqualTo(CellType.WALL);
    }
    
    @Test
    public void givenAResource_whenAddingItToTheMap_thenAddsTheResourceToThePosition() {
        GameMap gameMap = GameMapFactory.createGameMap();
        Vector2 position = new Vector2(1, 1);
        gameMap.addResource(position, 0, new SplittableRandom());
        
        assertThat(gameMap.getResource(position)).isNotNull();
    }
    
    @Test
    public void givenAnEnemyCell_whenAddingItToTheMap_thenAddsTheEnemyCell() {
        GameMap gameMap = GameMapFactory.createGameMap();
        Vector2 position = new Vector2(1, 1);
        SplittableRandom random = new SplittableRandom();
        EnemyPattern pattern = EnemyFactory.createEnemyPattern();
        Map<WeaponType, List<WeaponSetting>> weapons = WeaponFactory.createWeaponsSettings();
        Enemy enemy = new Enemy(position, random, pattern, weapons);
        gameMap.addEnemy(position, enemy);
        
        assertThat(gameMap.getEnemy(position)).isNotNull();
        assertThat(gameMap.getEnemy(position)).isEqualTo(enemy);
    }
    
    @Test
    public void givenAnCoordinate_whenInitMainCharacter_thenAddsTheCharacterToTheMap() {
        GameMap gameMap = GameMapFactory.createGameMap();
        Vector2 position = new Vector2(1, 1);
        
        gameMap.getMainCharacter().setPosition(position);
        
        assertThat(gameMap.getMainCharacter()).isNotNull();
        assertThat(gameMap.getMainCharacter().getPosition()).isEqualTo(position);
    }
}

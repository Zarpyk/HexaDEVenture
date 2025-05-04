package com.hexadeventure.model.combat;

import com.hexadeventure.common.CharacterFactory;
import com.hexadeventure.common.GameMapFactory;
import com.hexadeventure.model.inventory.characters.PlayableCharacter;
import com.hexadeventure.model.map.GameMap;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CombatTest {
    @Test
    public void givenWidthAndHeight_whenCreateCombatTerrain_thenTerrainIsCreated() {
        int row = 3;
        int column = 4;
        CombatTerrain combatTerrain = new CombatTerrain(row, column);
        
        assertThat(combatTerrain.getRowSize()).isEqualTo(row);
        assertThat(combatTerrain.getColumnSize()).isEqualTo(column);
        assertThat(combatTerrain.getPlayerTerrain().length).isEqualTo(row);
        assertThat(combatTerrain.getPlayerTerrain()[0].length).isEqualTo(column);
        assertThat(combatTerrain.getEnemyTerrain().length).isEqualTo(row);
        assertThat(combatTerrain.getEnemyTerrain()[0].length).isEqualTo(column);
    }
    
    @Test
    public void givenPosition_whenPlaceCharacter_thenCharacterIsAddedToTerrain() {
        int row = 3;
        int column = 4;
        CombatTerrain combatTerrain = new CombatTerrain(row, column);
        PlayableCharacter character = CharacterFactory.createCharacter();
        
        combatTerrain.placeCharacter(0, 0, character);
        
        assertThat(combatTerrain.getPlayerTerrain()[0][0]).isEqualTo(character);
    }
    
    @Test
    public void givenPosition_whenRemoveCharacter_thenCharacterIsRemovedFromTerrain() {
        int row = 3;
        int column = 4;
        CombatTerrain combatTerrain = new CombatTerrain(row, column);
        PlayableCharacter character = CharacterFactory.createCharacter();
        
        combatTerrain.placeCharacter(0, 0, character);
        combatTerrain.removeCharacter(0, 0);
        
        assertThat(combatTerrain.getPlayerTerrain()[0][0]).isNull();
    }
    
    @Test
    public void givenPosition_whenPlaceEnemy_thenEnemyIsAddedToTerrain() {
        int row = 3;
        int column = 4;
        CombatTerrain combatTerrain = new CombatTerrain(row, column);
        PlayableCharacter enemy = CharacterFactory.createCharacter();
        
        combatTerrain.placeEnemy(0, 0, enemy);
        
        assertThat(combatTerrain.getEnemyTerrain()[0][0]).isEqualTo(enemy);
    }
    
    @Test
    public void givenPosition_whenRemoveEnemy_thenEnemyIsRemovedFromTerrain() {
        int row = 3;
        int column = 4;
        CombatTerrain combatTerrain = new CombatTerrain(row, column);
        PlayableCharacter enemy = CharacterFactory.createCharacter();
        
        combatTerrain.placeEnemy(0, 0, enemy);
        combatTerrain.removeEnemy(0, 0);
        
        assertThat(combatTerrain.getEnemyTerrain()[0][0]).isNull();
    }
    
    @Test
    public void givenInitializedTerrain_whenResetTerrain_thenAllCellsAreEmpty() {
        int row = 3;
        int column = 4;
        CombatTerrain combatTerrain = new CombatTerrain(row, column);
        PlayableCharacter character = CharacterFactory.createCharacter();
        
        for (int i = 0; i < combatTerrain.getRowSize(); i++) {
            for (int j = 0; j < combatTerrain.getColumnSize(); j++) {
                combatTerrain.placeCharacter(i, j, character);
                combatTerrain.placeEnemy(i, j, character);
            }
        }
        
        combatTerrain.resetTerrain();
        
        for (int i = 0; i < combatTerrain.getRowSize(); i++) {
            for (int j = 0; j < combatTerrain.getColumnSize(); j++) {
                assertThat(combatTerrain.getPlayerTerrain()[i][j]).isNull();
                assertThat(combatTerrain.getEnemyTerrain()[i][j]).isNull();
            }
        }
    }
    
    @Test
    public void whenCreateMap_thenTerrainIsCreated() {
        int row = 3;
        int column = 4;
        GameMap gameMap = GameMapFactory.createGameMap();
        
        assertThat(gameMap.getCombatTerrain()).isNotNull();
        assertThat(gameMap.getCombatTerrain().getRowSize()).isEqualTo(row);
        assertThat(gameMap.getCombatTerrain().getColumnSize()).isEqualTo(column);
    }
}

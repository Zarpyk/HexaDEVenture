package com.hexadeventure.model.combat;

import com.hexadeventure.model.inventory.characters.Loot;
import com.hexadeventure.model.inventory.characters.PlayableCharacter;
import com.hexadeventure.model.map.GameMap;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CombatTerrain {
    private final String id;
    private final int rowSize;
    private final int columnSize;
    
    private final PlayableCharacter[][] playerTerrain;
    private final PlayableCharacter[][] enemyTerrain;
    
    private Loot[] loot;
    private int lootSeed;
    
    public CombatTerrain() {
        id = UUID.randomUUID().toString();
        this.rowSize = GameMap.COMBAT_TERRAIN_ROW_SIZE;
        this.columnSize = GameMap.COMBAT_TERRAIN_COLUMN_SIZE;
        this.playerTerrain = new PlayableCharacter[rowSize][columnSize];
        this.enemyTerrain = new PlayableCharacter[rowSize][columnSize];
    }
    
    public CombatTerrain(int rowSize, int columnSize) {
        id = UUID.randomUUID().toString();
        this.rowSize = rowSize;
        this.columnSize = columnSize;
        this.playerTerrain = new PlayableCharacter[rowSize][columnSize];
        this.enemyTerrain = new PlayableCharacter[rowSize][columnSize];
    }
    
    /**
     * Gets the character at the specified position on the terrain.
     *
     * @param row The row index of the player terrain
     * @param column The column index of the player terrain
     * @return The character at the specified position, or null if no character is present
     */
    public PlayableCharacter getCharacterAt(int row, int column) {
        return playerTerrain[row][column];
    }
    
    /**
     * Places a character on the terrain at the specified position.
     *
     * @param row The row index of the player terrain
     * @param column The column index of the player terrain
     * @param character The character to place on the terrain
     */
    public void placeCharacter(int row, int column, PlayableCharacter character) {
        playerTerrain[row][column] = character;
    }
    
    /**
     * Removes a character on the terrain at the specified position
     *
     * @param row The row index of the player terrain
     * @param column The column index of the player terrain
     */
    public void removeCharacter(int row, int column) {
        playerTerrain[row][column] = null;
    }
    
    /**
     * Places multiple enemies on the terrain.
     *
     * @param enemies 2D Array of enemies to place on the terrain, the first will be row, and the second will be column
     */
    public void placeEnemies(PlayableCharacter[][] enemies) {
        for (int i = 0; i < enemies.length; i++) {
            for (int j = 0; j < enemies[i].length; j++) {
                PlayableCharacter enemy = enemies[i][j];
                if(enemy != null) {
                    placeEnemy(i, j, enemy);
                }
            }
        }
    }
    
    /**
     * Places an enemy on the terrain at the specified position.
     *
     * @param row The row index of the enemy terrain
     * @param column The column index of the enemy terrain
     * @param enemy The enemy to place on the terrain
     */
    public void placeEnemy(int row, int column, PlayableCharacter enemy) {
        enemyTerrain[row][column] = enemy;
    }
    
    /**
     * Removes an enemy on the terrain at the specified position
     *
     * @param row The row index of the enemy terrain
     * @param column The column index of the enemy terrain
     */
    public void removeEnemy(int row, int column) {
        enemyTerrain[row][column] = null;
    }
    
    /**
     * Sets the loot and loot seed for the terrain.
     *
     * @param loot The loot when win the combat
     * @param lootSeed The seed to generate the loot
     */
    public void setLoot(Loot[] loot, int lootSeed) {
        this.loot = loot;
        this.lootSeed = lootSeed;
    }
    
    /**
     * Resets the terrain by removing all characters and enemies.
     */
    public void resetTerrain() {
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < columnSize; j++) {
                playerTerrain[i][j] = null;
                enemyTerrain[i][j] = null;
            }
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof CombatTerrain terrain)) return false;
        return Objects.equals(id, terrain.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

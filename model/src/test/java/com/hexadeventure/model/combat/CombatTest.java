package com.hexadeventure.model.combat;

import com.hexadeventure.common.CharacterFactory;
import com.hexadeventure.common.GameMapFactory;
import com.hexadeventure.model.inventory.ItemType;
import com.hexadeventure.model.inventory.characters.*;
import com.hexadeventure.model.map.GameMap;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CombatTest {
    //region CombatTerrain
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
    public void givenLootAndLootSeed_whenSetLoot_thenLootIsSet() {
        int row = 3;
        int column = 4;
        CombatTerrain combatTerrain = new CombatTerrain(row, column);
        Loot[] lootArray = new Loot[]{new Loot(ItemType.WEAPON, "", 1)};
        int lootSeed = 1234;
        
        combatTerrain.setLoot(lootArray, lootSeed);
        
        assertThat(combatTerrain.getLoot()).isEqualTo(lootArray);
        assertThat(combatTerrain.getLootSeed()).isEqualTo(lootSeed);
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
    //endregion
    
    //region GameMap
    @Test
    public void whenCreateMap_thenTerrainIsCreated() {
        int row = 3;
        int column = 4;
        GameMap gameMap = GameMapFactory.createGameMap();
        
        assertThat(gameMap.getCombatTerrain()).isNotNull();
        assertThat(gameMap.getCombatTerrain().getRowSize()).isEqualTo(row);
        assertThat(gameMap.getCombatTerrain().getColumnSize()).isEqualTo(column);
    }
    //endregion
    
    //region CharacterCombatInfo
    @Test
    public void givenDamage_whenApplyDamage_thenHealthIsReduced() {
        CharacterCombatInfo character = CharacterFactory.createCombatCharacter();
        double initialHealth = character.getHealth();
        int damage = 10;
        
        CharacterStatusChange characterStatusChange = character.damage(damage);
        
        assertThat(character.getHealth()).isEqualTo(initialHealth - damage);
        assertThat(characterStatusChange).isNotNull();
        assertThat(characterStatusChange.statChanged()).isEqualTo(CharacterStat.HEALTH);
        assertThat(characterStatusChange.oldValue()).isEqualTo(initialHealth);
        assertThat(characterStatusChange.newValue()).isEqualTo(initialHealth - damage);
    }
    
    @Test
    public void givenMoreDamageThanHealth_whenApplyDamage_thenHealthIsZero() {
        CharacterCombatInfo character = CharacterFactory.createCombatCharacter();
        double initialHealth = character.getHealth();
        double damage = initialHealth + 10;
        
        CharacterStatusChange characterStatusChange = character.damage(damage);
        
        assertThat(character.getHealth()).isEqualTo(0);
        assertThat(characterStatusChange).isNotNull();
        assertThat(characterStatusChange.statChanged()).isEqualTo(CharacterStat.HEALTH);
        assertThat(characterStatusChange.oldValue()).isEqualTo(initialHealth);
        assertThat(characterStatusChange.newValue()).isEqualTo(0);
    }
    
    @Test
    public void givenDeadCharacter_whenApplyDamage_thenHealthIsZero() {
        CharacterCombatInfo character = CharacterFactory.createCombatCharacter();
        double initialHealth = character.getHealth();
        
        character.damage(initialHealth);
        CharacterStatusChange characterStatusChange = character.damage(5);
        
        assertThat(character.getHealth()).isEqualTo(0);
        assertThat(characterStatusChange).isNull();
    }
    
    @Test
    public void givenHealing_whenApplyHealing_thenHealthIsIncreased() {
        CharacterCombatInfo character = CharacterFactory.createCombatCharacter();
        double initialHealth = character.getHealth();
        int healing = 5;
        
        character.damage(5);
        CharacterStatusChange characterStatusChange = character.heal(healing);
        
        assertThat(character.getHealth()).isEqualTo(initialHealth - 5 + healing);
        assertThat(characterStatusChange).isNotNull();
        assertThat(characterStatusChange.statChanged()).isEqualTo(CharacterStat.HEALTH);
        assertThat(characterStatusChange.oldValue()).isEqualTo(initialHealth - 5);
        assertThat(characterStatusChange.newValue()).isEqualTo(initialHealth - 5 + healing);
    }
    
    @Test
    public void givenMoreHealingThanMaxHealth_whenApplyHealing_thenHealthIsMax() {
        CharacterCombatInfo character = CharacterFactory.createCombatCharacter();
        double initialHealth = character.getHealth();
        double healing = initialHealth + 10;
        
        character.damage(initialHealth - 5);
        CharacterStatusChange characterStatusChange = character.heal(healing);
        
        assertThat(character.getHealth()).isEqualTo(initialHealth);
        assertThat(characterStatusChange).isNotNull();
        assertThat(characterStatusChange.statChanged()).isEqualTo(CharacterStat.HEALTH);
        assertThat(characterStatusChange.oldValue()).isEqualTo(5);
        assertThat(characterStatusChange.newValue()).isEqualTo(initialHealth);
    }
    
    @Test
    public void givenDeadCharacter_whenApplyHealing_thenHealthIsZero() {
        CharacterCombatInfo character = CharacterFactory.createCombatCharacter();
        double initialHealth = character.getHealth();
        
        character.damage(initialHealth);
        CharacterStatusChange characterStatusChange = character.heal(5);
        
        assertThat(character.getHealth()).isEqualTo(0);
        assertThat(characterStatusChange).isNull();
    }
    
    @Test
    public void givenCharacter_whenCreated_thenCooldownIsZero() {
        CharacterCombatInfo character = CharacterFactory.createCombatCharacter();
        
        assertThat(character.getCooldown()).isEqualTo(0);
    }
    
    @Test
    public void givenCharacter_whenResetCooldown_thenCooldownIsReset() {
        CharacterCombatInfo character = CharacterFactory.createCombatCharacter();
        
        CharacterStatusChange characterStatusChange = character.resetCooldown();
        
        assertThat(character.getCooldown()).isEqualTo(character.getCharacter().getWeapon().getCooldown());
        assertThat(characterStatusChange).isNotNull();
        assertThat(characterStatusChange.statChanged()).isEqualTo(CharacterStat.COOLDOWN);
        assertThat(characterStatusChange.oldValue()).isEqualTo(0);
        assertThat(characterStatusChange.newValue()).isEqualTo(character.getCharacter().getWeapon().getCooldown());
    }
    
    @Test
    public void givenDeadCharacter_whenResetCooldown_thenCooldownIsNotChanged() {
        CharacterCombatInfo character = CharacterFactory.createCombatCharacter();
        character.damage(character.getHealth());
        
        CharacterStatusChange characterStatusChange = character.resetCooldown();
        
        assertThat(character.getCooldown()).isEqualTo(0);
        assertThat(characterStatusChange).isNull();
    }
    
    @Test
    public void givenCharacter_whenReduceCooldown_thenCooldownIsReduced() {
        CharacterCombatInfo character = CharacterFactory.createCombatCharacter();
        character.resetCooldown();
        int resetedCooldown = character.getCooldown();
        
        CharacterStatusChange characterStatusChange = character.reduceCooldown();
        
        assertThat(character.getCooldown()).isEqualTo(resetedCooldown - 1);
        assertThat(characterStatusChange).isNotNull();
        assertThat(characterStatusChange.statChanged()).isEqualTo(CharacterStat.COOLDOWN);
        assertThat(characterStatusChange.oldValue()).isEqualTo(resetedCooldown);
        assertThat(characterStatusChange.newValue()).isEqualTo(resetedCooldown - 1);
    }
    
    @Test
    public void givenDeadCharacter_whenReduceCooldown_CooldownIsNotChanged() {
        CharacterCombatInfo character = CharacterFactory.createCombatCharacter();
        character.resetCooldown();
        int resetedCooldown = character.getCooldown();
        
        character.damage(character.getHealth());
        CharacterStatusChange characterStatusChange = character.reduceCooldown();
        
        assertThat(character.getCooldown()).isEqualTo(resetedCooldown);
        assertThat(characterStatusChange).isNull();
    }
    
    @Test
    public void givenCharacter_whenIncreaseAggro_thenAggroIsIncreased() {
        CharacterCombatInfo character = CharacterFactory.createCombatCharacter();
        double initialAggro = character.getCurrentAggro();
        double aggro = 99999;
        
        CharacterStatusChange characterStatusChange = character.increaseAggro(aggro);
        
        assertThat(character.getCurrentAggro()).isEqualTo(initialAggro + aggro);
        assertThat(characterStatusChange).isNotNull();
        assertThat(characterStatusChange.statChanged()).isEqualTo(CharacterStat.CURRENT_AGGRO);
        assertThat(characterStatusChange.oldValue()).isEqualTo(initialAggro);
        assertThat(characterStatusChange.newValue()).isEqualTo(initialAggro + aggro);
    }
    
    @Test
    public void givenDeadCharacter_whenIncreaseAggro_thenAggroIsNotChanged() {
        CharacterCombatInfo character = CharacterFactory.createCombatCharacter();
        double initialAggro = character.getCurrentAggro();
        
        character.damage(character.getHealth());
        CharacterStatusChange characterStatusChange = character.increaseAggro(99999);
        
        assertThat(character.getCurrentAggro()).isEqualTo(initialAggro);
        assertThat(characterStatusChange).isNull();
    }
    
    @Test
    public void givenCharacter_whenHypnotize_thenHypnotizationIsApplied() {
        CharacterCombatInfo character = CharacterFactory.createCombatCharacter();
        
        CharacterStatusChange characterStatusChange = character.hypnotize();
        
        assertThat(character.isHypnotized()).isTrue();
        assertThat(characterStatusChange).isNotNull();
        assertThat(characterStatusChange.statChanged()).isEqualTo(CharacterStat.HYPNOTIZED);
        assertThat(characterStatusChange.oldValue()).isEqualTo(0);
        assertThat(characterStatusChange.newValue()).isEqualTo(1);
    }
    //endregion
    
    //region CharacterCombatInfo Comparator
    @Test
    public void givenTwoCharacters_whenCompare_thenSortedBySpeed() {
        PlayableCharacter fastCharacter = CharacterFactory.createCharacter();
        fastCharacter.setSpeed(10);
        CharacterCombatInfo fastCharacterInfo = new CharacterCombatInfo(fastCharacter,
                                                                        0, 0, false);
        PlayableCharacter slowCharacter = CharacterFactory.createCharacter();
        slowCharacter.setSpeed(5);
        CharacterCombatInfo slowCharacterInfo = new CharacterCombatInfo(slowCharacter,
                                                                        0, 0, false);
        
        assertThat(fastCharacterInfo.compareBySpeed(slowCharacterInfo)).isLessThan(0);
        assertThat(slowCharacterInfo.compareBySpeed(fastCharacterInfo)).isGreaterThan(0);
    }
    
    @Test
    public void givenTwoCharactersOnSameSpeed_whenCompare_thenSortedByAlly() {
        PlayableCharacter fastCharacter = CharacterFactory.createCharacter();
        fastCharacter.setSpeed(10);
        CharacterCombatInfo fastCharacterInfo = new CharacterCombatInfo(fastCharacter,
                                                                        0, 0, false);
        PlayableCharacter slowCharacter = CharacterFactory.createCharacter();
        slowCharacter.setSpeed(10);
        CharacterCombatInfo slowCharacterInfo = new CharacterCombatInfo(slowCharacter,
                                                                        0, 0, true);
        
        assertThat(fastCharacterInfo.compareBySpeed(slowCharacterInfo)).isLessThan(0);
        assertThat(slowCharacterInfo.compareBySpeed(fastCharacterInfo)).isGreaterThan(0);
    }
    
    @Test
    public void givenTwoCharactersOnSameSpeedAndAlly_whenCompare_thenSortedByRow() {
        PlayableCharacter fastCharacter = CharacterFactory.createCharacter();
        fastCharacter.setSpeed(10);
        CharacterCombatInfo fastCharacterInfo = new CharacterCombatInfo(fastCharacter,
                                                                        0, 0, false);
        PlayableCharacter slowCharacter = CharacterFactory.createCharacter();
        slowCharacter.setSpeed(10);
        CharacterCombatInfo slowCharacterInfo = new CharacterCombatInfo(slowCharacter,
                                                                        1, 0, false);
        
        assertThat(fastCharacterInfo.compareBySpeed(slowCharacterInfo)).isLessThan(0);
        assertThat(slowCharacterInfo.compareBySpeed(fastCharacterInfo)).isGreaterThan(0);
    }
    
    @Test
    public void givenTwoCharactersOnSameSpeedAndAllyAndRow_whenCompare_thenSortedByColumn() {
        PlayableCharacter fastCharacter = CharacterFactory.createCharacter();
        fastCharacter.setSpeed(10);
        CharacterCombatInfo fastCharacterInfo = new CharacterCombatInfo(fastCharacter,
                                                                        0, 0, false);
        PlayableCharacter slowCharacter = CharacterFactory.createCharacter();
        slowCharacter.setSpeed(10);
        CharacterCombatInfo slowCharacterInfo = new CharacterCombatInfo(slowCharacter,
                                                                        0, 1, false);
        
        assertThat(fastCharacterInfo.compareBySpeed(slowCharacterInfo)).isLessThan(0);
        assertThat(slowCharacterInfo.compareBySpeed(fastCharacterInfo)).isGreaterThan(0);
    }
    
    @Test
    public void givenTwoCharacters_whenCompare_thenSortedByAggro() {
        CharacterCombatInfo character1 = new CharacterCombatInfo(CharacterFactory.createCharacter(),
                                                                 0, 0, false);
        CharacterCombatInfo character2 = new CharacterCombatInfo(CharacterFactory.createCharacter(),
                                                                 0, 0, false);
        
        character1.increaseAggro(10);
        character2.increaseAggro(5);
        
        assertThat(character1.compareByAggro(character2)).isLessThan(0);
        assertThat(character2.compareByAggro(character1)).isGreaterThan(0);
    }
    
    @Test
    public void givenTwoCharactersOnSameAggro_whenCompare_thenSortedByRow() {
        CharacterCombatInfo character1 = new CharacterCombatInfo(CharacterFactory.createCharacter(),
                                                                 0, 0, false);
        CharacterCombatInfo character2 = new CharacterCombatInfo(CharacterFactory.createCharacter(),
                                                                 1, 0, false);
        
        character1.increaseAggro(10);
        character2.increaseAggro(10);
        
        assertThat(character1.compareByAggro(character2)).isLessThan(0);
        assertThat(character2.compareByAggro(character1)).isGreaterThan(0);
    }
    
    @Test
    public void givenTwoCharactersOnSameAggroAndRow_whenCompare_thenSortedByColumn() {
        CharacterCombatInfo character1 = new CharacterCombatInfo(CharacterFactory.createCharacter(),
                                                                 0, 0, false);
        CharacterCombatInfo character2 = new CharacterCombatInfo(CharacterFactory.createCharacter(),
                                                                 0, 1, false);
        
        character1.increaseAggro(10);
        character2.increaseAggro(10);
        
        assertThat(character1.compareByAggro(character2)).isLessThan(0);
        assertThat(character2.compareByAggro(character1)).isGreaterThan(0);
    }
    //endregion
}

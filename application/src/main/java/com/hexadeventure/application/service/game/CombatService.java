package com.hexadeventure.application.service.game;

import com.hexadeventure.application.exceptions.*;
import com.hexadeventure.application.port.in.game.CombatUseCase;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.application.port.out.settings.SettingsImporter;
import com.hexadeventure.application.service.common.Utilities;
import com.hexadeventure.model.combat.CombatProcess;
import com.hexadeventure.model.combat.CombatTerrain;
import com.hexadeventure.model.combat.TurnInfo;
import com.hexadeventure.model.inventory.Inventory;
import com.hexadeventure.model.inventory.characters.CharacterCombatInfo;
import com.hexadeventure.model.inventory.characters.Loot;
import com.hexadeventure.model.inventory.characters.PlayableCharacter;
import com.hexadeventure.model.inventory.foods.Food;
import com.hexadeventure.model.inventory.materials.Material;
import com.hexadeventure.model.inventory.potions.Potion;
import com.hexadeventure.model.inventory.weapons.Weapon;
import com.hexadeventure.model.inventory.weapons.WeaponSetting;
import com.hexadeventure.model.map.GameMap;
import com.hexadeventure.model.map.resources.ResourceType;

import java.util.List;
import java.util.SplittableRandom;

public class CombatService implements CombatUseCase {
    private final UserRepository userRepository;
    private final GameMapRepository gameMapRepository;
    private final SettingsImporter settingsImporter;
    
    public CombatService(UserRepository userRepository, GameMapRepository gameMapRepository,
                         SettingsImporter settingsImporter) {
        this.userRepository = userRepository;
        this.gameMapRepository = gameMapRepository;
        this.settingsImporter = settingsImporter;
    }
    
    @Override
    public CombatTerrain getCombatStatus(String email) {
        GameMap gameMap = Utilities.getGameMap(email, userRepository, gameMapRepository);
        
        if(!gameMap.isInCombat()) throw new CombatNotStartedException();
        
        return gameMap.getCombatTerrain();
    }
    
    @Override
    public void placeCharacter(String email, int row, int column, String characterId) {
        GameMap gameMap = Utilities.getGameMap(email, userRepository, gameMapRepository);
        
        if(!gameMap.isInCombat()) throw new CombatNotStartedException();
        
        CombatTerrain combatTerrain = gameMap.getCombatTerrain();
        checkParams(row, column, combatTerrain);
        
        Inventory inventory = gameMap.getInventory();
        PlayableCharacter playableCharacter = inventory.getCharacters().get(characterId);
        if(playableCharacter == null) throw new CharacterNotFoundException();
        
        if(combatTerrain.getCharacterAt(row, column) != null) throw new PositionOccupiedException();
        combatTerrain.placeCharacter(row, column, playableCharacter);
        
        inventory.removeCharacter(playableCharacter);
        
        gameMapRepository.save(gameMap);
    }
    
    @Override
    public void removeCharacter(String email, int row, int column) {
        GameMap gameMap = Utilities.getGameMap(email, userRepository, gameMapRepository);
        
        if(!gameMap.isInCombat()) throw new CombatNotStartedException();
        
        CombatTerrain combatTerrain = gameMap.getCombatTerrain();
        checkParams(row, column, combatTerrain);
        
        PlayableCharacter playableCharacter = combatTerrain.getCharacterAt(row, column);
        if(playableCharacter == null) throw new PositionEmptyException();
        combatTerrain.removeCharacter(row, column);
        
        Inventory inventory = gameMap.getInventory();
        inventory.addCharacter(playableCharacter);
        
        gameMapRepository.save(gameMap);
    }
    
    @Override
    public CombatProcess startAutoCombat(String email) {
        GameMap gameMap = Utilities.getGameMap(email, userRepository, gameMapRepository);
        
        if(!gameMap.isInCombat()) throw new CombatNotStartedException();
        
        CombatTerrain combatTerrain = gameMap.getCombatTerrain();
        
        // Process the combat
        CombatProcessor combatProcessor = new CombatProcessor(combatTerrain);
        combatProcessor.processTurn();
        
        // Update the combat info
        boolean noCharacterRemain = true;
        for (CharacterCombatInfo character : combatProcessor.getCharacters()) {
            if(character.isDead()) {
                combatTerrain.removeCharacter(character.getRow(), character.getColumn());
            } else {
                character.getCharacter().getChangedStats().updateStats(character);
                noCharacterRemain = false;
            }
        }
        boolean noEnemyRemain = true;
        for (CharacterCombatInfo enemy : combatProcessor.getEnemies()) {
            if(enemy.isDead()) {
                if(!enemy.isHypnotized()) combatTerrain.removeEnemy(enemy.getRow(), enemy.getColumn());
                else enemy.getCharacter().getChangedStats().updateStats(enemy);
            } else {
                enemy.getCharacter().getChangedStats().updateStats(enemy);
                noEnemyRemain = false;
            }
        }
        
        // Check if combat finished
        if(noCharacterRemain || noEnemyRemain) {
            finishCombat(combatProcessor, gameMap);
        }
        
        gameMapRepository.save(gameMap);
        
        List<TurnInfo> turnInfos = combatProcessor.getTurnInfos();
        return new CombatProcess(turnInfos);
    }
    
    private static void checkParams(int row, int column, CombatTerrain combatTerrain) {
        if(row < 0 || row >= combatTerrain.getRowSize() ||
           column < 0 || column >= combatTerrain.getColumnSize()) {
            throw new InvalidPositionException();
        }
    }
    
    private void finishCombat(CombatProcessor combatProcessor, GameMap gameMap) {
        // Add characters back to inventory if they are not dead
        for (CharacterCombatInfo character : combatProcessor.getCharacters()) {
            if(character.isDead()) continue;
            gameMap.getInventory().addCharacter(character.getCharacter());
        }
        // Add enemies to inventory if they are hypnotized
        for (CharacterCombatInfo enemy : combatProcessor.getEnemies()) {
            if(!enemy.isHypnotized()) continue;
            PlayableCharacter character = enemy.getCharacter();
            // Regen enemy health
            character.getChangedStats().updateStats(character.getHealth(), false);
            gameMap.getInventory().addCharacter(character);
        }
        Loot[] lootArray = gameMap.getCombatTerrain().getLoot();
        if(lootArray != null) {
            SplittableRandom random = new SplittableRandom(gameMap.getCombatTerrain().getLootSeed());
            for (Loot loot : lootArray) {
                addLootToInventory(loot, gameMap, random);
            }
        }
        gameMap.setInCombat(false);
    }
    
    private void addLootToInventory(Loot loot, GameMap gameMap, SplittableRandom random) {
        switch (loot.getType()) {
            case WEAPON -> {
                WeaponSetting weapon = settingsImporter.importWeapons().get(loot.getId());
                for (int i = 0; i < loot.getCount(); i++) {
                    gameMap.getInventory().addItem(new Weapon(weapon, random));
                }
            }
            case FOOD -> {
                Food food = settingsImporter.importFoods().get(loot.getId());
                gameMap.getInventory().addItem(food, loot.getCount());
            }
            case POTION -> {
                Potion potion = settingsImporter.importPotions().get(loot.getId());
                gameMap.getInventory().addItem(potion, loot.getCount());
            }
            case MATERIAL -> {
                Material material = settingsImporter.importMaterials().get(ResourceType.valueOf(loot.getId()));
                gameMap.getInventory().addItem(material, loot.getCount());
            }
        }
    }
}

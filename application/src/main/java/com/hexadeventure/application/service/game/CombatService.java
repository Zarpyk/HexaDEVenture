package com.hexadeventure.application.service.game;

import com.hexadeventure.application.exceptions.*;
import com.hexadeventure.application.port.in.game.CombatUseCase;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.application.service.common.Utilities;
import com.hexadeventure.model.combat.CombatProcess;
import com.hexadeventure.model.combat.CombatTerrain;
import com.hexadeventure.model.combat.TurnInfo;
import com.hexadeventure.model.inventory.Inventory;
import com.hexadeventure.model.inventory.characters.PlayableCharacter;
import com.hexadeventure.model.map.GameMap;

import java.util.ArrayList;
import java.util.List;

public class CombatService implements CombatUseCase {
    private final UserRepository userRepository;
    private final GameMapRepository gameMapRepository;
    
    public CombatService(UserRepository userRepository, GameMapRepository gameMapRepository) {
        this.userRepository = userRepository;
        this.gameMapRepository = gameMapRepository;
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
        List<TurnInfo> turnInfos = new ArrayList<>();
        
        
        
        return null;
    }
    
    private static void checkParams(int row, int column, CombatTerrain combatTerrain) {
        if(row < 0 || row >= combatTerrain.getRowSize() ||
           column < 0 || column >= combatTerrain.getColumnSize()) {
            throw new InvalidPositionException();
        }
    }
}

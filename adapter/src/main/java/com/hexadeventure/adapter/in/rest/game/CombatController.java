package com.hexadeventure.adapter.in.rest.game;

import com.hexadeventure.adapter.in.rest.game.dto.out.combat.CombatProcessDTO;
import com.hexadeventure.adapter.in.rest.game.dto.out.combat.CombatInfoDTO;
import com.hexadeventure.adapter.in.rest.game.dto.in.PlaceCharacterDTO;
import com.hexadeventure.adapter.in.rest.game.dto.in.RemoveCharacterDTO;
import com.hexadeventure.application.port.in.game.CombatUseCase;
import com.hexadeventure.model.combat.CombatProcess;
import com.hexadeventure.model.combat.CombatTerrain;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
public class CombatController {
    private final CombatUseCase combatUseCase;
    
    public CombatController(CombatUseCase combatUseCase) {
        this.combatUseCase = combatUseCase;
    }
    
    @GetMapping("/game/combat")
    public ResponseEntity<CombatInfoDTO> move(Principal principal) {
        CombatTerrain response = combatUseCase.getCombatStatus(principal.getName());
        return ResponseEntity.ok(CombatInfoDTO.fromModel(response));
    }
    
    @PostMapping("/game/combat/character")
    public ResponseEntity<Void> placeCharacter(Principal principal,
                                               @RequestBody PlaceCharacterDTO placeCharacterDTO) {
        combatUseCase.placeCharacter(principal.getName(),
                                     placeCharacterDTO.row(),
                                     placeCharacterDTO.column(),
                                     placeCharacterDTO.characterId());
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/game/combat/character")
    public ResponseEntity<Void> removeCharacter(Principal principal,
                                                @RequestBody RemoveCharacterDTO removeCharacterDTO) {
        combatUseCase.removeCharacter(principal.getName(),
                                      removeCharacterDTO.row(),
                                      removeCharacterDTO.column());
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/game/combat/start")
    public ResponseEntity<CombatProcessDTO> startAutoCombat(Principal principal) {
        CombatProcess combatProcess = combatUseCase.startAutoCombat(principal.getName());
        return ResponseEntity.ok(CombatProcessDTO.fromModel(combatProcess));
    }
}

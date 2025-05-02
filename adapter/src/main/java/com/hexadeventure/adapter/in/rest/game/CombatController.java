package com.hexadeventure.adapter.in.rest.game;

import com.hexadeventure.adapter.in.rest.game.combat.CombatStatusDTO;
import com.hexadeventure.application.port.in.game.CombatUseCase;
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
    public ResponseEntity<CombatStatusDTO> move(Principal principal) {
        CombatTerrain response = combatUseCase.getCombatStatus(principal.getName());
        return ResponseEntity.ok(CombatStatusDTO.fromModel(response));
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
}

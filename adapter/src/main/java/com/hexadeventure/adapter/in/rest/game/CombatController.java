package com.hexadeventure.adapter.in.rest.game;

import com.hexadeventure.adapter.in.rest.game.dto.in.PlaceCharacterDTO;
import com.hexadeventure.adapter.in.rest.game.dto.out.combat.CombatInfoDTO;
import com.hexadeventure.adapter.in.rest.game.dto.out.combat.CombatProcessDTO;
import com.hexadeventure.application.port.in.game.CombatUseCase;
import com.hexadeventure.model.combat.CombatProcess;
import com.hexadeventure.model.combat.CombatTerrain;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Combat status retrieved successfully",
                         content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = CombatInfoDTO.class))),
            @ApiResponse(responseCode = "401", description = "User not logged in",
                         content = @Content),
            @ApiResponse(responseCode = "405", description = "Game not started or combat not started",
                         content = @Content),
    })
    public ResponseEntity<CombatInfoDTO> move(Principal principal) {
        CombatTerrain response = combatUseCase.getCombatStatus(principal.getName());
        return ResponseEntity.ok(CombatInfoDTO.fromModel(response));
    }
    
    @PostMapping("/game/combat/character")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Character placed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid character or position"),
            @ApiResponse(responseCode = "401", description = "User not logged in"),
            @ApiResponse(responseCode = "405", description = "Game not started or combat not started"),
    })
    public ResponseEntity<Void> placeCharacter(Principal principal,
                                               @RequestBody PlaceCharacterDTO placeCharacterDTO) {
        combatUseCase.placeCharacter(principal.getName(),
                                     placeCharacterDTO.row(),
                                     placeCharacterDTO.column(),
                                     placeCharacterDTO.characterId());
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/game/combat/character")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Character removed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid position"),
            @ApiResponse(responseCode = "401", description = "User not logged in"),
            @ApiResponse(responseCode = "405", description = "Game not started or combat not started"),
    })
    public ResponseEntity<Void> removeCharacter(Principal principal,
                                                @Schema(minimum = "0", maximum = "3")
                                                @RequestParam
                                                int row,
                                                @Schema(minimum = "0", maximum = "4")
                                                @RequestParam
                                                int column) {
        combatUseCase.removeCharacter(principal.getName(), row, column);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/game/combat/process")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Combat turn processed successfully",
                         content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = CombatProcessDTO.class))),
            @ApiResponse(responseCode = "401", description = "User not logged in",
                         content = @Content),
            @ApiResponse(responseCode = "405", description = "Game not started or combat not started",
                         content = @Content),
    })
    public ResponseEntity<CombatProcessDTO> processCombatTurn(Principal principal) {
        CombatProcess combatProcess = combatUseCase.processCombatTurn(principal.getName());
        return ResponseEntity.ok(CombatProcessDTO.fromModel(combatProcess));
    }
}

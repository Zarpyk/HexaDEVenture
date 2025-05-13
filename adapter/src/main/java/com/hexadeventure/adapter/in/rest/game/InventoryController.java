package com.hexadeventure.adapter.in.rest.game;

import com.hexadeventure.adapter.in.rest.game.dto.in.EquipWeaponDTO;
import com.hexadeventure.adapter.in.rest.game.dto.in.UnequipWeaponDTO;
import com.hexadeventure.adapter.in.rest.game.dto.in.UseItemDTO;
import com.hexadeventure.adapter.in.rest.game.dto.out.inventory.InventoryDTO;
import com.hexadeventure.adapter.in.rest.game.dto.out.inventory.RecipeCountDTO;
import com.hexadeventure.adapter.in.rest.game.dto.out.inventory.RecipesDTO;
import com.hexadeventure.application.port.in.game.InventoryUseCase;
import com.hexadeventure.model.inventory.Inventory;
import com.hexadeventure.model.inventory.recipes.Recipe;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
public class InventoryController {
    
    private final InventoryUseCase inventoryUseCase;
    
    public InventoryController(InventoryUseCase inventoryUseCase) {
        this.inventoryUseCase = inventoryUseCase;
    }
    
    @GetMapping("/game/craft/recipes/count")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipes retrieved successfully",
                         content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = RecipesDTO.class)))
    })
    public ResponseEntity<RecipeCountDTO> getRecipesCount(Principal principal) {
        int recipesCount = inventoryUseCase.getRecipesCount(principal.getName());
        return ResponseEntity.ok(new RecipeCountDTO(recipesCount));
    }
    
    @GetMapping("/game/craft/recipes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipes retrieved successfully",
                         content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = RecipesDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid page or size",
                         content = @Content),
            @ApiResponse(responseCode = "401", description = "User not logged in",
                         content = @Content),
            @ApiResponse(responseCode = "405", description = "Game not started",
                         content = @Content),
    })
    public ResponseEntity<RecipesDTO> getRecipes(Principal principal, @RequestParam int page, @RequestParam int size) {
        Recipe[] recipes = inventoryUseCase.getRecipes(principal.getName(), page, size);
        return ResponseEntity.ok(RecipesDTO.fromModel(recipes));
    }
    
    @PostMapping("/game/craft")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Crafting successful"),
            @ApiResponse(responseCode = "400", description = "Invalid recipe index or count"),
            @ApiResponse(responseCode = "401", description = "User not logged in"),
            @ApiResponse(responseCode = "405", description = "Game not started"),
    })
    public ResponseEntity<Void> craft(Principal principal, @RequestParam int recipeIndex, @RequestParam int count) {
        inventoryUseCase.craft(principal.getName(), recipeIndex, count);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/game/inventory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory retrieved successfully",
                         content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = InventoryDTO.class))),
            @ApiResponse(responseCode = "401", description = "User not logged in",
                         content = @Content),
            @ApiResponse(responseCode = "405", description = "Game not started",
                         content = @Content),
    })
    public ResponseEntity<InventoryDTO> getInventory(Principal principal) {
        Inventory inventory = inventoryUseCase.getInventory(principal.getName());
        return ResponseEntity.ok(InventoryDTO.fromModel(inventory));
    }
    
    @PostMapping("/game/inventory/equip")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Weapon equipped successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid character ID or weapon ID"),
            @ApiResponse(responseCode = "401", description = "User not logged in"),
            @ApiResponse(responseCode = "405", description = "Game not started"),
    })
    public ResponseEntity<Void> equipWeapon(Principal principal, @RequestBody EquipWeaponDTO equipWeaponDTO) {
        inventoryUseCase.equipWeapon(principal.getName(), equipWeaponDTO.characterId(), equipWeaponDTO.weaponId());
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/game/inventory/unequip")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Weapon unequipped successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid character ID or character doesn't have a weapon"),
            @ApiResponse(responseCode = "401", description = "User not logged in"),
            @ApiResponse(responseCode = "405", description = "Game not started"),
    })
    public ResponseEntity<Void> unequipWeapon(Principal principal, @RequestBody UnequipWeaponDTO unequipWeaponDTO) {
        inventoryUseCase.unequipWeapon(principal.getName(), unequipWeaponDTO.characterId());
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/game/inventory/use")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item used successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid character ID or item ID"),
            @ApiResponse(responseCode = "401", description = "User not logged in"),
            @ApiResponse(responseCode = "405", description = "Game not started"),
    })
    public ResponseEntity<Void> useItem(Principal principal, @RequestBody UseItemDTO useItemDTO) {
        inventoryUseCase.useItem(principal.getName(), useItemDTO.characterId(), useItemDTO.itemId());
        return ResponseEntity.ok().build();
    }
}

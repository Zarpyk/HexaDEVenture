package com.hexadeventure.adapter.in.rest.game;

import com.hexadeventure.adapter.in.rest.game.dto.in.CraftDTO;
import com.hexadeventure.adapter.in.rest.game.dto.in.EquipWeaponDTO;
import com.hexadeventure.adapter.in.rest.game.dto.in.UnequipWeaponDTO;
import com.hexadeventure.adapter.in.rest.game.dto.in.UseItemDTO;
import com.hexadeventure.adapter.in.rest.game.dto.out.combat.CharacterDataDTO;
import com.hexadeventure.adapter.in.rest.game.dto.out.combat.WeaponDataDTO;
import com.hexadeventure.adapter.in.rest.game.dto.out.inventory.*;
import com.hexadeventure.application.port.in.game.InventoryUseCase;
import com.hexadeventure.model.inventory.Inventory;
import com.hexadeventure.model.inventory.characters.PlayableCharacter;
import com.hexadeventure.model.inventory.foods.Food;
import com.hexadeventure.model.inventory.materials.Material;
import com.hexadeventure.model.inventory.potions.Potion;
import com.hexadeventure.model.inventory.recipes.Recipe;
import com.hexadeventure.model.inventory.weapons.Weapon;
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
    public ResponseEntity<RecipesDTO> getRecipes(Principal principal,
                                                 @RequestParam
                                                 @Schema(minimum = "1")
                                                 int page,
                                                 @RequestParam
                                                 @Schema(minimum = "1")
                                                 int size) {
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
    public ResponseEntity<Void> craft(Principal principal, @RequestBody CraftDTO craftDTO) {
        inventoryUseCase.craft(principal.getName(), craftDTO.recipeIndex(), craftDTO.count());
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
    
    @GetMapping("/game/inventory/character")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Weapon retrieved successfully",
                         content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = CharacterDataDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid character ID",
                         content = @Content),
            @ApiResponse(responseCode = "401", description = "User not logged in",
                         content = @Content),
            @ApiResponse(responseCode = "405", description = "Game not started",
                         content = @Content),
    })
    public ResponseEntity<CharacterDataDTO> getCharacter(Principal principal,
                                                         @RequestParam String characterId) {
        PlayableCharacter character = inventoryUseCase.getCharacter(principal.getName(), characterId);
        return ResponseEntity.ok(CharacterDataDTO.fromModel(character));
    }
    
    @GetMapping("/game/inventory/weapon")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Weapon retrieved successfully",
                         content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = WeaponDataDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid weapon ID",
                         content = @Content),
            @ApiResponse(responseCode = "401", description = "User not logged in",
                         content = @Content),
            @ApiResponse(responseCode = "405", description = "Game not started",
                         content = @Content),
    })
    public ResponseEntity<WeaponDataDTO> getWeapon(Principal principal,
                                                   @RequestParam String weaponId) {
        Weapon weapon = inventoryUseCase.getWeapon(principal.getName(), weaponId);
        return ResponseEntity.ok(WeaponDataDTO.fromModel(weapon));
    }
    
    @GetMapping("/game/inventory/potion")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Potion retrieved successfully",
                         content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = PotionDataDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid potion ID",
                         content = @Content),
            @ApiResponse(responseCode = "401", description = "User not logged in",
                         content = @Content),
            @ApiResponse(responseCode = "405", description = "Game not started",
                         content = @Content),
    })
    public ResponseEntity<PotionDataDTO> getPotion(Principal principal,
                                                   @RequestParam String potionId) {
        Potion potion = inventoryUseCase.getPotion(principal.getName(), potionId);
        return ResponseEntity.ok(PotionDataDTO.fromModel(potion));
    }
    
    @GetMapping("/game/inventory/food")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Food retrieved successfully",
                         content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = FoodDataDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid food ID",
                         content = @Content),
            @ApiResponse(responseCode = "401", description = "User not logged in",
                         content = @Content),
            @ApiResponse(responseCode = "405", description = "Game not started",
                         content = @Content),
    })
    public ResponseEntity<FoodDataDTO> getFood(Principal principal,
                                               @RequestParam String foodId) {
        Food food = inventoryUseCase.getFood(principal.getName(), foodId);
        return ResponseEntity.ok(FoodDataDTO.fromModel(food));
    }
    
    @GetMapping("/game/inventory/material")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Material retrieved successfully",
                         content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = MaterialDataDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid material ID",
                         content = @Content),
            @ApiResponse(responseCode = "401", description = "User not logged in",
                         content = @Content),
            @ApiResponse(responseCode = "405", description = "Game not started",
                         content = @Content),
    })
    public ResponseEntity<MaterialDataDTO> getMaterial(Principal principal,
                                                       @RequestParam String materialId) {
        Material material = inventoryUseCase.getMaterial(principal.getName(), materialId);
        return ResponseEntity.ok(MaterialDataDTO.fromModel(material));
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

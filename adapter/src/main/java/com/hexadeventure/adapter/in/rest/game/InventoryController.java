package com.hexadeventure.adapter.in.rest.game;

import com.hexadeventure.adapter.in.rest.game.dto.in.EquipWeaponDTO;
import com.hexadeventure.adapter.in.rest.game.dto.in.UnequipWeaponDTO;
import com.hexadeventure.adapter.in.rest.game.dto.in.UseItemDTO;
import com.hexadeventure.adapter.in.rest.game.dto.out.inventory.InventoryDTO;
import com.hexadeventure.adapter.in.rest.game.dto.out.inventory.RecipesDTO;
import com.hexadeventure.application.port.in.game.InventoryUseCase;
import com.hexadeventure.model.inventory.Inventory;
import com.hexadeventure.model.inventory.recipes.Recipe;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
public class InventoryController {
    
    private final InventoryUseCase inventoryUseCase;
    
    public InventoryController(InventoryUseCase inventoryUseCase) {
        this.inventoryUseCase = inventoryUseCase;
    }
    
    @GetMapping("/game/craft/recipes")
    public ResponseEntity<RecipesDTO> getRecipes(Principal principal, @RequestParam int page, @RequestParam int size) {
        Recipe[] recipes = inventoryUseCase.getRecipes(principal.getName(), page, size);
        return ResponseEntity.ok(RecipesDTO.fromModel(recipes));
    }
    
    @PostMapping("/game/craft")
    public ResponseEntity<Void> craft(Principal principal, @RequestParam int recipeIndex, @RequestParam int count) {
        inventoryUseCase.craft(principal.getName(), recipeIndex, count);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/game/inventory")
    public ResponseEntity<InventoryDTO> getInventory(Principal principal) {
        Inventory inventory = inventoryUseCase.getInventory(principal.getName());
        return ResponseEntity.ok(InventoryDTO.fromModel(inventory));
    }
    
    @PostMapping("/game/inventory/equip")
    public ResponseEntity<Void> equipWeapon(Principal principal, @RequestBody EquipWeaponDTO equipWeaponDTO) {
        inventoryUseCase.equipWeapon(principal.getName(), equipWeaponDTO.characterId(), equipWeaponDTO.weaponId());
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/game/inventory/unequip")
    public ResponseEntity<Void> unequipWeapon(Principal principal, @RequestBody UnequipWeaponDTO unequipWeaponDTO) {
        inventoryUseCase.unequipWeapon(principal.getName(), unequipWeaponDTO.characterId());
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/game/inventory/use")
    public ResponseEntity<Void> useItem(Principal principal, @RequestBody UseItemDTO useItemDTO) {
        inventoryUseCase.useItem(principal.getName(), useItemDTO.characterId(), useItemDTO.itemId());
        return ResponseEntity.ok().build();
    }
}

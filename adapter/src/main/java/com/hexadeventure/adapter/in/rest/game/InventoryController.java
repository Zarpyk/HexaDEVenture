package com.hexadeventure.adapter.in.rest.game;

import com.hexadeventure.adapter.in.rest.game.dto.out.inventory.RecipesDTO;
import com.hexadeventure.application.port.in.game.InventoryUseCase;
import com.hexadeventure.model.inventory.recipes.Recipe;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class InventoryController {
    
    private final InventoryUseCase inventoryUseCase;
    
    public InventoryController(InventoryUseCase inventoryUseCase) {
        this.inventoryUseCase = inventoryUseCase;
    }
    
    @GetMapping("/recipes")
    public ResponseEntity<RecipesDTO> getRecipes(Principal principal, @RequestParam int page, @RequestParam int size) {
        Recipe[] recipes = inventoryUseCase.getRecipes(principal.getName(), page, size);
        return ResponseEntity.ok(RecipesDTO.fromModel(recipes));
    }
}

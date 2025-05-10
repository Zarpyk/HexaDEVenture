package com.hexadeventure.adapter.in.rest.game;

import com.hexadeventure.adapter.in.rest.common.RestCommon;
import com.hexadeventure.adapter.in.rest.common.UserFactory;
import com.hexadeventure.adapter.in.rest.game.dto.in.EquipWeaponDTO;
import com.hexadeventure.adapter.in.rest.game.dto.in.UnequipWeaponDTO;
import com.hexadeventure.adapter.in.rest.game.dto.in.UseItemDTO;
import com.hexadeventure.adapter.in.rest.game.dto.out.inventory.InventoryDTO;
import com.hexadeventure.adapter.in.rest.game.dto.out.inventory.RecipesDTO;
import com.hexadeventure.application.exceptions.*;
import com.hexadeventure.application.port.in.game.InventoryUseCase;
import com.hexadeventure.model.inventory.Inventory;
import com.hexadeventure.model.inventory.recipes.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;

import static org.mockito.Mockito.*;

public class InventoryTest {
    private static final int TEST_PAGE = 0;
    private static final int TEST_SIZE = 10;
    private static final int TEST_RECIPE_INDEX = 0;
    private static final int TEST_CRAFT_COUNT = 1;
    
    private static final String TEST_INVENTORY_ID = "testInventoryId";
    public static final String TEST_CHARACTER_ID = "characterId";
    public static final String TEST_WEAPON_ID = "weaponId";
    
    private final InventoryUseCase inventoryUseCase = mock(InventoryUseCase.class);
    
    @BeforeEach
    public void beforeEach() {
        RestCommon.Setup(new InventoryController(inventoryUseCase));
    }
    
    @Test
    public void givenPageAndSize_whenGetRecipes_thenReturnOkWithDTO() {
        when(inventoryUseCase.getRecipes(UserFactory.EMAIL, TEST_PAGE, TEST_SIZE)).thenReturn(new Recipe[10]);
        RestCommon.getWithParam("game/craft/recipes",
                                "page", Integer.toString(TEST_PAGE),
                                "size", Integer.toString(TEST_SIZE))
                  .then()
                  .statusCode(HttpStatus.OK.value())
                  .extract().body().as(RecipesDTO.class);
    }
    
    @Test
    public void givenInvalidPageAndSize_whenGetRecipes_thenReturnBadRequest() {
        doThrow(InvalidSearchException.class).when(inventoryUseCase).getRecipes(UserFactory.EMAIL,
                                                                                TEST_PAGE,
                                                                                TEST_SIZE);
        RestCommon.getWithParam("game/craft/recipes",
                                "page", Integer.toString(TEST_PAGE),
                                "size", Integer.toString(TEST_SIZE))
                  .then()
                  .statusCode(HttpStatus.BAD_REQUEST.value());
    }
    
    @Test
    public void givenEnoughMaterials_whenCraft_thenReturnOk() {
        RestCommon.postWithParam("game/craft",
                                 "recipeIndex", Integer.toString(TEST_RECIPE_INDEX),
                                 "count", Integer.toString(TEST_CRAFT_COUNT))
                  .then()
                  .statusCode(HttpStatus.OK.value());
    }
    
    @Test
    public void givenEmptyInventory_whenCraft_thenReturnMethodNotAllowed() {
        doThrow(NotEnoughResourcesException.class).when(inventoryUseCase).craft(UserFactory.EMAIL,
                                                                                TEST_RECIPE_INDEX,
                                                                                TEST_CRAFT_COUNT);
        RestCommon.postWithParam("game/craft",
                                 "recipeIndex", Integer.toString(TEST_RECIPE_INDEX),
                                 "count", Integer.toString(TEST_CRAFT_COUNT))
                  .then()
                  .statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
    }
    
    @Test
    public void givenInvalidRecipe_whenCraft_thenReturnBadRequest() {
        doThrow(InvalidRecipeException.class).when(inventoryUseCase).craft(UserFactory.EMAIL,
                                                                           -1,
                                                                           TEST_CRAFT_COUNT);
        RestCommon.postWithParam("game/craft",
                                 "recipeIndex", Integer.toString(-1),
                                 "count", Integer.toString(TEST_CRAFT_COUNT))
                  .then()
                  .statusCode(HttpStatus.BAD_REQUEST.value());
    }
    
    @Test
    public void givenUser_whenGetInventory_thenReturnOkWithDTO() {
        when(inventoryUseCase.getInventory(UserFactory.EMAIL)).thenReturn(new Inventory(TEST_INVENTORY_ID,
                                                                                        new HashMap<>(),
                                                                                        new HashMap<>()));
        RestCommon.get("game/inventory")
                  .then()
                  .statusCode(HttpStatus.OK.value())
                  .extract().body().as(InventoryDTO.class);
    }
    
    @Test
    public void givenDTO_whenEquipWeapon_thenReturnOk() {
        EquipWeaponDTO equipWeaponDTO = new EquipWeaponDTO(TEST_CHARACTER_ID, TEST_WEAPON_ID);
        RestCommon.postWithBody("game/inventory/equip", equipWeaponDTO)
                  .then()
                  .statusCode(HttpStatus.OK.value());
    }
    
    @Test
    public void givenInvalidCharacter_whenEquipWeapon_thenReturnBadRequest() {
        doThrow(InvalidCharacterException.class).when(inventoryUseCase).equipWeapon(UserFactory.EMAIL,
                                                                                    "",
                                                                                    TEST_WEAPON_ID);
        EquipWeaponDTO equipWeaponDTO = new EquipWeaponDTO("", TEST_WEAPON_ID);
        RestCommon.postWithBody("game/inventory/equip", equipWeaponDTO)
                  .then()
                  .statusCode(HttpStatus.BAD_REQUEST.value());
    }
    
    @Test
    public void givenInvalidWeapon_whenEquipWeapon_thenReturnBadRequest() {
        doThrow(InvalidItemException.class).when(inventoryUseCase).equipWeapon(UserFactory.EMAIL,
                                                                               TEST_CHARACTER_ID,
                                                                               "");
        EquipWeaponDTO equipWeaponDTO = new EquipWeaponDTO(TEST_CHARACTER_ID, "");
        RestCommon.postWithBody("game/inventory/equip", equipWeaponDTO)
                  .then()
                  .statusCode(HttpStatus.BAD_REQUEST.value());
    }
    
    @Test
    public void givenDTO_whenUnequipWeapon_thenReturnOk() {
        UnequipWeaponDTO unequipWeaponDTO = new UnequipWeaponDTO(TEST_CHARACTER_ID);
        RestCommon.postWithBody("game/inventory/unequip", unequipWeaponDTO)
                  .then()
                  .statusCode(HttpStatus.OK.value());
    }
    
    @Test
    public void givenInvalidCharacter_whenUnequipWeapon_thenReturnBadRequest() {
        doThrow(InvalidCharacterException.class).when(inventoryUseCase).unequipWeapon(UserFactory.EMAIL,
                                                                                      "");
        UnequipWeaponDTO unequipWeaponDTO = new UnequipWeaponDTO("");
        RestCommon.postWithBody("game/inventory/unequip", unequipWeaponDTO)
                  .then()
                  .statusCode(HttpStatus.BAD_REQUEST.value());
    }
    
    @Test
    public void givenDTO_whenUseItem_thenReturnOk() {
        UseItemDTO useItemDTO = new UseItemDTO(TEST_CHARACTER_ID, TEST_WEAPON_ID);
        RestCommon.postWithBody("game/inventory/use", useItemDTO)
                  .then()
                  .statusCode(HttpStatus.OK.value());
    }
    
    @Test
    public void givenInvalidCharacter_whenUseItem_thenReturnBadRequest() {
        doThrow(InvalidCharacterException.class).when(inventoryUseCase).useItem(UserFactory.EMAIL,
                                                                                "",
                                                                                TEST_WEAPON_ID);
        UseItemDTO useItemDTO = new UseItemDTO("", TEST_WEAPON_ID);
        RestCommon.postWithBody("game/inventory/use", useItemDTO)
                  .then()
                  .statusCode(HttpStatus.BAD_REQUEST.value());
    }
    
    @Test
    public void givenInvalidItem_whenUseItem_thenReturnBadRequest() {
        doThrow(InvalidItemException.class).when(inventoryUseCase).useItem(UserFactory.EMAIL,
                                                                           TEST_CHARACTER_ID,
                                                                           "");
        UseItemDTO useItemDTO = new UseItemDTO(TEST_CHARACTER_ID, "");
        RestCommon.postWithBody("game/inventory/use", useItemDTO)
                  .then()
                  .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}

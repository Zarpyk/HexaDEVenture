package com.hexadeventure.bootstrap.e2e.game;

import com.hexadeventure.adapter.in.rest.game.dto.in.*;
import com.hexadeventure.adapter.in.rest.game.dto.out.combat.CharacterDataDTO;
import com.hexadeventure.adapter.in.rest.game.dto.out.combat.WeaponDataDTO;
import com.hexadeventure.adapter.in.rest.game.dto.out.inventory.*;
import com.hexadeventure.adapter.in.rest.game.dto.out.map.ChunkDataDTO;
import com.hexadeventure.adapter.in.rest.game.dto.out.map.Vector2DTO;
import com.hexadeventure.adapter.in.rest.game.dto.out.movement.MovementResponseDTO;
import com.hexadeventure.application.service.game.GameService;
import com.hexadeventure.bootstrap.e2e.common.RestCommon;
import com.hexadeventure.bootstrap.e2e.common.UserFactory;
import io.restassured.response.Response;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

// From: https://stackoverflow.com/a/54948672/11451105
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class AbstractGameE2ETest {
    private static final long TEST_SEED = 1234;
    private static final int TEST_SIZE = GameService.MIN_MAP_SIZE;
    
    @LocalServerPort
    private int port;
    
    @Test
    @Order(0)
    public void givenUserData_whenRegister_thenReturnCreated() {
        Response response = UserFactory.createTestUser(port);
        response.then().statusCode(HttpStatus.OK.value());
    }
    
    @Test
    @Order(1)
    public void givenUser_whenDontHaveStartedGame_thenReturnCreated() {
        StartGameDTO startGameDTO = new StartGameDTO(TEST_SEED, TEST_SIZE);
        
        Response response = RestCommon.postWithBody(port, "/start", startGameDTO);
        response.then().statusCode(HttpStatus.CREATED.value());
    }
    
    @Test
    @Order(2)
    public void givenUser_whenGetChunk_thenReturnOkWithDTO() {
        Response response = RestCommon.get(port, "/game/chunks");
        response.then().statusCode(HttpStatus.OK.value())
                .extract().body().as(ChunkDataDTO.class);
    }
    
    @Test
    @Order(3)
    public void givenVector2_whenMove_thenReturnOkWithDTO() {
        Vector2DTO position = new Vector2DTO(TEST_SIZE / 2 + 1, TEST_SIZE / 2);
        
        Response response = RestCommon.postWithBody(port, "/game/move", position);
        response.then().statusCode(HttpStatus.OK.value())
                .extract().body().as(MovementResponseDTO.class);
    }
    
    @Test
    @Order(4)
    public void givenUser_whenGetRecipeCount_thenReturnOkWithDTO() {
        Response response = RestCommon.get(port, "/game/craft/recipes/count");
        response.then().statusCode(HttpStatus.OK.value())
                .extract().body().as(RecipeCountDTO.class);
    }
    
    @Test
    @Order(5)
    public void givenUser_whenGetRecipes_thenReturnOkWithDTO() {
        Response response = RestCommon.get(port, "/game/craft/recipes?page=1&size=10");
        response.then().statusCode(HttpStatus.OK.value())
                .extract().body().as(RecipesDTO.class);
    }
    
    @Test
    @Order(6)
    public void givenNonExistentRecipe_whenCraft_thenReturnBadRequest() {
        CraftDTO craftDTO = new CraftDTO(999, 1);
        Response response = RestCommon.postWithBody(port, "/game/craft", craftDTO);
        response.then().statusCode(HttpStatus.BAD_REQUEST.value());
    }
    
    @Test
    @Order(7)
    public void givenUser_whenGetInventory_thenReturnOkWithDTO() {
        Response response = RestCommon.get(port, "/game/inventory");
        InventoryDTO inventoryDTO = response.then().statusCode(HttpStatus.OK.value())
                                            .extract().body().as(InventoryDTO.class);
        assertThat(inventoryDTO).isNotNull();
        String firstWeaponId = null;
        for (ItemDTO item : inventoryDTO.items()) {
            switch (item.itemType()) {
                case WEAPON -> {
                    response = RestCommon.get(port, "/game/inventory/weapon?weaponId=" + item.id());
                    response.then().statusCode(HttpStatus.OK.value())
                            .extract().body().as(WeaponDataDTO.class);
                    if(firstWeaponId == null) firstWeaponId = item.id();
                }
                case FOOD -> {
                    response = RestCommon.get(port, "/game/inventory/food?foodId=" + item.id());
                    response.then().statusCode(HttpStatus.OK.value())
                            .extract().body().as(FoodDataDTO.class);
                }
                case POTION -> {
                    response = RestCommon.get(port, "/game/inventory/potion?potionId=" + item.id());
                    response.then().statusCode(HttpStatus.OK.value())
                            .extract().body().as(PotionDataDTO.class);
                }
                case MATERIAL -> {
                    response = RestCommon.get(port, "/game/inventory/material?materialId=" + item.id());
                    response.then().statusCode(HttpStatus.OK.value())
                            .extract().body().as(MaterialDataDTO.class);
                }
            }
        }
        
        String firstCharacterId = null;
        for (CharacterDTO character : inventoryDTO.characters()) {
            response = RestCommon.get(port, "/game/inventory/character?characterId=" + character.id());
            response.then().statusCode(HttpStatus.OK.value())
                    .extract().body().as(CharacterDataDTO.class);
            if(firstCharacterId == null) firstCharacterId = character.id();
        }
        
        EquipWeaponDTO equipWeaponDTO = new EquipWeaponDTO(firstCharacterId, firstWeaponId);
        response = RestCommon.postWithBody(port, "/game/inventory/equip", equipWeaponDTO);
        response.then().statusCode(HttpStatus.OK.value());
        
        UnequipWeaponDTO unequipWeaponDTO = new UnequipWeaponDTO(firstCharacterId);
        response = RestCommon.postWithBody(port, "/game/inventory/unequip", unequipWeaponDTO);
        
        UseItemDTO useItemDTO = new UseItemDTO(firstCharacterId, firstWeaponId);
        response = RestCommon.postWithBody(port, "/game/inventory/use", useItemDTO);
        response.then().statusCode(HttpStatus.OK.value());
    }
    
    @Test
    @Order(8)
    public void givenNoStartedCombat_whenGetCombat_thenReturnBadRequest() {
        Response response = RestCommon.get(port, "/game/combat");
        response.then().statusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
    }
    
    @Test
    @Order(9)
    public void givenUser_whenFinish_thenReturnOk() {
        Response response = RestCommon.delete(port, "/finish");
        response.then().statusCode(HttpStatus.OK.value());
    }
    
    @Test
    @Order(10)
    public void givenUser_whenUnregister_thenReturnOk() {
        Response response = UserFactory.deleteTestUser(port);
        response.then().statusCode(HttpStatus.OK.value());
    }
}
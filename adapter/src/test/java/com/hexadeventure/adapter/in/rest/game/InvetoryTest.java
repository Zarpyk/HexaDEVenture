package com.hexadeventure.adapter.in.rest.game;

import com.hexadeventure.adapter.in.rest.common.RestCommon;
import com.hexadeventure.adapter.in.rest.common.UserFactory;
import com.hexadeventure.adapter.in.rest.game.dto.out.inventory.RecipesDTO;
import com.hexadeventure.application.exceptions.InvalidRecipeException;
import com.hexadeventure.application.exceptions.InvalidSearchException;
import com.hexadeventure.application.exceptions.NotEnoughtResourcesException;
import com.hexadeventure.application.port.in.game.InventoryUseCase;
import com.hexadeventure.model.inventory.recipes.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.mockito.Mockito.*;

public class InvetoryTest {
    private static final int TEST_PAGE = 0;
    private static final int TEST_SIZE = 10;
    private static final int TEST_RECIPE_INDEX = 0;
    private static final int TEST_CRAFT_COUNT = 1;
    
    private final InventoryUseCase inventoryUseCase = mock(InventoryUseCase.class);
    
    @BeforeEach
    public void beforeEach() {
        RestCommon.Setup(new InventoryController(inventoryUseCase));
    }
    
    @Test
    public void givenPageAndSize_whenGetRecipes_thenReturnOkWithDTO() {
        when(inventoryUseCase.getRecipes(UserFactory.EMAIL, TEST_PAGE, TEST_SIZE)).thenReturn(new Recipe[10]);
        RestCommon.getWithParam("/recipes",
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
        RestCommon.getWithParam("/recipes",
                                "page", Integer.toString(TEST_PAGE),
                                "size", Integer.toString(TEST_SIZE))
                  .then()
                  .statusCode(HttpStatus.BAD_REQUEST.value());
    }
    
    @Test
    public void givenEnoughtMaterials_whenCraft_thenReturnOk() {
        RestCommon.postWithParam("/craft",
                                 "recipeIndex", Integer.toString(TEST_RECIPE_INDEX),
                                 "count", Integer.toString(TEST_CRAFT_COUNT))
                  .then()
                  .statusCode(HttpStatus.OK.value());
    }
    
    @Test
    public void givenEmptyInventory_whenCraft_thenReturnMethodNotAllowed() {
        doThrow(NotEnoughtResourcesException.class).when(inventoryUseCase).craft(UserFactory.EMAIL,
                                                                                 TEST_RECIPE_INDEX,
                                                                                 TEST_CRAFT_COUNT);
        RestCommon.postWithParam("/craft",
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
        RestCommon.postWithParam("/craft",
                                 "recipeIndex", Integer.toString(-1),
                                 "count", Integer.toString(TEST_CRAFT_COUNT))
                  .then()
                  .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}

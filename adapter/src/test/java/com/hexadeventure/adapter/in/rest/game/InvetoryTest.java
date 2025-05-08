package com.hexadeventure.adapter.in.rest.game;

import com.hexadeventure.adapter.in.rest.common.RestCommon;
import com.hexadeventure.adapter.in.rest.common.UserFactory;
import com.hexadeventure.adapter.in.rest.game.dto.out.inventory.RecipesDTO;
import com.hexadeventure.application.port.in.game.InventoryUseCase;
import com.hexadeventure.model.inventory.recipes.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InvetoryTest {
    private final InventoryUseCase inventoryUseCase = mock(InventoryUseCase.class);
    
    @BeforeEach
    public void beforeEach() {
        RestCommon.Setup(new InventoryController(inventoryUseCase));
    }
    
    @Test
    public void givenPageAndSize_whenGetRecipes_thenReturnOkWithDTO() {
        when(inventoryUseCase.getRecipes(UserFactory.EMAIL, 0, 10)).thenReturn(new Recipe[10]);
        RestCommon.getWithParam("/recipes", "page", "0", "size", "10")
                  .then()
                  .statusCode(HttpStatus.OK.value())
                  .extract().body().as(RecipesDTO.class);
    }
}

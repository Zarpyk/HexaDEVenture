package com.hexadeventure.adapter.in.rest.users;

import com.hexadeventure.adapter.in.rest.common.RestCommon;
import com.hexadeventure.adapter.in.rest.common.UserFactory;
import com.hexadeventure.adapter.in.rest.users.dto.out.UserInfoDTO;
import com.hexadeventure.application.exceptions.InvalidIdException;
import com.hexadeventure.application.port.in.users.UserUseCase;
import com.hexadeventure.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private final UserUseCase userUseCase = mock(UserUseCase.class);
    
    @BeforeEach
    public void beforeEach() {
        RestCommon.Setup(new UserController(userUseCase));
    }
    
    @Test
    public void givenAUserId_whenGettingUser_thenReturnsOkWithDTO() {
        User model = UserFactory.USER_DTO.toModel();
        when(userUseCase.getUser(model.getId())).thenReturn(model);
        RestCommon.get("/userInfo?userId=" + model.getId(), false)
                  .then()
                  .statusCode(HttpStatus.OK.value())
                  .extract().body().as(UserInfoDTO.class);
    }
    
    @Test
    public void givenAUserId_whenGettingUser_thenReturnsBadRequest() {
        when(userUseCase.getUser(anyString())).thenThrow(new InvalidIdException());
        RestCommon.get("/userInfo?userId=invalid", false)
                  .then()
                  .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}

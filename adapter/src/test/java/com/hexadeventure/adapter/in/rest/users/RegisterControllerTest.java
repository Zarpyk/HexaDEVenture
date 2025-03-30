package com.hexadeventure.adapter.in.rest.users;

import com.hexadeventure.adapter.in.rest.common.RestCommon;
import com.hexadeventure.adapter.in.rest.common.UserFactory;
import com.hexadeventure.adapter.in.rest.login.RegisterController;
import com.hexadeventure.adapter.in.rest.login.UserDTO;
import com.hexadeventure.application.exceptions.UserExistException;
import com.hexadeventure.application.port.in.login.RegisterUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class RegisterControllerTest {
    private static final UserDTO ANOTHER_USER_DTO = new UserDTO("a@a.com", "another_user", "another_password");
    
    private final RegisterUseCase registerUseCase = mock(RegisterUseCase.class);
    
    @BeforeEach
    public void beforeEach() {
        RestCommon.Setup(new RegisterController(registerUseCase));
    }
    
    @org.junit.jupiter.api.Test
    public void givenAnEmailAndUsernameAndPassword_whenRegistering_thenRegistersTheUser() {
        RestCommon.postWithBody("/register", UserFactory.USER_DTO, false)
                  .then().statusCode(HttpStatus.OK.value());
    }
    
    @Test
    public void givenAnExistingEmail_whenRegistering_thenReturnsError() {
        // Simulate already the user exists
        doThrow(new UserExistException(UserFactory.EMAIL)).when(registerUseCase).register(any());
        
        RestCommon.postWithBody("/register", UserFactory.USER_DTO, false)
                  .then().statusCode(HttpStatus.CONFLICT.value());
    }
    
    @Test
    public void givenAnAuthRequest_whenAlreadyLogin_thenReturnsError() {
        RestCommon.postWithBody("/register", ANOTHER_USER_DTO)
                  .then().statusCode(HttpStatus.UNAUTHORIZED.value());
    }
}

package com.hexadeventure.adapter.in.rest.users;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import com.hexadeventure.adapter.in.rest.common.RestCommon;
import com.hexadeventure.adapter.in.rest.common.UserFactory;
import com.hexadeventure.adapter.in.rest.login.RegisterController;
import com.hexadeventure.adapter.in.rest.login.UserDTO;
import com.hexadeventure.application.exceptions.UserExistException;
import com.hexadeventure.application.port.in.login.RegisterUseCase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@RunWith(SpringRunner.class)
public class RegisterControllerTest {
    private static final UserDTO ANOTHER_USER_DTO = new UserDTO("a@a.com", "another_user", "another_password");
    
    @Mock
    private RegisterUseCase registerUseCase;
    
    @Before
    public void initialiseRestAssuredMockMvcStandalone() {
        RestCommon.Setup(new RegisterController(registerUseCase));
    }
    
    @Test
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

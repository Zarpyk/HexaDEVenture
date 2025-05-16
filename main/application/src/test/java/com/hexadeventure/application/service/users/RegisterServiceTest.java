package com.hexadeventure.application.service.users;

import com.hexadeventure.application.exceptions.InvalidEmailException;
import com.hexadeventure.application.exceptions.InvalidPasswordException;
import com.hexadeventure.application.exceptions.InvalidUsernameException;
import com.hexadeventure.application.exceptions.UserExistException;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.application.service.common.UserFactory;
import com.hexadeventure.model.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

public class RegisterServiceTest {
    private final UserRepository userRepository = mock(UserRepository.class);
    private final GameMapRepository gameMapRepository = mock(GameMapRepository.class);
    
    private final RegisterService registerService = new RegisterService(userRepository, gameMapRepository);
    
    @Test
    public void givenNewUser_whenRegister_thenSaveUser() {
        registerService.register(UserFactory.USER, UserFactory.PASSWORD);
        
        verify(userRepository, times(1)).save(UserFactory.USER);
    }
    
    @Test
    public void givenExistingUser_whenRegister_thenThrowException() {
        UserFactory.createTestUser(userRepository);
        
        assertThatExceptionOfType(UserExistException.class)
                .isThrownBy(() -> registerService.register(UserFactory.USER, UserFactory.PASSWORD));
        
        verify(userRepository, never()).save(any());
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"", "invalid_email", "invalid@domain", "invalid@domain.", "invalid@.com",
                            "invalid@domain..com", "@invalid.com"})
    public void givenInvalidEmail_whenRegister_thenThrowException(String email) {
        User user = new User(email, UserFactory.USERNAME, UserFactory.PASSWORD);
        
        assertThatExceptionOfType(InvalidEmailException.class)
                .isThrownBy(() -> registerService.register(user, UserFactory.PASSWORD));
    }
    
    @Test
    public void givenInvalidUsername_whenRegister_thenThrowException() {
        User user = new User(UserFactory.EMAIL, "", UserFactory.PASSWORD);
        
        assertThatExceptionOfType(InvalidUsernameException.class)
                .isThrownBy(() -> registerService.register(user, UserFactory.PASSWORD));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"1@Abc", "12345678", "abcdefgh", "ABCDEFGH", "12345678abcdefgh", "12345678ABCDEFGH",
                            "abcdefghABCDEFGH", "abcdefgh12345678", "ABCDEFGH12345678",
                            "abcdefghABCDEFGH12345678", "!@#$&* _-", "abcdefgh!@#$&*",
                            "ABCDEFGH!@#$&*", "12345678!@#$&*", "abcdefgh12345678!@#$&*",
                            "ABCDEFGH12345678!@#$&*",
                            "1234567890@1234567890@1234567890@1234567890@1234567890@1234567890"})
    public void givenInvalidPassword_whenRegister_thenThrowException(String password) {
        User user = new User(UserFactory.EMAIL, UserFactory.USERNAME, password);
        
        assertThatExceptionOfType(InvalidPasswordException.class)
                .isThrownBy(() -> registerService.register(user, password));
    }
    
    @Test
    public void givenExistingUser_whenUnregister_thenDeleteUser() {
        UserFactory.createTestUser(userRepository);
        
        registerService.unregister(UserFactory.USER.getEmail());
        
        verify(userRepository, times(1)).deleteByEmail(UserFactory.USER.getEmail());
    }
    
    @Test
    public void givenUserWithMap_whenUnregister_thenDeleteMap() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(UUID.randomUUID().toString());
        
        registerService.unregister(UserFactory.USER.getEmail());
        
        verify(gameMapRepository, times(1)).deleteById(testUser.getMapId());
    }
}

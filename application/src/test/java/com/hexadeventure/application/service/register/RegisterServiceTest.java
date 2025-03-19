package com.hexadeventure.application.service.register;

import com.hexadeventure.application.exceptions.UserExistException;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.application.service.common.UserFactory;
import com.hexadeventure.model.user.User;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

public class RegisterServiceTest {
    private final UserRepository userRepository = mock(UserRepository.class);
    private final GameMapRepository gameMapRepository = mock(GameMapRepository.class);
    
    private final RegisterService registerService = new RegisterService(userRepository, gameMapRepository);
    
    @Test
    public void givenNewUser_whenRegister_thenSaveUser() {
        registerService.register(UserFactory.USER);
        
        verify(userRepository, times(1)).save(UserFactory.USER);
    }
    
    @Test
    public void givenExistingUser_whenRegister_thenThrowException() {
        UserFactory.createTestUser(userRepository);
        
        assertThatExceptionOfType(UserExistException.class).isThrownBy(() -> {
            registerService.register(UserFactory.USER);
        });
        
        verify(userRepository, never()).save(any());
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

package com.hexadeventure.application.service.register;

import org.junit.jupiter.api.Test;
import com.hexadeventure.application.exceptions.UserExistException;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.application.service.common.UserFactory;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

public class RegisterServiceTest {
    private final UserRepository userRepository = mock(UserRepository.class);
    
    private final RegisterService registerService = new RegisterService(userRepository);
    
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
}

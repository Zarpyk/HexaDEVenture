package com.hexadeventure.application.service.users;

import com.hexadeventure.application.exceptions.InvalidIdException;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.application.service.common.UserFactory;
import com.hexadeventure.model.user.User;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private final UserRepository userRepository = mock(UserRepository.class);
    
    private final UserService userService = new UserService(userRepository);
    
    @Test
    public void givenId_whenGetUser_thenReturnUser() {
        when(userRepository.findById(UserFactory.USER.getId())).thenReturn(Optional.of(UserFactory.USER));
        
        User user = userService.getUser(null, UserFactory.USER.getId());
        
        assertThat(user).isEqualTo(UserFactory.USER);
    }
    
    @Test
    public void givenAuthUser_whenGetUser_thenReturnUser() {
        when(userRepository.findByEmail(UserFactory.EMAIL)).thenReturn(Optional.of(UserFactory.USER));
        
        User user = userService.getUser(UserFactory.EMAIL, UUID.randomUUID().toString());
        
        assertThat(user).isEqualTo(UserFactory.USER);
    }
    
    @Test
    public void givenInvalidId_whenGetUser_thenThrowException() {
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());
        
        assertThatExceptionOfType(InvalidIdException.class)
                .isThrownBy(() -> userService.getUser(null, UUID.randomUUID().toString()));
    }
    
    @Test
    public void givenNothing_whenRegister_thenThrowException() {
        assertThatExceptionOfType(InvalidIdException.class)
                .isThrownBy(() -> userService.getUser(null, null));
    }
}

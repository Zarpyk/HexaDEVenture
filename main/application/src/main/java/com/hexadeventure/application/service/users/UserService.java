package com.hexadeventure.application.service.users;

import com.hexadeventure.application.exceptions.InvalidIdException;
import com.hexadeventure.application.port.in.users.UserUseCase;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.model.user.User;

import java.util.Optional;

public class UserService implements UserUseCase {
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public User getUser(String userId) {
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()) throw new InvalidIdException();
        return user.get();
    }
}

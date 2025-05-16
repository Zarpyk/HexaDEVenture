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
    public User getUser(String email, String userId) {
        if(email == null && userId == null) throw new InvalidIdException();
        Optional<User> user = email == null ? userRepository.findById(userId) : userRepository.findByEmail(email);
        if(user.isEmpty()) throw new InvalidIdException();
        return user.get();
    }
}

package com.hexadeventure.application.service.register;


import com.hexadeventure.application.exceptions.UserExistException;
import com.hexadeventure.application.port.in.login.RegisterUseCase;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.model.user.User;

public class RegisterService implements RegisterUseCase {
    private final UserRepository userRepository;
    
    public RegisterService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public void register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserExistException(user.getEmail());
        }
        userRepository.save(user);
    }
}

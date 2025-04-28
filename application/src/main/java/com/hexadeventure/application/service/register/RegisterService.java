package com.hexadeventure.application.service.register;


import com.hexadeventure.application.exceptions.UserExistException;
import com.hexadeventure.application.port.in.login.RegisterUseCase;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.model.user.User;

public class RegisterService implements RegisterUseCase {
    private final UserRepository userRepository;
    private final GameMapRepository gameMapRepository;
    
    public RegisterService(UserRepository userRepository, GameMapRepository gameMapRepository) {
        this.userRepository = userRepository;
        this.gameMapRepository = gameMapRepository;
    }
    
    @Override
    public void register(User user) {
        if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserExistException(user.getEmail());
        }
        userRepository.save(user);
    }
    
    @Override
    public void unregister(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        userRepository.deleteByEmail(email);
        if(user.getMapId() != null) {
            gameMapRepository.deleteById(user.getMapId());
        }
    }
}

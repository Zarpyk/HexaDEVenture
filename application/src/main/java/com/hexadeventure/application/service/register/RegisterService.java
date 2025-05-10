package com.hexadeventure.application.service.register;


import com.hexadeventure.application.exceptions.InvalidEmailException;
import com.hexadeventure.application.exceptions.InvalidPasswordException;
import com.hexadeventure.application.exceptions.InvalidUsernameException;
import com.hexadeventure.application.exceptions.UserExistException;
import com.hexadeventure.application.port.in.login.RegisterUseCase;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.model.user.User;

import java.util.regex.Pattern;

public class RegisterService implements RegisterUseCase {
    /**
     * Simplified email validation that doesn't strictly follow the different standards.
     * @see <a href="https://stackoverflow.com/a/8204716/11451105">Email Validation Regex</a>
     */
    private static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
                                                               Pattern.CASE_INSENSITIVE);
    /**
     * Minimum 8 characters
     * Maximum 64 characters
     * At least one uppercase letter
     * At least one lowercase letter
     * At least one digit
     * At least one special character (!@#$&*_-) or space
     * @see <a href="https://stackoverflow.com/a/5142164/11451105">Password Validation Regex</a>
     */
    private static final Pattern PASSWORD_REGEX = Pattern.compile("^(?=.*[A-Z])" +
                                                                  "(?=.*[!@#$&*_\\- ])" +
                                                                  "(?=.*[0-9])" +
                                                                  "(?=.*[a-z])" +
                                                                  ".{8,64}$");
    
    private final UserRepository userRepository;
    private final GameMapRepository gameMapRepository;
    
    public RegisterService(UserRepository userRepository, GameMapRepository gameMapRepository) {
        this.userRepository = userRepository;
        this.gameMapRepository = gameMapRepository;
    }
    
    @Override
    public void register(User user) {
        if(user.getEmail() == null || !EMAIL_REGEX.matcher(user.getEmail()).matches()) {
            throw new InvalidEmailException(user.getEmail());
        }
        if(user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new InvalidUsernameException();
        }
        if(user.getPassword() == null || !PASSWORD_REGEX.matcher(user.getPassword()).matches()) {
            throw new InvalidPasswordException();
        }
        
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

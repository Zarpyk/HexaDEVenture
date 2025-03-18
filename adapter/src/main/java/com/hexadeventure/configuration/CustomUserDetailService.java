package com.hexadeventure.configuration;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.model.user.User;

/**
 * Custom user details service.
 * @author Alfredo Rueda Unsain & Josep Roure Alcobé
 */
@Service
public class CustomUserDetailService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                                  .orElseThrow(() -> new UsernameNotFoundException(
                                          "User Not Found with username: " + username));
        return new CustomUserDetails(user);
    }
}

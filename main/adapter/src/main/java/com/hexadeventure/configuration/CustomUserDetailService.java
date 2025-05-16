package com.hexadeventure.configuration;

import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.model.user.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                                  .orElseThrow(() -> new UsernameNotFoundException(
                                          "User Not Found with email: " + email));
        return new CustomUserDetails(user);
    }
}

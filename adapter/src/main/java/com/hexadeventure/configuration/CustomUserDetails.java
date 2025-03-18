package com.hexadeventure.configuration;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.hexadeventure.model.user.User;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails, CredentialsContainer {
    
    private final String email;
    private String password;
    
    public CustomUserDetails(User user) {
        this.email = user.getEmail();
        this.password = user.getPassword();
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public String getUsername() {
        return email;
    }
    
    @Override
    public void eraseCredentials() {
        password = null;
    }
}

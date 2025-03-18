package com.hexadeventure.adapter.in.rest.login;

import com.hexadeventure.configuration.PasswordEncoderConfig;
import com.hexadeventure.model.user.User;

public record UserDTO(String email, String username, String password) {
    public User toModel() {
        return new User(email, username, PasswordEncoderConfig.passwordEncoder().encode(password));
    }
}

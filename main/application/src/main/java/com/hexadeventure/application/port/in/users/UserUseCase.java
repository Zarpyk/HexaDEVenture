package com.hexadeventure.application.port.in.users;

import com.hexadeventure.model.user.User;

public interface UserUseCase {
    User getUser(String email, String userId);
}

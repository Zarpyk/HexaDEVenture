package com.hexadeventure.adapter.in.rest.common;

import java.security.Principal;

public class UserPrincipal implements Principal {
    @Override
    public String getName() {
        return UserFactory.EMAIL;
    }
}

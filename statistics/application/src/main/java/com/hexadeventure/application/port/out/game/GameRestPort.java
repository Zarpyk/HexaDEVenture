package com.hexadeventure.application.port.out.game;


import com.hexadeventure.model.user.UserInfo;

public interface GameRestPort {
    UserInfo getUserInfo(String userId);
}

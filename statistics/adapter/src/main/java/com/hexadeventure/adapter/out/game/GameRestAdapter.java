package com.hexadeventure.adapter.out.game;

import com.hexadeventure.application.port.out.game.GameRestPort;
import com.hexadeventure.configuration.GameServiceConfig;
import com.hexadeventure.model.user.UserInfo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@SuppressWarnings("HttpUrlsUsage")
@Component
public class GameRestAdapter implements GameRestPort {
    private final RestClient restClient;
    
    public GameRestAdapter(RestClient.Builder restClient, GameServiceConfig configuration) {
        this.restClient = restClient
                .baseUrl("http://" + configuration.getHost() + ":" + configuration.getPort())
                .build();
    }
    
    public UserInfo getUserInfo(String userId) {
        try {
            UserInfoDTO body = restClient.get()
                                         .uri("/userInfo?userId=" + userId)
                                         .retrieve()
                                         .body(UserInfoDTO.class);
            if(body == null) return null;
            return body.toModel();
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.BAD_REQUEST) return null;
            else throw e;
        }
    }
}

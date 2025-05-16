package com.hexadeventure.adapter.out.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hexadeventure.application.port.out.game.GameRestPort;
import com.hexadeventure.configuration.GameServiceConfig;
import com.hexadeventure.model.user.UserInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class GameRestAdapterTest {
    private final static String HOST = "http://localhost:8080";
    
    private final static UserInfoDTO USER_INFO_DTO = new UserInfoDTO(UUID.randomUUID().toString(),
                                                                     "",
                                                                     "",
                                                                     0,
                                                                     0,
                                                                     0,
                                                                     null,
                                                                     0,
                                                                     0);
    
    
    private GameRestPort gameRestPort;
    
    private MockRestServiceServer server;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @BeforeEach
    public void beforeEach() {
        RestClient.Builder restClientBuilder = RestClient.builder();
        server = MockRestServiceServer.bindTo(restClientBuilder).build();
        GameServiceConfig gameServiceConfig = new GameServiceConfig();
        gameServiceConfig.setHost("localhost");
        gameServiceConfig.setPort(8080);
        gameRestPort = new GameRestAdapter(restClientBuilder, gameServiceConfig);
    }
    
    @Test
    public void givenId_whenGetUserInfo_thenReturnUserInfo() throws JsonProcessingException {
        String detailsString = objectMapper.writeValueAsString(USER_INFO_DTO);
        
        server.expect(requestTo(HOST + "/userInfo?userId=" + USER_INFO_DTO.id()))
              .andRespond(withSuccess(detailsString, MediaType.APPLICATION_JSON));
        
        UserInfo userInfo = gameRestPort.getUserInfo(USER_INFO_DTO.id());
        assertThat(userInfo).isNotNull();
        assertThat(userInfo.getId()).isEqualTo(USER_INFO_DTO.id());
    }
    
    @Test
    public void givenInvalidId_whenGetUserInfo_thenReturnNull() {
        this.server.expect(requestTo(HOST + "/userInfo?userId=" + USER_INFO_DTO.id()))
                   .andRespond(withBadRequest());
        
        UserInfo userInfo = gameRestPort.getUserInfo(USER_INFO_DTO.id());
        assertThat(userInfo).isNull();
    }
}

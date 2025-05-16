package com.hexadeventure;

import com.hexadeventure.application.port.in.users.StatsUseCase;
import com.hexadeventure.application.port.out.game.GameRestPort;
import com.hexadeventure.application.service.users.StatsService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication()
@OpenAPIDefinition(
        info = @Info(title = "HexaDEVenture Stats API",
                     version = "v1",
                     license = @License(name = "Commons Clause + Apache 2.0",
                                        url = "https://github.com/Zarpyk/HexaDEVenture/blob/main/LICENSE")))
public class HexaDEVentureStatsApplication {
    private final GameRestPort gameRestPort;
    
    public HexaDEVentureStatsApplication(GameRestPort gameRestPort) {
        this.gameRestPort = gameRestPort;
    }
    
    @Bean
    public StatsUseCase statsUseCase() {
        return new StatsService(gameRestPort);
    }
}

package com.hexadeventure;

import com.hexadeventure.application.port.in.game.GameUseCase;
import com.hexadeventure.application.port.in.login.RegisterUseCase;
import com.hexadeventure.application.port.out.noise.NoiseGenerator;
import com.hexadeventure.application.port.out.pathfinder.AStarPathfinder;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.application.service.game.GameService;
import com.hexadeventure.application.service.register.RegisterService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication()
public class HexaDEVentureApplication {
    private final UserRepository userRepository;
    private final GameMapRepository gameMapRepository;
    
    private final NoiseGenerator noiseGenerator;
    private final AStarPathfinder aStarAdapter;
    
    public HexaDEVentureApplication(UserRepository userRepository, GameMapRepository gameMapRepository,
                                    NoiseGenerator noiseGenerator, AStarPathfinder aStarAdapter) {
        this.userRepository = userRepository;
        this.gameMapRepository = gameMapRepository;
        this.noiseGenerator = noiseGenerator;
        this.aStarAdapter = aStarAdapter;
    }
    
    
    @Bean
    public RegisterUseCase registerUseCase() {
        return new RegisterService(userRepository, gameMapRepository);
    }
    
    @Bean
    public GameUseCase gameUseCase() {
        return new GameService(userRepository, gameMapRepository, noiseGenerator, aStarAdapter);
    }
}

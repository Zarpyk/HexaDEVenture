package com.hexadeventure;

import com.hexadeventure.application.port.in.game.CombatUseCase;
import com.hexadeventure.application.port.in.game.GameUseCase;
import com.hexadeventure.application.port.in.login.RegisterUseCase;
import com.hexadeventure.application.port.out.noise.NoiseGenerator;
import com.hexadeventure.application.port.out.pathfinder.AStarPathfinder;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.application.port.out.settings.SettingsImporter;
import com.hexadeventure.application.service.game.CombatService;
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
    private final SettingsImporter settingsImporter;
    
    public HexaDEVentureApplication(UserRepository userRepository, GameMapRepository gameMapRepository,
                                    NoiseGenerator noiseGenerator, AStarPathfinder aStarAdapter,
                                    SettingsImporter settingsImporter) {
        this.userRepository = userRepository;
        this.gameMapRepository = gameMapRepository;
        this.noiseGenerator = noiseGenerator;
        this.aStarAdapter = aStarAdapter;
        this.settingsImporter = settingsImporter;
    }
    
    
    @Bean
    public RegisterUseCase registerUseCase() {
        return new RegisterService(userRepository, gameMapRepository);
    }
    
    @Bean
    public GameUseCase gameUseCase() {
        return new GameService(userRepository, gameMapRepository, noiseGenerator, aStarAdapter, settingsImporter);
    }
    
    @Bean
    public CombatUseCase combatUseCase() {
        return new CombatService(userRepository, gameMapRepository);
    }
}

package com.hexadeventure.application.service.game;

import org.junit.jupiter.api.Test;
import com.hexadeventure.application.exceptions.GameStartedException;
import com.hexadeventure.application.port.out.noise.NoiseGenerator;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.application.service.common.UserFactory;
import com.hexadeventure.model.map.GameMap;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GameServiceTest {
    private static final String TEST_USER_EMAIL = "test@test.com";
    private static final long TEST_SEED = 1234;
    private static final int TEST_SIZE = 100;
    
    private final UserRepository userRepository = mock(UserRepository.class);
    private final GameMapRepository gameMapRepository = mock(GameMapRepository.class);
    private final NoiseGenerator noiseGenerator = mock(NoiseGenerator.class);
    private final GameService gameService = new GameService(userRepository, gameMapRepository, noiseGenerator);
    
    @Test
    public void givenUserWithSeedAndSize_whenItDontHaveStartedGame_thenCreateNewMap() {
        UserFactory.createTestUser(userRepository);
        
        gameService.startGame(TEST_USER_EMAIL, TEST_SEED, TEST_SIZE);
        
        verify(noiseGenerator, times(1)).initNoise(any(), eq(TEST_SEED));
        verify(noiseGenerator, times(TEST_SIZE * TEST_SIZE))
                .getPerlinNoise(any(Double.class), any(Double.class), any());
        verify(noiseGenerator, times(1)).releaseNoise(any());
        
        verify(userRepository, times(1)).updateMapIdByEmail(eq(UserFactory.EMAIL), any());
        verify(gameMapRepository, times(1)).save(any());
    }
    
    @Test
    public void givenUserWithSeedAndSize_whenItHaveStartedGame_thenThrowException() {
        UserFactory.createTestUser(userRepository);
        
        gameService.startGame(TEST_USER_EMAIL, TEST_SEED, TEST_SIZE);
        verify(gameMapRepository, times(1)).save(any());
        
        Optional<GameMap> gameMap = Optional.of(new GameMap(TEST_USER_EMAIL, TEST_SEED, TEST_SIZE));
        when(gameMapRepository.findById(any())).thenReturn(gameMap);
        
        assertThatExceptionOfType(GameStartedException.class).isThrownBy(() -> {
            gameService.startGame(TEST_USER_EMAIL, TEST_SEED, TEST_SIZE);
        });
        
        verify(gameMapRepository, times(1)).save(any());
    }
}

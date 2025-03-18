package com.hexadeventure.application.service.game;

import com.hexadeventure.application.exceptions.GameStartedException;
import com.hexadeventure.application.port.in.game.GameUseCase;
import com.hexadeventure.application.port.out.noise.NoiseGenerator;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.model.map.GameMap;
import com.hexadeventure.model.map.Vector2;
import com.hexadeventure.model.user.User;

import java.util.Optional;

public class GameService implements GameUseCase {
    private final UserRepository userRepository;
    private final GameMapRepository gameMapRepository;
    private final NoiseGenerator noiseGenerator;
    
    public GameService(UserRepository userRepository, GameMapRepository gameMapRepository,
                       NoiseGenerator noiseGenerator) {
        this.userRepository = userRepository;
        this.gameMapRepository = gameMapRepository;
        this.noiseGenerator = noiseGenerator;
    }
    
    @Override
    public void startGame(String email, long seed, int size) {
        Optional<User> user = userRepository.findByEmail(email);
        assert user.isPresent();
        Optional<GameMap> map = gameMapRepository.findById(user.get().getMapId());
        if(map.isEmpty()) {
            GameMap newMap = generateMap(email, seed, size);
            gameMapRepository.save(newMap);
            userRepository.updateMapIdByEmail(email, newMap.getId());
        } else {
            throw new GameStartedException();
        }
    }
    
    private GameMap generateMap(String email, long seed, int size) {
        GameMap map = new GameMap(email, seed, size);
        generateCells(email, seed, size, map);
        generatePlayer(map);
        return map;
    }
    
    private void generateCells(String email, long seed, int size, GameMap map) {
        noiseGenerator.initNoise(email, seed);
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                double noise = noiseGenerator.getPerlinNoise(x, y, email);
                map.createCell(noise, x, y);
            }
        }
        noiseGenerator.releaseNoise(email);
    }
    
    private void generatePlayer(GameMap map) {
        int mapSize = map.getMapSize();
        Vector2 center = new Vector2(mapSize / 2, mapSize / 2);
        map.initMainCharacter(center);
    }
}

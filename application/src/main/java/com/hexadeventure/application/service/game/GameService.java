package com.hexadeventure.application.service.game;

import com.hexadeventure.application.exceptions.GameNotStartedException;
import com.hexadeventure.application.exceptions.GameStartedException;
import com.hexadeventure.application.exceptions.MapSizeException;
import com.hexadeventure.application.port.in.game.GameUseCase;
import com.hexadeventure.application.port.out.noise.NoiseGenerator;
import com.hexadeventure.application.port.out.pathfinder.AStarPathfinder;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.model.map.GameMap;
import com.hexadeventure.model.map.Vector2;
import com.hexadeventure.model.user.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

public class GameService implements GameUseCase {
    public static final int MIN_MAP_SIZE = 100;
    
    private final UserRepository userRepository;
    private final GameMapRepository gameMapRepository;
    private final NoiseGenerator noiseGenerator;
    private final AStarPathfinder aStarPathfinder;
    
    public GameService(UserRepository userRepository, GameMapRepository gameMapRepository,
                       NoiseGenerator noiseGenerator, AStarPathfinder aStarPathfinder) {
        this.userRepository = userRepository;
        this.gameMapRepository = gameMapRepository;
        this.noiseGenerator = noiseGenerator;
        this.aStarPathfinder = aStarPathfinder;
    }
    
    @Override
    @Transactional
    public void startGame(String email, long seed, int size) {
        Optional<User> user = userRepository.findByEmail(email);
        assert user.isPresent();
        if(user.get().getMapId() != null) throw new GameStartedException();
        if(size < MIN_MAP_SIZE) {
            throw new MapSizeException(MIN_MAP_SIZE);
        }
        /*MapGenerator mapGenerator = new MapGenerator(noiseGenerator, aStarPathfinder);
        GameMap newMap = mapGenerator.generateMap(email, seed, size);*/
        GameMap newMap = new GameMap(email, seed, size);
        gameMapRepository.save(newMap);
        userRepository.updateMapIdByEmail(email, newMap.getId());
    }
    
    @Override
    public MovementResponseDTO move(String email, Vector2 positionToMove) {
        Optional<User> user = userRepository.findByEmail(email);
        assert user.isPresent();
        if(user.get().getMapId() == null) throw new GameNotStartedException();
        Optional<GameMap> map = gameMapRepository.findById(user.get().getMapId());
        assert map.isPresent();
        List<MovementActionDTO> actions = new ArrayList<>();
        
        /*Queue<Vector2> path = aStarPathfinder.generatePath(map.get().getMainCharacter().getPosition(),
                                                           positionToMove,
                                                           map.get().getCostMap(true));
        
        // TODO US 1.8 & 1.9
        
        while (!path.isEmpty()) {
            Vector2 position = path.poll();
            actions.add(new MovementActionDTO(position.x, position.y, null, null));
        }*/
        
        return new MovementResponseDTO(actions);
    }
}

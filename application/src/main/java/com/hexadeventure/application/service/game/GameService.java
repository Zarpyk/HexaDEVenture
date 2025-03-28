package com.hexadeventure.application.service.game;

import com.hexadeventure.application.exceptions.GameNotStartedException;
import com.hexadeventure.application.exceptions.GameStartedException;
import com.hexadeventure.application.exceptions.MapSizeException;
import com.hexadeventure.application.port.in.game.GameUseCase;
import com.hexadeventure.application.port.out.noise.NoiseGenerator;
import com.hexadeventure.application.port.out.pathfinder.AStarPathfinder;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.model.inventory.materials.Material;
import com.hexadeventure.model.map.*;
import com.hexadeventure.model.map.resources.Resource;
import com.hexadeventure.model.movement.MovementAction;
import com.hexadeventure.model.movement.MovementResponse;
import com.hexadeventure.model.movement.ResourceAction;
import com.hexadeventure.model.user.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

public class GameService implements GameUseCase {
    // Use only odd numbers
    public static final int MIN_SQUARE_SIZE = 3;
    public static final int MIN_MAP_SIZE = Chunk.SIZE * (MIN_SQUARE_SIZE * MIN_SQUARE_SIZE);
    
    /**
     * The distance in chunks from the player chunk to render
     */
    public static final int RENDER_DISTANCE = 1;
    
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
            throw new MapSizeException("Map size must be greater than " + MIN_MAP_SIZE);
        }
        if(size % 16 != 0) {
            throw new MapSizeException("Map size must be a multiple of 16");
        }
        MapGenerator mapGenerator = new MapGenerator(noiseGenerator, aStarPathfinder);
        GameMap newMap = mapGenerator.initialMapGeneration(email, seed, size);
        gameMapRepository.save(newMap);
        userRepository.updateMapIdByEmail(email, newMap.getId());
    }
    
    @Override
    public MovementResponse move(String email, Vector2 positionToMove) {
        Optional<User> user = userRepository.findByEmail(email);
        assert user.isPresent();
        if(user.get().getMapId() == null) throw new GameNotStartedException();
        Optional<GameMap> map = gameMapRepository.findById(user.get().getMapId());
        assert map.isPresent();
        GameMap gameMap = map.get();
        List<MovementAction> actions = new ArrayList<>();
        
        MainCharacter mainCharacter = map.get().getMainCharacter();
        Vector2C currentChunk = Chunk.getChunkPosition(mainCharacter.getPosition());
        
        // Check the position to move is not a wall
        Map<Vector2C, Chunk> mapChunks = gameMapRepository.findMapChunks(gameMap.getId(), Set.of(currentChunk));
        gameMap.addChunks(mapChunks, false);
        CellData cell = gameMap.getCell(positionToMove);
        if(cell.getType() == CellType.WALL) {
            return new MovementResponse(actions);
        }
        
        // Get chunks around the player
        Set<Vector2C> chunkArroundPlayer = currentChunk.getArroundPositions(RENDER_DISTANCE, false);
        mapChunks = gameMapRepository.findMapChunks(gameMap.getId(), chunkArroundPlayer);
        gameMap.addChunks(mapChunks, false);
        
        // Generate the path
        Queue<Vector2> path = aStarPathfinder.generatePath(mainCharacter.getPosition(),
                                                           positionToMove,
                                                           gameMap.getCostMap(chunkArroundPlayer, true));
        
        while (!path.isEmpty()) {
            Vector2 position = path.poll();
            
            Chunk chunk = gameMap.getChunkOfCell(position);
            Resource resource = chunk.getResource(position);
            ResourceAction resourceAction = null;
            if(resource != null) {
                resourceAction = new ResourceAction(resource.getType().ordinal(), resource.getCount());
                gameMap.getInventory().addItem(new Material(resource.getType().name(),
                                                            resource.getType()), resource.getCount());
                chunk.removeResource(position);
            }
            
            // TODO US 1.9
            
            MovementAction movementAction = new MovementAction(position.x, position.y, resourceAction, null);
            actions.add(movementAction);
        }
        
        return new MovementResponse(actions);
    }
}

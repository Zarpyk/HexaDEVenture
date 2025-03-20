package com.hexadeventure.application.service.game;

import com.hexadeventure.application.exceptions.GameStartedException;
import com.hexadeventure.application.exceptions.MapSizeException;
import com.hexadeventure.application.port.in.game.GameUseCase;
import com.hexadeventure.application.port.out.noise.NoiseGenerator;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.model.map.CellType;
import com.hexadeventure.model.map.EmptyCell;
import com.hexadeventure.model.map.GameMap;
import com.hexadeventure.model.map.Vector2;
import com.hexadeventure.model.map.obstacles.ObstacleCell;
import com.hexadeventure.model.map.obstacles.ObstacleType;
import com.hexadeventure.model.map.resources.ResourceCell;
import com.hexadeventure.model.user.User;
import com.hexadeventure.utils.DoubleMapper;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Random;

public class GameService implements GameUseCase {
    public static final int MIN_MAP_SIZE = 100;
    
    private static final int CLEAR_RADIUS_AROUND_PLAYER = 10;
    private static final double PLAYER_CIRCLE_CLEAR_THRESHOLD = 0.15;
    private static final int CLEAR_AROUND_PLAYER_CIRCLE_VARIATION = 0;
    
    private static final int BORDER_OFFSET = 2;
    private static final double BORDER_THRESHOLD = 0.2;
    private static final int GENERATE_BORDER_CIRCLE_VARIATION = 1;
    private static final ObstacleType BORDER_OBSTACLE_TYPE = ObstacleType.WALL;
    
    private static final int GENERATE_RESOURCE_PROBABILITY = 5;
    
    private static final int BOSS_BORDER_OFFSET = 10;
    
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
    @Transactional
    public void startGame(String email, long seed, int size) {
        Optional<User> user = userRepository.findByEmail(email);
        assert user.isPresent();
        if(user.get().getMapId() == null) {
            if(size < MIN_MAP_SIZE) {
                throw new MapSizeException(MIN_MAP_SIZE);
            }
            GameMap newMap = generateMap(email, seed, size);
            printMap(newMap);
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
        clearPlayerPosition(map);
        generateBorder(map);
        generateResources(map);
        return map;
    }
    
    /**
     * Temporal method generated with Claude 3.5 to print the map as an image
     * @param map The map to print
     */
    @Deprecated(forRemoval = true)
    private void printMap(GameMap map) {
        int size = map.getMapSize();
        int cellSize = 10; // Each cell will be 10x10 pixels
        int gridThickness = 1;
        int imageSize = size * cellSize;
        
        BufferedImage image = new BufferedImage(size * cellSize, size * cellSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // Draw background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, imageSize, imageSize);
        
        // Draw cells
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                int pixelX = x * cellSize;
                int pixelY = y * cellSize;
                
                if(map.getCell(x, y).getType() == CellType.OBSTACLE) {
                    // Draw obstacle cells in black
                    g2d.setColor(Color.BLACK);
                    g2d.fillRect(pixelX, pixelY, cellSize, cellSize);
                } else if(map.getCell(x, y).getType() == CellType.RESOURCE) {
                    // Draw resource cells in green
                    g2d.setColor(Color.GREEN);
                    g2d.fillRect(pixelX, pixelY, cellSize, cellSize);
                }
                
                // Draw grid lines
                g2d.setColor(Color.GRAY);
                g2d.drawRect(pixelX, pixelY, cellSize, cellSize);
            }
        }
        
        // Mark player position
        Vector2 playerPos = map.getMainCharacter().getPosition();
        g2d.setColor(Color.RED);
        g2d.fillOval(playerPos.x * cellSize + cellSize / 4,
                     playerPos.y * cellSize + cellSize / 4,
                     cellSize / 2,
                     cellSize / 2);
        
        g2d.dispose();
        
        try {
            File outputfile = new File("map.png");
            ImageIO.write(image, "png", outputfile);
            System.out.println(outputfile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    private void generateCells(String email, long seed, int size, GameMap map) {
        noiseGenerator.initNoise(email, seed, 0.1,
                                 4, 0.5, 1.5, NoiseGenerator.FRACTAL_FBM, true,
                                 true);
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                double noise = noiseGenerator.getPerlinNoise(x, y, email, false);
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
    
    private void clearPlayerPosition(GameMap map) {
        double[][] circle = noiseGenerator.getCircleWithNoisyEdge(CLEAR_RADIUS_AROUND_PLAYER,
                                                                  map.getSeed(),
                                                                  CLEAR_AROUND_PLAYER_CIRCLE_VARIATION);
        Vector2 playerPosition = map.getMainCharacter().getPosition();
        int centerX = playerPosition.x;
        int centerY = playerPosition.y;
        
        for (int x = 0; x < circle.length; x++) {
            for (int y = 0; y < circle[x].length; y++) {
                if(circle[x][y] < PLAYER_CIRCLE_CLEAR_THRESHOLD) {
                    Vector2 position = new Vector2(centerX - CLEAR_RADIUS_AROUND_PLAYER + x,
                                                   centerY - CLEAR_RADIUS_AROUND_PLAYER + y);
                    map.setCell(position, new EmptyCell(position));
                }
            }
        }
    }
    
    private void generateBorder(GameMap map) {
        double[][] circle = noiseGenerator.getCircleWithNoisyEdge(map.getMapSize() / 2 - BORDER_OFFSET,
                                                                  map.getSeed(),
                                                                  GENERATE_BORDER_CIRCLE_VARIATION);
        
        for (int x = 0; x < map.getMapSize(); x++) {
            for (int y = 0; y < map.getMapSize(); y++) {
                if(x < BORDER_OFFSET || y < BORDER_OFFSET || x >= map.getMapSize() - BORDER_OFFSET ||
                   y >= map.getMapSize() - BORDER_OFFSET) {
                    map.setCell(new Vector2(x, y), new ObstacleCell(new Vector2(x, y), BORDER_OBSTACLE_TYPE));
                } else {
                    int circleX = x - BORDER_OFFSET;
                    int circleY = y - BORDER_OFFSET;
                    if(circle[circleX][circleY] > BORDER_THRESHOLD) {
                        Vector2 position = new Vector2(x, y);
                        map.setCell(position, new ObstacleCell(position, BORDER_OBSTACLE_TYPE));
                    }
                }
            }
        }
    }
    
    
    private void generateResources(GameMap map) {
        Random random = new Random(map.getSeed());
        
        double probability = GENERATE_RESOURCE_PROBABILITY / 100.0;
        
        int seedOffset = 0;
        for (int x = 0; x < map.getMapSize(); x++) {
            for (int y = 0; y < map.getMapSize(); y++) {
                if(map.getCell(x, y).getType() == CellType.EMPTY) {
                    if(Math.abs(x - map.getMainCharacter().getPosition().x) <= CLEAR_RADIUS_AROUND_PLAYER &&
                       Math.abs(y - map.getMainCharacter().getPosition().y) <= CLEAR_RADIUS_AROUND_PLAYER) {
                        continue;
                    }
                    double randomValue = random.nextDouble();
                    if(randomValue < probability) {
                        double threshold = DoubleMapper.map(randomValue, 0, probability, -1, 1);
                        map.setCell(new Vector2(x, y), new ResourceCell(new Vector2(x, y), threshold));
                    }
                }
            }
        }
    }
}

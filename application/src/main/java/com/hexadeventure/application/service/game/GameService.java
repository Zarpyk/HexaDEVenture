package com.hexadeventure.application.service.game;

import com.hexadeventure.application.exceptions.GameStartedException;
import com.hexadeventure.application.exceptions.MapSizeException;
import com.hexadeventure.application.port.in.game.GameUseCase;
import com.hexadeventure.application.port.out.noise.NoiseGenerator;
import com.hexadeventure.application.port.out.pathfinder.AStarPathfinder;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.model.enemies.Boss;
import com.hexadeventure.model.enemies.Enemy;
import com.hexadeventure.model.map.CellType;
import com.hexadeventure.model.map.GameMap;
import com.hexadeventure.model.map.Vector2;
import com.hexadeventure.model.map.resources.Resource;
import com.hexadeventure.model.user.User;
import com.hexadeventure.utils.DoubleMapper;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;

public class GameService implements GameUseCase {
    public static final int MIN_MAP_SIZE = 100;
    
    private static final int CLEAR_RADIUS_AROUND_PLAYER = 10;
    private static final double PLAYER_CIRCLE_CLEAR_THRESHOLD = 0.15;
    private static final int CLEAR_AROUND_PLAYER_CIRCLE_VARIATION = 0;
    
    private static final int BORDER_OFFSET = 2;
    private static final double BORDER_THRESHOLD = 0.2;
    private static final int GENERATE_BORDER_CIRCLE_VARIATION = 1;
    private static final CellType BORDER_OBSTACLE_TYPE = CellType.WALL;
    
    private static final int GENERATE_RESOURCE_PROBABILITY = 5;
    
    private static final int BOSS_BORDER_OFFSET = 10;
    private static final int BOSS_CLEAR_RADIUS = 5;
    private static final double BOSS_CLEAR_CIRCLE_THRESHOLD = 0.15;
    private static final int BOSS_CLEAR_CIRCLE_VARIATION = 0;
    private static final int BOSS_PATH_EXTRA_WIDTH = 1;
    
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
        Random random = new Random(seed);
        GameMap map = new GameMap(email, seed, size);
        generateCells(email, seed, size, map);
        generatePlayer(map);
        generateBorder(map);
        generateResources(map, random);
        generateFinalBoss(map, random);
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
                
                switch (map.getCell(x, y).getType()) {
                    case WALL -> {
                        // Draw obstacle cells in black
                        g2d.setColor(Color.BLACK);
                        g2d.fillRect(pixelX, pixelY, cellSize, cellSize);
                    }
                    case PATH -> {
                        // Draw path cells in gray
                        g2d.setColor(Color.GRAY);
                        g2d.fillRect(pixelX, pixelY, cellSize, cellSize);
                    }
                    case null, default -> {
                    }
                }
                
                // Draw grid lines
                g2d.setColor(Color.GRAY);
                g2d.drawRect(pixelX, pixelY, cellSize, cellSize);
            }
        }
        
        for (Map.Entry<Vector2, Resource> resource : map.getResources().entrySet()) {
            Vector2 position = resource.getKey();
            int pixelX = position.x * cellSize;
            int pixelY = position.y * cellSize;
            
            // Draw resources in green
            g2d.setColor(Color.GREEN);
            g2d.fillRect(pixelX, pixelY, cellSize, cellSize);
        }
        
        for (Map.Entry<Vector2, Enemy> enemy : map.getEnemies().entrySet()) {
            Vector2 position = enemy.getKey();
            int pixelX = position.x * cellSize;
            int pixelY = position.y * cellSize;
            
            if(enemy.getValue() instanceof Boss) {
                // Draw boss in red
                g2d.setColor(Color.RED);
                g2d.fillRect(pixelX, pixelY, cellSize, cellSize);
            } else {
                // Draw enemies in blue
                g2d.setColor(Color.BLUE);
                g2d.fillRect(pixelX, pixelY, cellSize, cellSize);
            }
            g2d.fillRect(pixelX, pixelY, cellSize, cellSize);
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
        clearPosition(map, center,
                      CLEAR_RADIUS_AROUND_PLAYER,
                      PLAYER_CIRCLE_CLEAR_THRESHOLD,
                      CLEAR_AROUND_PLAYER_CIRCLE_VARIATION,
                      true);
    }
    
    private void generateBorder(GameMap map) {
        double[][] circle = noiseGenerator.getCircleWithNoisyEdge(map.getMapSize() / 2 - BORDER_OFFSET,
                                                                  map.getSeed(),
                                                                  GENERATE_BORDER_CIRCLE_VARIATION);
        
        for (int x = 0; x < map.getMapSize(); x++) {
            for (int y = 0; y < map.getMapSize(); y++) {
                if(x < BORDER_OFFSET || y < BORDER_OFFSET || x >= map.getMapSize() - BORDER_OFFSET ||
                   y >= map.getMapSize() - BORDER_OFFSET) {
                    map.setCell(new Vector2(x, y), BORDER_OBSTACLE_TYPE);
                } else {
                    int circleX = x - BORDER_OFFSET;
                    int circleY = y - BORDER_OFFSET;
                    if(circle[circleX][circleY] > BORDER_THRESHOLD) {
                        Vector2 position = new Vector2(x, y);
                        map.setCell(position, BORDER_OBSTACLE_TYPE);
                    }
                }
            }
        }
    }
    
    
    private void generateResources(GameMap map, Random random) {
        double probability = GENERATE_RESOURCE_PROBABILITY / 100.0;
        
        int seedOffset = 0;
        for (int x = 0; x < map.getMapSize(); x++) {
            for (int y = 0; y < map.getMapSize(); y++) {
                if(map.getCell(x, y).getType() == CellType.GROUND) {
                    if(Math.abs(x - map.getMainCharacter().getPosition().x) <= CLEAR_RADIUS_AROUND_PLAYER &&
                       Math.abs(y - map.getMainCharacter().getPosition().y) <= CLEAR_RADIUS_AROUND_PLAYER) {
                        continue;
                    }
                    double randomValue = random.nextDouble();
                    if(randomValue < probability) {
                        double threshold = DoubleMapper.map(randomValue, 0, probability, -1, 1);
                        map.addResource(new Vector2(x, y), threshold, random);
                    }
                }
            }
        }
    }
    
    private void generateFinalBoss(GameMap map, Random random) {
        Vector2 bossPosition = getBossPosition(map, random);
        
        map.addEnemy(bossPosition, new Boss());
        clearPosition(map,
                      bossPosition,
                      BOSS_CLEAR_RADIUS,
                      BOSS_CLEAR_CIRCLE_THRESHOLD,
                      BOSS_CLEAR_CIRCLE_VARIATION,
                      false);
        generateRoadToBoss(map, bossPosition);
    }
    
    private static Vector2 getBossPosition(GameMap map, Random random) {
        int randomDirection = random.nextInt(4);
        Vector2 direction = switch (randomDirection) {
            case 0 -> Vector2.UP;
            case 1 -> Vector2.DOWN;
            case 2 -> Vector2.LEFT;
            case 3 -> Vector2.RIGHT;
            default -> throw new IllegalStateException("Unexpected value: " + randomDirection);
        };
        
        int center = map.getMapSize() / 2;
        int distance = map.getMapSize() / 2 - BOSS_BORDER_OFFSET;
        return new Vector2(center + distance * direction.x,
                           center + distance * direction.y);
    }
    
    private void generateRoadToBoss(GameMap map, Vector2 bossPosition) {
        int[][] mapCost = new int[map.getMapSize()][map.getMapSize()];
        for (int x = 0; x < map.getMapSize(); x++) {
            for (int y = 0; y < map.getMapSize(); y++) {
                CellType cellType = map.getCell(x, y).getType();
                mapCost[x][y] = CellType.getCost(cellType);
            }
        }
        Queue<Vector2> path = aStarPathfinder.generatePath(map.getMainCharacter().getPosition(),
                                                           bossPosition,
                                                           mapCost);
        if(path == null) throw new RuntimeException("Fail creating path to boss");
        
        Vector2 direction = Vector2.UP;
        while (!path.isEmpty()) {
            Vector2 position = path.poll();
            Vector2 nextPosition = path.peek();
            if(nextPosition != null) direction = position.subtract(nextPosition);
            direction.normalize();
            
            // Set the center path cell to empty cell
            if(!position.equals(bossPosition)) map.setCell(position, CellType.PATH);
            
            //noinspection NonStrictComparisonCanBeEquality
            for (int i = 1; i <= BOSS_PATH_EXTRA_WIDTH; i++) {
                // Set left and right cells to create a path
                Vector2 left = position.getLeft(direction, i);
                Vector2 right = position.getRight(direction, i);
                if(!left.equals(bossPosition)) map.setCell(left, CellType.PATH);
                if(!right.equals(bossPosition)) map.setCell(right, CellType.PATH);
            }
        }
    }
    
    private void clearPosition(GameMap map, Vector2 center, int radius, double threshold, int circleVariation,
                               boolean clearCenter) {
        double[][] circle = noiseGenerator.getCircleWithNoisyEdge(radius,
                                                                  map.getSeed(),
                                                                  circleVariation);
        
        for (int x = 0; x < circle.length; x++) {
            for (int y = 0; y < circle[x].length; y++) {
                if(!clearCenter && x == radius && y == radius) {
                    continue;
                }
                
                if(circle[x][y] < threshold) {
                    Vector2 position = new Vector2(center.x - radius + x,
                                                   center.y - radius + y);
                    map.setCell(position, CellType.GROUND);
                }
            }
        }
    }
}

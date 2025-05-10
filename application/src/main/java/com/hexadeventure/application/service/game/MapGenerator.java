package com.hexadeventure.application.service.game;

import com.hexadeventure.application.port.out.noise.NoiseGenerator;
import com.hexadeventure.application.port.out.pathfinder.AStarPathfinder;
import com.hexadeventure.application.port.out.settings.SettingsImporter;
import com.hexadeventure.model.enemies.Boss;
import com.hexadeventure.model.enemies.Enemy;
import com.hexadeventure.model.inventory.characters.EnemyPattern;
import com.hexadeventure.model.inventory.weapons.WeaponSetting;
import com.hexadeventure.model.inventory.weapons.WeaponType;
import com.hexadeventure.model.map.*;
import com.hexadeventure.model.map.resources.Resource;
import com.hexadeventure.utils.DoubleMapper;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.Queue;

public class MapGenerator {
    private static final int CLEAR_RADIUS_AROUND_PLAYER = 10;
    private static final double PLAYER_CIRCLE_CLEAR_THRESHOLD = 0.15;
    private static final int CLEAR_AROUND_PLAYER_CIRCLE_VARIATION = 0;
    
    private static final int BORDER_OFFSET = 2;
    private static final double BORDER_THRESHOLD = 0.2;
    private static final CellType BORDER_OBSTACLE_TYPE = CellType.WALL;
    private static final int GENERATE_BORDER_CIRCLE_VARIATION = 1;
    
    private static final int GENERATE_RESOURCE_PROBABILITY = 5;
    private static final int GENERATE_RESOURCE_CENTER_RADIUS = 5;
    private static final int GENERATE_RESOURCE_VARIATION = 2;
    
    private static final int BOSS_BORDER_OFFSET = BORDER_OFFSET + 10;
    private static final int BOSS_CLEAR_RADIUS = 5;
    private static final double BOSS_CLEAR_CIRCLE_THRESHOLD = 0.15;
    private static final int BOSS_PATH_EXTRA_WIDTH = 1;
    private static final int BOSS_PATHFINDING_GENERATE_CHUNKS_WIDTH = 1;
    private static final int BOSS_VARIATION = 3;
    
    private static final double GENERATE_ENEMY_PROBABILITY = 0.5;
    private static final double GENERATE_ENEMY_PROBABILITY_MAX_INCREMENT = 5;
    private static final int GENERATE_ENEMY_CENTER_RADIUS = 5;
    private static final int GENERATE_ENEMY_BOSS_RADIUS = 2;
    private static final int GENERATE_ENEMY_VARIATION = 4;
    
    private final NoiseGenerator noiseGenerator;
    private final AStarPathfinder aStarPathfinder;
    private final SettingsImporter settingsImporter;
    
    public MapGenerator(NoiseGenerator noiseGenerator, AStarPathfinder aStarPathfinder,
                        SettingsImporter settingsImporter) {
        this.noiseGenerator = noiseGenerator;
        this.aStarPathfinder = aStarPathfinder;
        this.settingsImporter = settingsImporter;
    }
    
    public GameMap initialMapGeneration(String email, long seed, int size) {
        int center = size / 2;
        Vector2C centerChunk = Chunk.getChunkPosition(new Vector2(center, center));
        
        int distance = (GameService.MIN_SQUARE_SIZE - 1) / 2;
        
        Set<Vector2C> chunksToGenerate = new HashSet<>(centerChunk.getArroundPositions(distance, true));
        
        GameMap map = new GameMap(email, seed, size);
        generateCells(map, chunksToGenerate, true);
        clearPlayerPosition(map, chunksToGenerate);
        generateBorder(map, chunksToGenerate);
        generateResources(map, chunksToGenerate);
        generateFinalBoss(map);
        generateEnemies(map, chunksToGenerate);
        printMap(map);
        return map;
    }
    
    public void generateSelectedChunks(GameMap map, Set<Vector2C> chunksToGenerate) {
        generateCells(map, chunksToGenerate, false);
        generateBorder(map, chunksToGenerate);
        generateResources(map, chunksToGenerate);
        generateEnemies(map, chunksToGenerate);
    }
    
    /**
     * Temporal method generated with Claude 3.5 to print the map as an image
     * @param map The map to print
     */
    @Deprecated(forRemoval = true)
    private void printMap(GameMap map) {
        int size = map.getSize();
        int cellSize = 10;
        int gridThickness = 1;
        int imageSize = size * cellSize;
        
        BufferedImage image = new BufferedImage(size * cellSize, size * cellSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // Draw background
        g2d.setColor(Color.GRAY);
        g2d.fillRect(0, 0, imageSize, imageSize);
        
        // Draw cells
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                int pixelX = x * cellSize;
                int pixelY = y * cellSize;
                
                if(!map.getChunks().containsKey(Chunk.getChunkPosition(new Vector2(x, y)))) {
                    continue;
                }
                
                CellData cell = map.getCell(new Vector2(x, y));
                if(cell == null) continue;
                
                switch (cell.getType()) {
                    case WALL -> {
                        // Draw obstacle cells in black
                        g2d.setColor(Color.BLACK);
                        g2d.fillRect(pixelX, pixelY, cellSize, cellSize);
                    }
                    case PATH -> {
                        // Draw path cells in gray
                        g2d.setColor(Color.DARK_GRAY);
                        g2d.fillRect(pixelX, pixelY, cellSize, cellSize);
                    }
                    case GROUND -> {
                        // Draw ground cells in white
                        g2d.setColor(Color.WHITE);
                        g2d.fillRect(pixelX, pixelY, cellSize, cellSize);
                    }
                    case GROUND2 -> {
                        // Draw ground2 cells in light gray
                        g2d.setColor(Color.LIGHT_GRAY);
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
        
        for (Chunk chunks : map.getChunks().values()) {
            for (Map.Entry<Vector2, Resource> resource : chunks.getResources().entrySet()) {
                Vector2 position = resource.getKey();
                int pixelX = position.x * cellSize;
                int pixelY = position.y * cellSize;
                
                // Draw resources in green
                g2d.setColor(Color.GREEN);
                g2d.fillRect(pixelX, pixelY, cellSize, cellSize);
            }
            
            for (Map.Entry<Vector2, Enemy> enemy : chunks.getEnemies().entrySet()) {
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
    
    private void generateCells(GameMap map, Set<Vector2C> chunksToGenerate, boolean canOverrideChunks) {
        noiseGenerator.initNoise(map.getUserEmail(), map.getSeed(), 0.1,
                                 4, 0.5, 1.5, NoiseGenerator.FRACTAL_FBM, true,
                                 true);
        // Parallel method from: https://stackoverflow.com/a/54448037
        HashMap<Vector2C, Chunk> chunks = new HashMap<>();
        for (Vector2C chunkPosition : chunksToGenerate) {
            chunks.put(chunkPosition, new Chunk(chunkPosition));
        }
        chunks.entrySet().stream().parallel().forEach(e -> {
            Chunk chunk = e.getValue();
            Vector2C position = chunk.getPosition();
            for (int x = position.getRealX(); x < position.getEndX(); x++) {
                for (int y = position.getRealY(); y < position.getEndY(); y++) {
                    double noise = noiseGenerator.getPerlinNoise(x, y, map.getUserEmail(), false);
                    chunk.createCell(noise, new Vector2(x, y));
                    e.setValue(chunk);
                }
            }
        });
        
        if(map.getChunks() == null) map.setChunks(chunks);
        else map.addChunks(chunks, canOverrideChunks);
        
        noiseGenerator.releaseNoise(map.getUserEmail());
    }
    
    private void clearPlayerPosition(GameMap map, Set<Vector2C> chunksToGenerate) {
        clearPosition(map, map.getMainCharacter().getPosition(),
                      CLEAR_RADIUS_AROUND_PLAYER,
                      PLAYER_CIRCLE_CLEAR_THRESHOLD,
                      CLEAR_AROUND_PLAYER_CIRCLE_VARIATION,
                      true,
                      chunksToGenerate);
    }
    
    private void generateBorder(GameMap map, Set<Vector2C> chunksToGenerate) {
        Map<Vector2, Double> circle = noiseGenerator.getCircleWithNoisyEdge(map.getSize() / 2 - BORDER_OFFSET,
                                                                            new Vector2(map.getSize() / 2,
                                                                                        map.getSize() / 2),
                                                                            map.getSeed(),
                                                                            GENERATE_BORDER_CIRCLE_VARIATION,
                                                                            chunksToGenerate);
        
        for (Vector2C chunk : chunksToGenerate) {
            for (int x = chunk.getRealX(); x < chunk.getEndX(); x++) {
                for (int y = chunk.getRealY(); y < chunk.getEndY(); y++) {
                    Vector2 position = new Vector2(x, y);
                    if(x < BORDER_OFFSET || y < BORDER_OFFSET ||
                       x > map.getSize() - BORDER_OFFSET || y > map.getSize() - BORDER_OFFSET) {
                        map.setCell(position, BORDER_OBSTACLE_TYPE);
                    } else if(circle.getOrDefault(position, 0d) > BORDER_THRESHOLD) {
                        map.setCell(position, BORDER_OBSTACLE_TYPE);
                    }
                }
            }
        }
    }
    
    
    private void generateResources(GameMap map, Set<Vector2C> chunksToGenerate) {
        double probability = GENERATE_RESOURCE_PROBABILITY / 100.0;
        
        for (Vector2C chunk : chunksToGenerate) {
            for (int x = chunk.getRealX(); x < chunk.getEndX(); x++) {
                for (int y = chunk.getRealY(); y < chunk.getEndY(); y++) {
                    Vector2 position = new Vector2(x, y);
                    if(map.getCell(position).getType() == CellType.GROUND) {
                        if(Math.abs(x - map.getMainCharacter().getPosition().x) <= GENERATE_RESOURCE_CENTER_RADIUS &&
                           Math.abs(y - map.getMainCharacter().getPosition().y) <= GENERATE_RESOURCE_CENTER_RADIUS) {
                            continue;
                        }
                        SplittableRandom random = new SplittableRandom(position.getRandomSeed(map.getSeed(),
                                                                                              GENERATE_RESOURCE_VARIATION));
                        double randomValue = random.nextDouble();
                        if(randomValue < probability) {
                            double threshold = DoubleMapper.map(randomValue, 0, probability, -1, 1);
                            map.addResource(position, threshold, random);
                        }
                    }
                }
            }
        }
    }
    
    private void generateFinalBoss(GameMap map) {
        Vector2 bossPosition = getBossPosition(map);
        map.setBossPosition(bossPosition);
        
        Set<Vector2C> chunksToGenerate = getBossRoadChunks(map);
        generateSelectedChunks(map, chunksToGenerate);
        
        clearPosition(map,
                      bossPosition,
                      BOSS_CLEAR_RADIUS,
                      BOSS_CLEAR_CIRCLE_THRESHOLD,
                      BOSS_VARIATION,
                      false, chunksToGenerate);
        
        generateRoadToBoss(map, chunksToGenerate);
        
        EnemyPattern[] enemyPatterns = settingsImporter.importBossPatterns();
        SplittableRandom random = new SplittableRandom(Objects.hash(map.getSeed(), BOSS_VARIATION));
        EnemyPattern randomPattern = enemyPatterns[random.nextInt(enemyPatterns.length)];
        Map<WeaponType, List<WeaponSetting>> weapons = settingsImporter.importWeaponsByTypeAndThreshold(1);
        // Splitting boss from a normal enemy allow difference it on the printMap
        map.addEnemy(bossPosition, new Boss(bossPosition, random, randomPattern, weapons));
    }
    
    private static Vector2 getBossPosition(GameMap map) {
        SplittableRandom random = new SplittableRandom(Objects.hash(map.getSeed(), BOSS_VARIATION));
        int randomDirection = random.nextInt(4);
        Vector2 direction = switch (randomDirection) {
            case 0 -> Vector2.UP;
            case 1 -> Vector2.DOWN;
            case 2 -> Vector2.LEFT;
            case 3 -> Vector2.RIGHT;
            default -> throw new IllegalStateException("Unexpected value: " + randomDirection);
        };
        
        int center = map.getSize() / 2;
        int distance = map.getSize() / 2 - BOSS_BORDER_OFFSET;
        return new Vector2(center + distance * direction.x,
                           center + distance * direction.y);
    }
    
    private static Set<Vector2C> getBossRoadChunks(GameMap map) {
        Set<Vector2C> chunksToGenerate = new HashSet<>();
        Vector2C bossChunk = Chunk.getChunkPosition(map.getBossPosition());
        Vector2C playerChunk = Chunk.getChunkPosition(map.getMainCharacter().getPosition());
        double distance = Vector2C.getDistance(bossChunk, playerChunk);
        Vector2C direction = bossChunk.subtract(playerChunk);
        direction.normalize();
        Vector2C position = playerChunk;
        for (int i = 0; i <= distance; i++) {
            chunksToGenerate.add(position);
            for (int j = -BOSS_PATHFINDING_GENERATE_CHUNKS_WIDTH; j < BOSS_PATHFINDING_GENERATE_CHUNKS_WIDTH * 2; j++) {
                if(j == 0) continue;
                chunksToGenerate.add(position.getLeft(direction, j));
                chunksToGenerate.add(position.getRight(direction, j));
            }
            position = position.add(direction);
        }
        return chunksToGenerate;
    }
    
    private void generateRoadToBoss(GameMap map, Set<Vector2C> chunksToGenerate) {
        Queue<Vector2> path = aStarPathfinder.generatePath(map.getMainCharacter().getPosition(),
                                                           map.getBossPosition(),
                                                           map.getCostMap(chunksToGenerate,
                                                                          false));
        if(path == null) throw new RuntimeException("Fail creating path to boss");
        
        Vector2 direction = Vector2.UP;
        while (!path.isEmpty()) {
            Vector2 position = path.poll();
            Vector2 nextPosition = path.peek();
            if(nextPosition != null) direction = position.subtract(nextPosition);
            direction.normalize();
            
            map.setCell(position, CellType.PATH);
            
            //noinspection NonStrictComparisonCanBeEquality
            for (int i = 1; i <= BOSS_PATH_EXTRA_WIDTH; i++) {
                // Set left and right cells to create a path
                Vector2 left = position.getLeft(direction, i);
                Vector2 right = position.getRight(direction, i);
                if(!left.equals(map.getBossPosition())) map.setCell(left, CellType.PATH);
                if(!right.equals(map.getBossPosition())) map.setCell(right, CellType.PATH);
            }
        }
    }
    
    
    private void generateEnemies(GameMap map, Set<Vector2C> chunksToGenerate) {
        Vector2 center = new Vector2(map.getSize() / 2, map.getSize() / 2);
        
        for (Vector2C chunk : chunksToGenerate) {
            for (int x = chunk.getRealX(); x < chunk.getEndX(); x++) {
                for (int y = chunk.getRealY(); y < chunk.getEndY(); y++) {
                    // Don't spawn enemies on the border
                    if(x < BORDER_OFFSET || y < BORDER_OFFSET ||
                       x > map.getSize() - BORDER_OFFSET || y > map.getSize() - BORDER_OFFSET) {
                        continue;
                    }
                    
                    Vector2 position = new Vector2(x, y);
                    
                    // Don't spawn enemies on obstacles or resources
                    if(!CellType.isWalkable(map.getCell(position).getType()) || map.getResource(position) != null) {
                        continue;
                    }
                    
                    // Don't spawn enemies on the center
                    if(Math.abs(x - center.x) <= GENERATE_ENEMY_CENTER_RADIUS &&
                       Math.abs(y - center.y) <= GENERATE_ENEMY_CENTER_RADIUS) {
                        continue;
                    }
                    
                    // Don't spawn enemies on the boss position
                    if(Math.abs(x - map.getBossPosition().x) <= GENERATE_ENEMY_BOSS_RADIUS &&
                       Math.abs(y - map.getBossPosition().y) <= GENERATE_ENEMY_BOSS_RADIUS) {
                        continue;
                    }
                    
                    double distance = Vector2.getDistance(position, center);
                    double maxDistance = (map.getSize() / 2.0) - BORDER_OFFSET;
                    double normalizedDistance = Math.clamp(distance / maxDistance, 0, 1);
                    
                    // Increase enemy spawn chance based on distance from the center
                    double spawnRate = GENERATE_ENEMY_PROBABILITY / 100.0;
                    spawnRate += normalizedDistance * (GENERATE_ENEMY_PROBABILITY_MAX_INCREMENT / 100.0);
                    
                    SplittableRandom random = new SplittableRandom(position.getRandomSeed(map.getSeed(),
                                                                                          GENERATE_ENEMY_VARIATION));
                    if(random.nextDouble() < spawnRate) {
                        EnemyPattern[] enemyPatterns = settingsImporter.importEnemyPatterns(normalizedDistance);
                        EnemyPattern randomPattern = enemyPatterns[random.nextInt(enemyPatterns.length)];
                        Map<WeaponType, List<WeaponSetting>> weapons = settingsImporter.importWeaponsByTypeAndThreshold(
                                normalizedDistance);
                        map.addEnemy(position, new Enemy(position, random, randomPattern, weapons));
                    }
                }
            }
        }
    }
    
    private void clearPosition(GameMap map, Vector2 center, int radius, double threshold, int circleVariation,
                               boolean clearCenter, Set<Vector2C> chunksToGenerate) {
        Map<Vector2, Double> circle = noiseGenerator.getCircleWithNoisyEdge(radius, center,
                                                                            map.getSeed(),
                                                                            circleVariation, chunksToGenerate);
        for (Vector2 circlePosition : circle.keySet()) {
            if(!clearCenter && circlePosition == center) {
                continue;
            }
            if(circle.get(circlePosition) < threshold) {
                map.setCell(circlePosition, CellType.GROUND);
            }
        }
    }
}

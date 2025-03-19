package com.hexadeventure.application.service.game;

import com.hexadeventure.application.exceptions.GameStartedException;
import com.hexadeventure.application.port.in.game.GameUseCase;
import com.hexadeventure.application.port.out.noise.NoiseGenerator;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.model.map.CellType;
import com.hexadeventure.model.map.GameMap;
import com.hexadeventure.model.map.Vector2;
import com.hexadeventure.model.user.User;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
    @Transactional
    public void startGame(String email, long seed, int size) {
        Optional<User> user = userRepository.findByEmail(email);
        assert user.isPresent();
        if(user.get().getMapId() == null) {
            GameMap newMap = generateMap(email, seed, size);
            printMap(newMap);
            gameMapRepository.save(newMap);
            userRepository.updateMapIdByEmail(email, newMap.getId());
        } else {
            throw new GameStartedException();
        }
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
        
        BufferedImage image = new BufferedImage(size * cellSize, size * cellSize,
                                                BufferedImage.TYPE_INT_RGB);
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
                     cellSize / 2, cellSize / 2);
        
        g2d.dispose();
        
        try {
            File outputfile = new File("map.png");
            ImageIO.write(image, "png", outputfile);
            System.out.println(outputfile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println(e);
        }
        
        // Draw cells
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if(map.getCell(x, y).getType() == CellType.OBSTACLE) {
                    System.out.print("O");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }
    
    private GameMap generateMap(String email, long seed, int size) {
        GameMap map = new GameMap(email, seed, size);
        generateCells(email, seed, size, map);
        generatePlayer(map);
        clearCenter(map);
        return map;
    }
    
    private void generateCells(String email, long seed, int size, GameMap map) {
        noiseGenerator.initNoise(email, seed, 0.1,
                                 4, 0.5, 1.5,
                                 NoiseGenerator.FRACTAL_FBM, true,
                                 true);
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
    
    private void clearCenter(GameMap map) {
    
    }
}

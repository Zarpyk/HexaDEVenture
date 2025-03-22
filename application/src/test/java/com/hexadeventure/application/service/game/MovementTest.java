package com.hexadeventure.application.service.game;

import com.hexadeventure.application.exceptions.GameNotStartedException;
import com.hexadeventure.application.port.out.noise.NoiseGenerator;
import com.hexadeventure.application.port.out.pathfinder.AStarPathfinder;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.application.port.out.persistence.UserRepository;
import com.hexadeventure.application.service.common.UserFactory;
import com.hexadeventure.model.map.CellData;
import com.hexadeventure.model.map.CellType;
import com.hexadeventure.model.map.GameMap;
import com.hexadeventure.model.map.Vector2;
import com.hexadeventure.model.user.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import static com.hexadeventure.model.map.CellType.GROUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class MovementTest {
    private static final String TEST_USER_EMAIL = "test@test.com";
    private static final Vector2 START_POSITION = new Vector2(0, 0);
    private static final Vector2 END_POSITION = new Vector2(4, 4);
    
    private static final int EMPTY_MAP_SIZE = 5;
    private static final int EMPTY_MAP_PATH_LENGTH = 9;
    private static final CellType[][] emptyMapTypes = new CellType[][]{
            {GROUND, GROUND, GROUND, GROUND, GROUND},
            {GROUND, GROUND, GROUND, GROUND, GROUND},
            {GROUND, GROUND, GROUND, GROUND, GROUND},
            {GROUND, GROUND, GROUND, GROUND, GROUND},
            {GROUND, GROUND, GROUND, GROUND, GROUND}
    };
    
    private static final String EMPTY_MAP_ID = UUID.randomUUID().toString();
    
    private static final UserRepository userRepository = mock(UserRepository.class);
    private static final GameMapRepository gameMapRepository = mock(GameMapRepository.class);
    private static final NoiseGenerator noiseGenerator = mock(NoiseGenerator.class);
    private static final AStarPathfinder aStarPathfinder = mock(AStarPathfinder.class);
    private static final GameService gameService = new GameService(userRepository, gameMapRepository,
                                                                   noiseGenerator, aStarPathfinder);
    
    @BeforeAll
    public static void beforeAll() {
        CellData[][] cells = new CellData[EMPTY_MAP_SIZE][EMPTY_MAP_SIZE];
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[x].length; y++) {
                cells[x][y] = new CellData(new Vector2(x, y), emptyMapTypes[x][y]);
            }
        }
        GameMap emptyMap = new GameMap(EMPTY_MAP_ID, TEST_USER_EMAIL, 0, cells, null, null);
        emptyMap.initMainCharacter(START_POSITION);
        int[][] emptyMapCost = Arrays.stream(cells)
                                     .map(x -> Arrays.stream(x)
                                                     .mapToInt(y -> CellType.getCost(y.getType(), true))
                                                     .toArray())
                                     .toArray(int[][]::new);
        Queue<Vector2> path = new LinkedList<>();
        path.add(START_POSITION);
        for (int i = 0; i < EMPTY_MAP_PATH_LENGTH - 2; i++) {
            path.add(new Vector2(0, 0));
        }
        path.add(END_POSITION);
        
        when(gameMapRepository.findById(eq(EMPTY_MAP_ID))).thenReturn(java.util.Optional.of(emptyMap));
        when(aStarPathfinder.generatePath(eq(START_POSITION), eq(END_POSITION), eq(emptyMapCost))).thenReturn(path);
    }
    
    @Test
    public void givenNoStartGameUser_whenMove_thenThrowAnException() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(null);
        
        assertThatExceptionOfType(GameNotStartedException.class)
                .isThrownBy(() -> gameService.move(TEST_USER_EMAIL, END_POSITION));
    }
    
    @Test
    public void givenPosition_whenMove_thenMoveToThePosition() {
        User testUser = UserFactory.createTestUser(userRepository);
        testUser.setMapId(EMPTY_MAP_ID);
        when(userRepository.findByEmail(TEST_USER_EMAIL)).thenReturn(java.util.Optional.of(testUser));
        
        MovementResponseDTO move = gameService.move(TEST_USER_EMAIL, END_POSITION);
        
        assertThat(move.actions()).hasSize(EMPTY_MAP_PATH_LENGTH);
        assertThat(move.actions().getFirst().x()).isEqualTo(START_POSITION.x);
        assertThat(move.actions().getFirst().y()).isEqualTo(START_POSITION.y);
        assertThat(move.actions().getLast().x()).isEqualTo(END_POSITION.x);
        assertThat(move.actions().getLast().y()).isEqualTo(END_POSITION.y);
    }
}

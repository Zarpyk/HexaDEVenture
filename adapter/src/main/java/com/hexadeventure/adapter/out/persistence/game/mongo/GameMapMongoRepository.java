package com.hexadeventure.adapter.out.persistence.game.mongo;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.hexadeventure.application.port.out.persistence.GameMapRepository;
import com.hexadeventure.model.map.CellData;
import com.hexadeventure.model.map.GameMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.gridfs.GridFsCriteria.whereFilename;

@ConditionalOnProperty(name = "persistence", havingValue = "mongo")
@Repository
public class GameMapMongoRepository implements GameMapRepository {
    
    private final GameMapMongoSDRepository repo;
    private final GridFsOperations gridFsOperations;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    static {
        // From: https://medium.com/@davenkin_93074/jackson-polymorphism-explained-910cd1619ffc
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                                                                    .allowIfBaseType("com.hexadeventure")
                                                                    .allowIfSubType(CellData[][].class)
                                                                    .allowIfSubType(CellData[].class)
                                                                    .build();
        objectMapper.activateDefaultTyping(ptv,
                                           ObjectMapper.DefaultTyping.NON_FINAL_AND_ENUMS,
                                           JsonTypeInfo.As.PROPERTY);
    }
    
    public GameMapMongoRepository(GameMapMongoSDRepository repo, GridFsOperations gridFsOperations) {
        this.repo = repo;
        this.gridFsOperations = gridFsOperations;
    }
    
    @Override
    public Optional<GameMap> findById(String id) {
        Optional<GameMapMongoEntity> map = repo.findById(id);
        if(map.isEmpty()) return Optional.empty();
        try {
            GridFsResource gridResource = gridFsOperations.getResource(map.get().getGridFileId() + ".json");
            CellData[][] grid = objectMapper.readValue(gridResource.getInputStream(), CellData[][].class);
            return map.map(x -> GameMapMongoMapper.toModel(x, grid));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void save(GameMap newMap) {
        try {
            GameMapMongoEntity mapEntity = GameMapMongoMapper.toEntity(newMap);
            byte[] gridData = objectMapper.writeValueAsBytes(newMap.getGrid());
            
            Optional<GameMapMongoEntity> oldMap = repo.findById(newMap.getId());
            if(oldMap.isPresent() && oldMap.get().getGridFileId() != null) {
                //gridFsOperations.delete(query(whereFilename().is(oldMap.get().getCellsFileId() + ".json")));
                gridFsOperations.store(
                        new ByteArrayInputStream(gridData),
                        oldMap.get().getGridFileId() + ".json",
                        "application/json"
                );
                mapEntity.setGridFileId(oldMap.get().getGridFileId());
            } else {
                String fileId = UUID.randomUUID().toString();
                gridFsOperations.store(
                        new ByteArrayInputStream(gridData),
                        fileId + ".json",
                        "application/json"
                );
                mapEntity.setGridFileId(fileId);
            }
            repo.save(mapEntity);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void deleteById(String mapId) {
        Optional<GameMapMongoEntity> map = repo.findById(mapId);
        if(map.isPresent() && map.get().getGridFileId() != null) {
            gridFsOperations.delete(query(whereFilename().is(map.get().getGridFileId() + ".json")));
        }
        repo.deleteById(mapId);
    }
}

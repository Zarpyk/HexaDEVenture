package com.hexadeventure.adapter.out.persistence.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.mongodb.gridfs.GridFsOperations;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

public class GridFs {
    private final ObjectMapper objectMapper;
    private final GridFsOperations gridFsOperations;
    
    public GridFs(ObjectMapper objectMapper, GridFsOperations gridFsOperations) {
        this.objectMapper = objectMapper;
        this.gridFsOperations = gridFsOperations;
    }
    
    public String storeData(Object data, String existingFileId) throws JsonProcessingException {
        byte[] jsonData = objectMapper.writeValueAsBytes(data);
        String fileId = (existingFileId != null) ? existingFileId : UUID.randomUUID().toString();
        
        gridFsOperations.store(
                new ByteArrayInputStream(jsonData),
                fileId + ".json",
                "application/json"
        );
        
        return fileId;
    }
    
    public <T> T readData(String fileId, Class<T> clazz) throws IOException {
        // From: https://stackoverflow.com/a/2525152/11451105
        return objectMapper.readValue(gridFsOperations.getResource(fileId + ".json").getInputStream(), clazz);
    }
    
    public <T> T readData(String fileId, TypeReference<T> ref) throws IOException {
        // From: https://stackoverflow.com/a/2525152/11451105
        return objectMapper.readValue(gridFsOperations.getResource(fileId + ".json").getInputStream(), ref);
    }
}

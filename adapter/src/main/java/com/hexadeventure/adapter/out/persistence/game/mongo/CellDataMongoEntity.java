package com.hexadeventure.adapter.out.persistence.game.mongo;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "CellDatas")
@Getter
@Setter
public class CellDataMongoEntity {
    @MongoId
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String data;
}

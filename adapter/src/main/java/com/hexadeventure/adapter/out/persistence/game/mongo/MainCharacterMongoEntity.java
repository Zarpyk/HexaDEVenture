package com.hexadeventure.adapter.out.persistence.game.mongo;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "MainCharacters")
@Getter
@Setter
public class MainCharacterMongoEntity {
    @MongoId
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private int x;
    private int y;
}

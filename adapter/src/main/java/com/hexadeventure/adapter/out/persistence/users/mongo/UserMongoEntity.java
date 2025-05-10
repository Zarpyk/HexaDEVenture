package com.hexadeventure.adapter.out.persistence.users.mongo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Document(collection = "Users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserMongoEntity {
    @MongoId
    private String id;
    private String email;
    private String username;
    private String password;
    private String mapId;
    private int wins;
    private int playedGames;
    private int playedTime;
    private LocalDateTime currentGameStartTime;
    private int travelledDistance;
    private int collectedResources;
}

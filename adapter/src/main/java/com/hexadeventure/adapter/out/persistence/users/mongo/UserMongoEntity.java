package com.hexadeventure.adapter.out.persistence.users.mongo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "Users")
@Getter
@Setter
public class UserMongoEntity {
    @MongoId
    private String id;
    private String email;
    private String username;
    private String password;
    private String mapId;
}

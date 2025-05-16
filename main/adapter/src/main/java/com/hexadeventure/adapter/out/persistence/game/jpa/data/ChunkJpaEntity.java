package com.hexadeventure.adapter.out.persistence.game.jpa.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

@Entity
@Table(name = "chunk", indexes = {
        @Index(name = "chunk_map_id_index", columnList = "mapId, x, y", unique = true)
})
@Getter
@Setter
public class ChunkJpaEntity {
    @Id
    private String id;
    private String mapId;
    private int x;
    private int y;
    
    @Column(columnDefinition = "json")
    // From: https://stackoverflow.com/a/77150431/11451105
    // Probably only for PostgreSQL
    @ColumnTransformer(write = "?::json")
    private String cellsJson;
    
    @Column(columnDefinition = "json")
    @ColumnTransformer(write = "?::json")
    private String resourcesJson;
    
    @Column(columnDefinition = "json")
    @ColumnTransformer(write = "?::json")
    private String enemiesJson;
}

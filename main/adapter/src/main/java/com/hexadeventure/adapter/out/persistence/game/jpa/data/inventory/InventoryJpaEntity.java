package com.hexadeventure.adapter.out.persistence.game.jpa.data.inventory;

import com.hexadeventure.adapter.out.persistence.game.jpa.data.GameMapJpaEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

@Entity
@Table(name = "inventory")
@Getter
@Setter
public class InventoryJpaEntity {
    @Id
    private String id;
    
    @Column(columnDefinition = "json")
    // From: https://stackoverflow.com/a/77150431/11451105
    // Probably only for PostgreSQL
    @ColumnTransformer(write = "?::json")
    private String itemsJson;
    
    @Column(columnDefinition = "json")
    // From: https://stackoverflow.com/a/77150431/11451105
    // Probably only for PostgreSQL
    @ColumnTransformer(write = "?::json")
    private String charactersJson;
    
    @OneToOne
    @JoinColumn(name = "game_map_id")
    private GameMapJpaEntity gameMap;
}

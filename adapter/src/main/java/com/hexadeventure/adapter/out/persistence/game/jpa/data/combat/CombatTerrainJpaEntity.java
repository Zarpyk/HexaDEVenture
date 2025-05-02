package com.hexadeventure.adapter.out.persistence.game.jpa.data.combat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

@Entity
@Table(name = "combat_terrain")
@Getter
@Setter
public class CombatTerrainJpaEntity {
    @Id
    private String id;
    
    private int rowSize;
    private int columnSize;
    
    @Column(columnDefinition = "json")
    // From: https://stackoverflow.com/a/77150431/11451105
    // Probably only for PostgreSQL
    @ColumnTransformer(write = "?::json")
    private String playerTerrain;
    
    @Column(columnDefinition = "json")
    // From: https://stackoverflow.com/a/77150431/11451105
    // Probably only for PostgreSQL
    @ColumnTransformer(write = "?::json")
    private String enemyTerrain;
}

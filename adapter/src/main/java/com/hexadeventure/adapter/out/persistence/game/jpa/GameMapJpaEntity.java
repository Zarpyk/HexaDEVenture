package com.hexadeventure.adapter.out.persistence.game.jpa;

import com.hexadeventure.adapter.out.persistence.game.jpa.data.CellDataJpaEntity;
import com.hexadeventure.adapter.out.persistence.game.jpa.data.EnemyJpaEntity;
import com.hexadeventure.adapter.out.persistence.game.jpa.data.ResourceJpaEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "game_map")
@Getter
@Setter
public class GameMapJpaEntity {
    @Id
    private String id;
    private String userId;
    private long seed;
    private int gridSize;
    
    @OneToMany(mappedBy = "map", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<CellDataJpaEntity> grid;
    
    @OneToMany(mappedBy = "map", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<ResourceJpaEntity> resources;
    
    @OneToMany(mappedBy = "map", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<EnemyJpaEntity> enemies;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "main_character_id")
    private MainCharacterJpaEntity mainCharacter;
}

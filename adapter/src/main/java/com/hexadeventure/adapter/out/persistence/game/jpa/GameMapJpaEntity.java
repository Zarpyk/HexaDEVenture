package com.hexadeventure.adapter.out.persistence.game.jpa;

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
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "main_character_id")
    private MainCharacterJpaEntity mainCharacter;
}

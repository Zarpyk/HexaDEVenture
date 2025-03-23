package com.hexadeventure.adapter.out.persistence.game.jpa.data;

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
    private int mapSize;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "main_character_id")
    private MainCharacterJpaEntity mainCharacter;
}

package com.hexadeventure.adapter.out.persistence.game.jpa.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "main_character")
@Getter
@Setter
public class MainCharacterJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private int x;
    private int y;
    
    @OneToOne
    @JoinColumn(name = "game_map_id")
    private GameMapJpaEntity map;
}

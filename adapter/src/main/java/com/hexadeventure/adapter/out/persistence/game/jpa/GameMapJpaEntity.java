package com.hexadeventure.adapter.out.persistence.game.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
    @OneToMany(mappedBy = "map")
    private List<CellDataJpaEntity> grid;
}

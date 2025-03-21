package com.hexadeventure.adapter.out.persistence.game.jpa.data;

import com.hexadeventure.adapter.out.persistence.game.jpa.GameMapJpaEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cell_data")
@Getter
@Setter
public class CellDataJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    private int x;
    private int y;
    private int type;
    
    @ManyToOne
    @JoinColumn(name = "map_id")
    private GameMapJpaEntity map;
}

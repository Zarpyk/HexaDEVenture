package com.hexadeventure.adapter.out.persistence.game.jpa;

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
    @Column(columnDefinition = "json")
    private String data;
    @ManyToOne
    @JoinColumn(name = "map_id")
    private GameMapJpaEntity map;
}

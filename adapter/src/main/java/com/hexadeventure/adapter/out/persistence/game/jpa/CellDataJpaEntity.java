package com.hexadeventure.adapter.out.persistence.game.jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

@Entity
@Table(name = "cell_data")
@Getter
@Setter
public class CellDataJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(columnDefinition = "json")
    // From: https://stackoverflow.com/a/77150431/11451105
    // Probably only for PostgreSQL
    @ColumnTransformer(write = "?::json")
    private String data;
    
    @ManyToOne
    @JoinColumn(name = "map_id")
    private GameMapJpaEntity map;
}

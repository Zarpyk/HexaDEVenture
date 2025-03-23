package com.hexadeventure.adapter.out.persistence.game.jpa.data;

import com.hexadeventure.adapter.out.persistence.game.jpa.data.chunk.CellDataJpaEntity;
import com.hexadeventure.adapter.out.persistence.game.jpa.data.chunk.EnemyJpaEntity;
import com.hexadeventure.adapter.out.persistence.game.jpa.data.chunk.ResourceJpaEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "chunk", indexes = {
        @Index(name = "chunk_map_id_index", columnList = "mapId, x, y", unique = true)
})
@Getter
@Setter
public class ChunkJpaEntity {
    @Id
    private String id;
    private String mapId;
    private int x;
    private int y;
    
    @OneToMany(mappedBy = "chunk", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<CellDataJpaEntity> grid;
    
    @OneToMany(mappedBy = "chunk", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<ResourceJpaEntity> resources;
    
    @OneToMany(mappedBy = "chunk", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<EnemyJpaEntity> enemies;
}

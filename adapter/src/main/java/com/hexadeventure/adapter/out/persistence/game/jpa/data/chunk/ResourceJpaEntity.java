package com.hexadeventure.adapter.out.persistence.game.jpa.data.chunk;

import com.hexadeventure.adapter.out.persistence.game.jpa.data.ChunkJpaEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "resources")
@Getter
@Setter
public class ResourceJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    private int x;
    private int y;
    
    private int type;
    private int count;
    
    @ManyToOne
    @JoinColumn(name = "chunk_id")
    private ChunkJpaEntity chunk;
}

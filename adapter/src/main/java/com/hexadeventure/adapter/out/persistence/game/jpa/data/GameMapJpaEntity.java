package com.hexadeventure.adapter.out.persistence.game.jpa.data;

import com.hexadeventure.adapter.out.persistence.game.jpa.data.combat.CombatTerrainJpaEntity;
import com.hexadeventure.adapter.out.persistence.game.jpa.data.inventory.InventoryJpaEntity;
import com.hexadeventure.model.map.Vector2;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "inventory_id")
    private InventoryJpaEntity inventory;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "combat_terrain_id")
    private CombatTerrainJpaEntity combatTerrain;
    
    private int bossPositionX;
    private int bossPositionY;
    private boolean isInCombat;
    private boolean isBossBattle;
}

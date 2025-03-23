package com.hexadeventure.adapter.out.persistence.game.jpa.data.chunk;

import com.hexadeventure.model.map.Vector2;
import com.hexadeventure.model.map.resources.Resource;
import com.hexadeventure.model.map.resources.ResourceType;

public class ResourceJpaMapper {
    public static ResourceJpaEntity toEntity(Resource model) {
        ResourceJpaEntity entity = new ResourceJpaEntity();
        entity.setX(model.getPosition().x);
        entity.setY(model.getPosition().y);
        entity.setType(model.getType().ordinal());
        return entity;
    }
    
    public static Resource toModel(ResourceJpaEntity entity) {
        return new Resource(new Vector2(entity.getX(), entity.getY()),
                            ResourceType.values()[entity.getType()],
                            entity.getCount());
    }
}

package com.hexadeventure.model.map;

public enum CellType {
    GROUND,
    GROUND2,
    PATH,
    WALL;
    
    public static boolean isWalkable(CellType cellType) {
        return cellType != WALL;
    }
    
    public static int getCost(CellType cellType, boolean onlyWalkable) {
        return switch (cellType) {
            case PATH -> 1;
            case GROUND -> 5;
            case GROUND2 -> 10;
            case WALL -> onlyWalkable ? -1 : 200;
        };
    }
}

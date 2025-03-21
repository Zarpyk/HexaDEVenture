package com.hexadeventure.model.map;

public enum CellType {
    GROUND,
    PATH,
    WALL;
    
    public static boolean isWalkable(CellType cellType) {
        return cellType != WALL;
    }
    
    public static int getCost(CellType cellType) {
        return switch (cellType) {
            case PATH -> 1;
            case GROUND -> 2;
            case WALL -> 200;
        };
    }
}

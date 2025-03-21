package com.hexadeventure.model.map;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
public class Vector2 {
    public static final Vector2 ZERO = new Vector2(0, 0);
    public static final Vector2 ONE = new Vector2(1, 1);
    public static final Vector2 UP = new Vector2(0, 1);
    public static final Vector2 DOWN = new Vector2(0, -1);
    public static final Vector2 LEFT = new Vector2(-1, 0);
    public static final Vector2 RIGHT = new Vector2(1, 0);
    
    public int x;
    public int y;
    
    public Vector2 getLeft(Vector2 direction, int distance) {
        // If the direction is (1, 0), the left is (0, 1)
        // If the direction is (0, 1), the left is (-1, 0)
        // If the direction is (-1, 0), the left is (0, -1)
        // If the direction is (0, -1), the left is (1, 0)
        return new Vector2(x - direction.y * distance, y + direction.x * distance);
    }
    
    public Vector2 getRight(Vector2 direction, int distance) {
        // If the direction is (1, 0), the right is (0, -1)
        // If the direction is (0, 1), the right is (1, 0)
        // If the direction is (-1, 0), the right is (0, 1)
        // If the direction is (0, -1), the right is (-1, 0)
        return new Vector2(x + direction.y * distance, y - direction.x * distance);
    }
    
    public Vector2 add(Vector2 vector2) {
        return new Vector2(x + vector2.x, y + vector2.y);
    }
    
    public Vector2 subtract(Vector2 vector2) {
        return new Vector2(x - vector2.x, y - vector2.y);
    }
    
    public Vector2 multiply(int scalar) {
        return new Vector2(x * scalar, y * scalar);
    }
    
    public Vector2 divide(int scalar) {
        return new Vector2(x / scalar, y / scalar);
    }
    
    public void normalize() {
        int length = (int) Math.sqrt(x * x + y * y);
        x /= length;
        y /= length;
    }
    
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Vector2 vector2)) return false;
        return x == vector2.x && y == vector2.y;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
    
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}

package com.hexadeventure.model.map;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * Chunk 2D vector
 */
@AllArgsConstructor
@NoArgsConstructor
public class Vector2C {
    public static final Vector2C ZERO = new Vector2C(0, 0);
    public static final Vector2C ONE = new Vector2C(1, 1);
    public static final Vector2C UP = new Vector2C(0, 1);
    public static final Vector2C DOWN = new Vector2C(0, -1);
    public static final Vector2C LEFT = new Vector2C(-1, 0);
    public static final Vector2C RIGHT = new Vector2C(1, 0);
    
    public int x;
    public int y;
    
    public int getRealX() {
        return x * Chunk.SIZE;
    }
    
    public int getRealY() {
        return y * Chunk.SIZE;
    }
    
    public int getEndX() {
        return getRealX() + Chunk.SIZE;
    }
    
    public int getEndY() {
        return getRealY() + Chunk.SIZE;
    }
    
    public Vector2C getLeft(Vector2C direction, int distance) {
        // If the direction is (1, 0), the left is (0, 1)
        // If the direction is (0, 1), the left is (-1, 0)
        // If the direction is (-1, 0), the left is (0, -1)
        // If the direction is (0, -1), the left is (1, 0)
        return new Vector2C(x - direction.y * distance, y + direction.x * distance);
    }
    
    public Vector2C getRight(Vector2C direction, int distance) {
        // If the direction is (1, 0), the right is (0, -1)
        // If the direction is (0, 1), the right is (1, 0)
        // If the direction is (-1, 0), the right is (0, 1)
        // If the direction is (0, -1), the right is (-1, 0)
        return new Vector2C(x + direction.y * distance, y - direction.x * distance);
    }
    
    public Vector2C add(Vector2C vector2) {
        return new Vector2C(x + vector2.x, y + vector2.y);
    }
    
    public Vector2C add(int x, int y) {
        return new Vector2C(this.x + x, this.y + y);
    }
    
    public Vector2C subtract(Vector2C vector2) {
        return new Vector2C(x - vector2.x, y - vector2.y);
    }
    
    public Vector2C subtract(int x, int y) {
        return new Vector2C(this.x - x, this.y - y);
    }
    
    public Vector2C multiply(int scalar) {
        return new Vector2C(x * scalar, y * scalar);
    }
    
    public Vector2C divide(int scalar) {
        return new Vector2C(x / scalar, y / scalar);
    }
    
    public void normalize() {
        int length = (int) Math.sqrt(x * x + y * y);
        x /= length;
        y /= length;
    }
    
    public static double getDistance(Vector2C a, Vector2C b) {
        return Math.sqrt((a.x - b.x) * (a.x - b.x) +
                         (a.y - b.y) * (a.y - b.y));
    }
    
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Vector2C vector2)) return false;
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

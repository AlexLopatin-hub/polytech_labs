package org.example;

public record Node(int id, double x, double y) {
    public double distanceTo(Node other) {
        return Math.hypot(x - other.x, y - other.y);
    }
}

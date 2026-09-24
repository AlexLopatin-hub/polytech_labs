package org.example.models.agents;

import java.util.Random;

public enum Direction {
    UP(0, 1), DOWN(0, -1), LEFT(-1, 0), RIGHT(1, 0);

    public final int dx, dy;
    Direction(int dx, int dy) { this.dx = dx; this.dy = dy; }

    public static Direction random() {
        Random random = new Random();
        return Direction.values()[random.nextInt(Direction.values().length)];
    }

    public static Direction oppositeOf(Direction dir) {
        switch (dir) {
            case UP -> { return DOWN; }
            case DOWN -> { return UP; }
            case RIGHT -> { return LEFT; }
            case LEFT -> { return RIGHT; }
        }
        return null;
    }
}

package org.example.models.environment;

public record Cell(CellContent content) {
    public static int getManhattanDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(y2 - y1) + Math.abs(x2 - x1);
    }
}

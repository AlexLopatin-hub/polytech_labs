package org.example;

import java.util.ArrayList;
import java.util.List;

public record Position(int row, int col) {
    public Position move(Direction d) { return new Position(row + d.dr, col + d.dc); }
    public boolean inside(int size) { return row >= 0 && col >= 0 && row < size && col < size; }
    public List<Position> neighbors(int size) {
        List<Position> result = new ArrayList<>();

        for (Direction d : new Direction[]{Direction.EAST, Direction.NORTH, Direction.WEST, Direction.SOUTH}) {
            Position p = move(d);
            if (p.inside(size)) result.add(p);
        }
        return result;
    }
    @Override public String toString() { return "(" + (row + 1) + ", " + (col + 1) + ")"; }
}

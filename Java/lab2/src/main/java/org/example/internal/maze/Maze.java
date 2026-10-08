package org.example.internal.maze;

import java.util.*;

import org.example.internal.agent.State;
import org.example.internal.models.CellType;

public class Maze {
    public final int rows, cols;          // size in rooms
    public final int height, width;       // size with walls
    private CellType[][] grid;
    public int startR, startC, cheeseR, cheeseC;

    public Maze(int rows, int cols) {
        if (rows < 2 || cols < 2) throw new IllegalArgumentException("Minimum size is 2x2");
        this.rows = rows;
        this.cols = cols;
        this.height = 2 * rows + 1;
        this.width = 2 * cols + 1;
        this.grid = new CellType[height][width];
        for (CellType[] row : grid) Arrays.fill(row, CellType.WALL);
    }

    CellType get(int r, int c) { return grid[r][c]; }

    void set(int r, int c, CellType t) { grid[r][c] = t; }

    public boolean inside(int r, int c) { return r >= 0 && c >= 0 && r < height && c < width; }

    public boolean isFree(int r, int c) { return inside(r, c) && grid[r][c] != CellType.WALL; }

    public double reward(int r, int c, double x, double y, double z, double e) {
        return switch (grid[r][c]) {
            case WATER -> x;
            case SHOCK -> y;
            case CHEESE -> z;
            case EMPTY -> e;
            default -> 0;
        };
    }

    public void setAgentPosition(State oldState, State newState) {
        set(oldState.r, oldState.c, CellType.EMPTY);
        set(newState.r, newState.c, CellType.MOUSE);
    }

    public CellType[][] getGrid() {
        CellType[][] result = new CellType[grid.length][];
        for (int i = 0; i < grid.length; i++) {
            if (grid[i] != null) {
                result[i] = grid[i].clone();
            }
        }
        return result;
    }

    public void setGrid(CellType[][] newGrid) {
        CellType[][] result = new CellType[newGrid.length][];
        for (int i = 0; i < newGrid.length; i++) {
            if (newGrid[i] != null) {
                result[i] = newGrid[i].clone(); // Копируем каждую строку
            }
        }

        this.grid = result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (CellType[] row : grid) {
            for (CellType t : row) sb.append(t.symbol).append(t.symbol == '#' ? "#" : " ");
            sb.append('\n');
        }
        return sb.toString();
    }
}


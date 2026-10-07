package org.example.internal.maze;

import org.example.internal.models.CellType;

import java.util.*;

public class MazeGenerator {
    private final Random rnd;

    public MazeGenerator(long seed) { this.rnd = new Random(seed); }
    public MazeGenerator() { this(System.nanoTime()); }

    public Maze generate(int rows, int cols, int waterCount, int shockCount) {
        Maze maze = new Maze(rows, cols);
        carvePassages(maze);

        // Mouse in the bottom-right corner, cheese in the upper-left
        maze.startR = maze.height - 2;
        maze.startC = maze.width - 2;
        maze.cheeseR = 1;
        maze.cheeseC = 1;
        maze.set(maze.startR, maze.startC, CellType.MOUSE);
        maze.set(maze.cheeseR, maze.cheeseC, CellType.CHEESE);

        scatter(maze, CellType.WATER, waterCount);
        scatter(maze, CellType.SHOCK, shockCount);
        return maze;
    }

    private void carvePassages(Maze maze) {
        int[][] dirs = {{-2, 0}, {2, 0}, {0, -2}, {0, 2}};
        Deque<int[]> stack = new ArrayDeque<>();

        int r = 1, c = 1;
        maze.set(r, c, CellType.EMPTY);
        stack.push(new int[]{r, c});

        while (!stack.isEmpty()) {
            int[] cur = stack.peek();
            List<int[]> neighbours = new ArrayList<>();
            for (int[] d : dirs) {
                int nr = cur[0] + d[0], nc = cur[1] + d[1];
                if (maze.inside(nr, nc) && maze.get(nr, nc) == CellType.WALL
                        && nr > 0 && nc > 0 && nr < maze.height - 1 && nc < maze.width - 1) {
                    neighbours.add(new int[]{nr, nc, d[0] / 2, d[1] / 2});
                }
            }
            if (neighbours.isEmpty()) {
                stack.pop();
            } else {
                int[] n = neighbours.get(rnd.nextInt(neighbours.size()));
                maze.set(cur[0] + n[2], cur[1] + n[3], CellType.EMPTY);
                maze.set(n[0], n[1], CellType.EMPTY);
                stack.push(new int[]{n[0], n[1]});
            }
        }
    }


    private void scatter(Maze maze, CellType type, int count) {
        List<int[]> free = new ArrayList<>();
        for (int r = 0; r < maze.height; r++)
            for (int c = 0; c < maze.width; c++)
                if (maze.get(r, c) == CellType.EMPTY) free.add(new int[]{r, c});
        Collections.shuffle(free, rnd);
        for (int i = 0; i < Math.min(count, free.size()); i++) {
            maze.set(free.get(i)[0], free.get(i)[1], type);
        }
    }
}


package org.example.internal;

import org.example.internal.agent.Action;
import org.example.internal.agent.Agent;
import org.example.internal.agent.Policy;
import org.example.internal.agent.State;
import org.example.internal.maze.Maze;
import org.example.internal.maze.MazeGenerator;
import org.example.internal.models.CellType;

public class Environment {
    public Environment(Config envConfig) {
        this.rows = envConfig.rows();
        this.cols = envConfig.cols();
        this.waterCount = envConfig.waterCount();
        this.shockCount = envConfig.shockCount();

        this.emptyReward = envConfig.emptyReward();
        this.waterReward = envConfig.waterReward();
        this.shockReward = envConfig.shockReward();
        this.cheeseReward = envConfig.cheeseReward();

        this.maze = new MazeGenerator().generate(
                this.rows,
                this.cols,
                this.waterCount,
                this.shockCount
        );

        this.agentPolicy = new Policy(this.maze.height, this.maze.width);

        this.agent = new Agent(
                new State(this.maze.startR, this.maze.startC),
                this.agentPolicy
        );

        this.visited = new boolean[this.maze.height][this.maze.width];
        this.visited[this.maze.startR][this.maze.startC] = true;

        this.mazeGrid = this.maze.getGrid();
    }

    private final int rows;
    private final int cols;
    private final int waterCount;
    private final int shockCount;

    private final double emptyReward;
    private final double waterReward;
    private final double shockReward;
    private final double cheeseReward;

    private CellType[][] mazeGrid;

    public Maze maze;
    public Policy agentPolicy;
    public Agent agent;
    private boolean[][] visited;
    private long trainingAges;

    public synchronized void runAge(int samplesLimit) {
        for (int i = 0; i < samplesLimit; i++) {
            this.step();
        }

        this.trainingAges++;
        this.reset();
    }
    public synchronized void step() {
        boolean[] availableActions = availableActions();
        if (numOfAvailableActions(availableActions) == 1) {
            reset();
            return;
        }

        Action action = this.agent.selectAction(availableActions);

        State oldState = new State(this.agent.getState().r, this.agent.getState().c);
        State newState = calculateNewState(oldState, action);

        double reward = maze.reward(
                newState.r,
                newState.c,
                this.waterReward,
                this.shockReward,
                this.cheeseReward,
                this.emptyReward
        );

        this.agent.setState(newState);
        this.maze.setAgentPosition(oldState, newState);
        this.visited[newState.r][newState.c] = true;

        agent.updatePolicy(oldState, newState, action.ordinal(), reward);
    }

    private State calculateNewState(State state, Action action) {
        return new State(state.r + action.dr, state.c + action.dc);
    }

    public synchronized void reset() {
        State oldState = new State(this.agent.getState().r, this.agent.getState().c);
        State startState = new State(this.maze.startR, this.maze.startC);

        this.agent.setState(startState);
        this.maze.setAgentPosition(oldState, startState);

        this.visited = new boolean[this.maze.height][this.maze.width];
        this.visited[startState.r][startState.c] = true;

        this.maze.setGrid(mazeGrid.clone());
    }

    public synchronized void learn(int ages) {
        if (ages < 0) return;

        for (int i = 0; i < ages; i++) {
            this.runAge(2000);
        }

        this.reset();
    }

    public synchronized void resetPolicy() {
        this.agentPolicy = new Policy(this.maze.height, this.maze.width);
        this.agentPolicy.reset();
        this.trainingAges = 0;
        this.maze.setGrid(mazeGrid.clone());
        this.agent = new Agent(
                new State(this.maze.startR, this.maze.startC),
                this.agentPolicy
        );

        this.visited = new boolean[this.maze.height][this.maze.width];
        this.visited[this.maze.startR][this.maze.startC] = true;
    }

    public synchronized void generateNewMaze() {
        this.maze = new MazeGenerator().generate(
                this.rows,
                this.cols,
                this.waterCount,
                this.shockCount
        );
        this.mazeGrid = this.maze.getGrid();
        this.agent = new Agent(
                new State(this.maze.startR, this.maze.startC),
                this.agentPolicy
        );

        this.visited = new boolean[this.maze.height][this.maze.width];
        this.visited[this.maze.startR][this.maze.startC] = true;
    }

    public synchronized CellType[][] getMazeSnapshot() {
        return this.maze.getGrid();
    }

    public synchronized boolean[][] getVisitedSnapshot() {
        boolean[][] result = new boolean[this.visited.length][];
        for (int i = 0; i < this.visited.length; i++) {
            result[i] = this.visited[i].clone();
        }
        return result;
    }

    public synchronized int getAgentRow() {
        return this.agent.getState().r;
    }

    public synchronized int getAgentCol() {
        return this.agent.getState().c;
    }

    public synchronized int getMazeHeight() {
        return this.maze.height;
    }

    public synchronized int getMazeWidth() {
        return this.maze.width;
    }

    public synchronized long getTrainingAges() {
        return this.trainingAges;
    }

    private boolean[] availableActions() {
        State state = this.agent.getState();
        boolean[] available = new boolean[Action.values().length];
        for (Action action : Action.values()) {
            int row = state.r + action.dr;
            int col = state.c + action.dc;
            available[action.ordinal()] = maze.isFree(row, col); // && !visited[row][col];
        }
        return available;
    }

    private boolean hasAvailableAction(boolean[] availableActions) {
        for (boolean available : availableActions) {
            if (available) {
                return true;
            }
        }
        return false;
    }

    private int numOfAvailableActions(boolean[] availableActions) {
        int counter = 0;
        for (boolean available : availableActions) {
            if (available) {
                counter++;
            }
        }
        return counter;
    }
}

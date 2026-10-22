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

        this.discountFactor = envConfig.discountFactor();
        this.learningRate = envConfig.learningRate();

        this.maxReward = Double.NEGATIVE_INFINITY;
        this.sumReward = 0;

        this.maze = new MazeGenerator().generate(
                this.rows,
                this.cols,
                this.waterCount,
                this.shockCount
        );

        this.agentPolicy = new Policy(
                this.maze.height,
                this.maze.width,
                this.discountFactor,
                this.learningRate
        );

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

    private final double discountFactor;
    private final double learningRate;

    public double sumReward;
    public double lastReward;
    public double maxReward;

    private CellType[][] mazeGrid;

    public Maze maze;
    public Policy agentPolicy;
    public Agent agent;
    private boolean[][] visited;
    private long trainingAges;

    public synchronized void runAge(int samplesLimit) {
        this.lastReward = 0;

        for (int i = 0; i < samplesLimit; i++) {
            boolean ok = this.step();
            if (!ok) {
                break;
            }
        }

        this.trainingAges++;
        this.sumReward += this.lastReward;
        this.maxReward = Math.max(this.maxReward, this.lastReward);
        this.reset();
    }

    public synchronized boolean step() {
        boolean[] availableActions = availableActions();
        if (!hasAvailableAction(availableActions)) {
            return false;
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

        this.lastReward += reward;

        this.agent.setState(newState);
        this.maze.setAgentPosition(oldState, newState);
        this.visited[newState.r][newState.c] = true;

        agent.updatePolicy(oldState, newState, action.ordinal(), reward);

        if (newState.r == maze.cheeseR && newState.c == maze.cheeseC) {
            return false;
        }

        return true;
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
        System.out.printf("AGES %d-%d\navgReward: %.2f\nmaxReward: %.0f\nlastReward: %.0f\n\n",
                this.trainingAges - ages + 1, this.trainingAges, this.sumReward / this.trainingAges,
                this.maxReward, this.lastReward);
    }

    public synchronized void resetPolicy() {
        this.maxReward = Double.NEGATIVE_INFINITY;
        this.sumReward = 0;
        this.lastReward = 0;

        this.agentPolicy = new Policy(
                this.maze.height,
                this.maze.width,
                this.discountFactor,
                this.learningRate
        );
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

//    private int numOfAvailableActions(boolean[] availableActions) {
//        int counter = 0;
//        for (boolean available : availableActions) {
//            if (available) {
//                counter++;
//            }
//        }
//        return counter;
//    }
}

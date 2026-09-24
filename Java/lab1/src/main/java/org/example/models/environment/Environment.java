package org.example.models.environment;

import org.example.models.agents.*;
import org.example.models.agents.Direction;

import java.util.ArrayList;
import java.util.Random;

public class Environment {
    public Environment(Config config) {
        this.tickTime = config.tickTime();
        this.width = config.width();
        this.height = config.height();
        this.map = new Agent[this.width][this.height];
        this.agents = new ArrayList<>();

        Random random = new Random();

        for (int i = 0; i <= config.plantsCount(); i++) {
            int x = random.nextInt(0, this.width);
            int y = random.nextInt(0, this.height);

            if (map[x][y] == null) {
                Agent plant = new Plant(x, y);
                this.map[x][y] = plant;
                this.agents.add(plant);
            }
        }

        for (int i = 0; i <= config.herbivoresCount(); i++) {
            int x = random.nextInt(0, this.width);
            int y = random.nextInt(0, this.height);

            if (map[x][y] == null) {
                Agent herbivore = new Herbivore(x, y);
                this.map[x][y] = herbivore;
                this.agents.add(herbivore);
            }
        }

        for (int i = 0; i <= config.predatorsCount(); i++) {
            int x = random.nextInt(0, this.width);
            int y = random.nextInt(0, this.height);

            if (map[x][y] == null) {
                Agent predator = new Predator(x, y);
                this.map[x][y] = predator;
                this.agents.add(predator);
            }
        }
    }

    public final int width, height;
    public final int tickTime;

    private final Agent[][] map;
    private final ArrayList<Agent> agents ;

    private boolean inBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    private void trySpawn(Agent agent) {
        int x = agent.getX();
        int y = agent.getY();

        Cell[][] visionField = getVisionField(agent, 1);
        for (int relX = 0; relX < visionField.length; relX++) {
            for (int relY = 0; relY < visionField.length; relY++) {

                if (visionField[relX][relY].content() == CellContent.EMPTY) {
                    int newX = x + relX - 1;
                    int newY = y + relY - 1;

                    Agent offspring = agent.makeOffspring(newX, newY);
                    map[offspring.getX()][offspring.getY()] = offspring;
                    agents.add(offspring);
                    return;
                }

            }
        }
    }

    private void spawnPlants(int count) {
        Random random = new Random();
        for (int i = 0; i <= count; i++) {
            int x = random.nextInt(0, this.width);
            int y = random.nextInt(0, this.height);

            if (map[x][y] == null) {
                Agent plant = new Plant(x, y);
                this.map[x][y] = plant;
                this.agents.add(plant);
            }
        }
    }

    public Agent[][] getMap() { return map; }

    public ArrayList<Agent> getAgents() { return agents; }

    public Agent getAgentByCoords(int x, int y) { return map[x][y]; }

    public boolean moveAgent(MobileAgent agent, Direction dir) {
        if (dir == null) { return false; }

        int newX = agent.getX() + dir.dx;
        int newY = agent.getY() + dir.dy;

        if (!inBounds(newX, newY)) { return false; }
        if (map[newX][newY] != null) { return false; }

        map[agent.getX()][agent.getY()] = null;
        map[newX][newY] = agent;
        agent.setPosition(newX, newY);

        return true;
    }

    public Cell[][] getVisionField(Agent agent, int radius) {
        int len = 2 * radius + 1;
        Cell[][] visionField = new Cell[len][len];

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                int mx = agent.getX() + dx;
                int my = agent.getY() + dy;

                int vx = radius + dx;
                int vy = radius + dy;

                visionField[vx][vy] = inBounds(mx, my)
                        ? new Cell(CellContent.contentOf(map[mx][my]))
                        : new Cell(CellContent.OUT_OF_BOUNDS);
            }
        }
        return visionField;
    }

    public void removeDead() {
        for (Agent agent : new ArrayList<>(agents)) {
            if (!agent.isAlive()) {
                map[agent.getX()][agent.getY()] = null;
                agents.remove(agent);
            }
        }
    }

    public void spawnNewAgents() {
        for (Agent agent : new ArrayList<>(agents)) {
            if (agent.getEnergy() > Agent.energyLimit) {
                trySpawn(agent);
            }
        }
    }

    public void tick() {
        // ход хищников
        for (Agent agent : this.agents.stream()
                .filter(a -> a instanceof Predator)
                .toArray(Agent[]::new)) {
            if (agent.isAlive()) {
                agent.act(this);
            }
        }
        // ход остальных агентов
        for (Agent agent : this.agents.stream()
                .filter(a -> !(a instanceof Predator))
                .toArray(Agent[]::new)) {
            if (agent.isAlive()) {
                agent.act(this);
            }
        }

        removeDead();
        spawnNewAgents();
        spawnPlants(10);
    }
}

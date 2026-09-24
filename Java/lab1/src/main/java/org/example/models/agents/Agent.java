package org.example.models.agents;

import org.example.models.environment.Environment;

public abstract class Agent {

    public Agent(int x, int y) {
        this.x = x;
        this.y = y;
    }

    private boolean alive = true;
    protected int x;
    protected int y;
    protected int energy = 25;
    public static int energyLimit = 50;
    public static int offspringCost = 25;


    public int getX() { return this.x; }

    public int getY() { return this.y; }

    public int getEnergy() { return this.energy; }

    public void changeEnergy(int delta) { this.energy += delta; }

    public boolean isAlive() { return this.alive; }

    public void die() { this.alive = false; }

    public abstract Agent makeOffspring(int x, int y);

    public abstract void act(Environment env);
}

package org.example.models.agents;

import org.example.models.environment.Environment;

public class Plant extends Agent {

    public Plant(int x, int y) { super(x, y); }

    @Override
    public Agent makeOffspring(int x, int y) {
        changeEnergy(-offspringCost);
        return new Plant(x, y);
    }

    @Override
    public void act(Environment env) {
        changeEnergy(1);
        if (getEnergy() <= 0) {
            die();
        }
    }
}

package org.example.models.agents;

import org.example.models.enums.Direction;
import org.example.models.environment.Cell;
import org.example.models.environment.Environment;

public class Herbivore extends MobileAgent {

    public Herbivore(int x, int y) { super(x, y); }

    @Override
    protected boolean tryEat(Environment env, Agent victim) {
        if (victim instanceof Plant) {
            changeEnergy(5);
            victim.die();
            return true;
        }
        return false;
    }

    @Override
    public Agent makeOffspring(int x, int y) {
        changeEnergy(-offspringCost);
        return new Herbivore(x, y);
    }

    public Direction decideMove(Cell[][] visionField) {
        boolean plantInSight = false;
        int closestPlantDist = Integer.MAX_VALUE;
        Direction direction = null;
        int centerX = visionField.length / 2,
            centerY = visionField[0].length / 2;

        for (int x = 0; x < visionField.length; x++) {
            for (int y = 0; y < visionField[x].length; y++) {
                if (x == centerX && y == centerY) { continue; }

                switch (visionField[x][y].content()) {
                    case PREDATOR -> {
                        return Direction.oppositeOf(defineDirection(centerX, centerY, x, y));
                    }
                    case PLANT -> {
                        plantInSight = true;
                        int dist = Cell.getManhattanDistance(x, y, centerX, centerY);
                        if (dist < closestPlantDist) {
                            closestPlantDist = dist;
                            direction = defineDirection(centerX, centerY, x, y);
                        }
                    }
                }

            }
        }

        if (plantInSight) { return direction; }
        return Direction.random();
    }
}

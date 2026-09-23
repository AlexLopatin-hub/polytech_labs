package org.example.models.agents;

import org.example.models.enums.CellContent;
import org.example.models.enums.Direction;
import org.example.models.environment.Cell;
import org.example.models.environment.Environment;

public class Predator extends MobileAgent {

    public Predator(int x, int y) {
        super(x, y);
    }

    @Override
    protected boolean tryEat(Environment env, Agent victim) {
        if (victim instanceof Herbivore) {
            changeEnergy(5);
            victim.die();
            return true;
        }
        return false;
    }

    @Override
    public Agent makeOffspring(int x, int y) {
        changeEnergy(-offspringCost);
        return new Predator(x, y);
    }

    @Override
    public Direction decideMove(Cell[][] visionField) {
        boolean herbivoreInSight = false;
        int closestDist = Integer.MAX_VALUE;
        Direction direction = null;
        int centerX = 2, centerY = 2;

        for (int x = 0; x < visionField.length; x++) {
            for (int y = 0; y < visionField[x].length; y++) {
                if (x == centerX && y == centerY) { continue; }

                if (visionField[x][y].content() == CellContent.HERBIVORE) {
                    herbivoreInSight = true;
                    int dist = Cell.getManhattanDistance(x, y, centerX, centerY);
                    if (dist < closestDist) {
                        closestDist = dist;
                        direction = defineDirection(centerX, centerY, x, y);
                    }
                }
            }

        }

        if (herbivoreInSight) { return direction; }
        return Direction.random();
    }
}

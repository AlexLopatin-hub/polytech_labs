package org.example.models.agents;

import org.example.models.enums.CellContent;
import org.example.models.enums.Direction;
import org.example.models.environment.Cell;
import org.example.models.environment.Environment;

public abstract class MobileAgent extends Agent {

    public MobileAgent(int x, int y) { super(x, y); }

    protected abstract boolean tryEat(Environment env, Agent victim);

    public abstract Direction decideMove(Cell[][] visionField);

    protected Direction defineDirection(int xStart, int yStart, int xDest, int yDest) {
        int dx = xDest - xStart;
        int dy = yDest - yStart;
        return (Math.abs(dx) >= Math.abs(dy))
                ? (dx > 0 ? Direction.RIGHT : Direction.LEFT)
                : (dy > 0 ? Direction.UP : Direction.DOWN);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean eat(Environment env) {
        int visionRadius = 1;
        Cell[][] visionField = env.getVisionField(this, visionRadius);
        for (int x = 0; x < visionField.length; x++) {
            for (int y = 0; y < visionField[x].length; y++) {
                if (
                    visionField[x][y].content() != CellContent.OUT_OF_BOUNDS
                    && visionField[x][y].content() != CellContent.EMPTY
                ) {
                    Agent victim = env.getAgentByCoords(
                            this.x + x - visionRadius,
                            this.y + y - visionRadius
                    );

                    boolean ok = tryEat(env, victim);
                    if (ok) { return true; }
                }
            }
        }
        return false;
    }

    public void act(Environment env) {
        boolean ok = eat(env);
        if (ok) {
            return;
        }

        changeEnergy(-1);
        if (getEnergy() <= 0) {
            die();
            return;
        }

        int visionRadius = 2;
        Cell[][] visionField = env.getVisionField(this, visionRadius);
        Direction direction = decideMove(visionField);
        env.moveAgent(this, direction);
    }
}

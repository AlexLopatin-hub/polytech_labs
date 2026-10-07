package org.example.internal.agent;

public enum Action {
    UP(-1, 0), RIGHT(0, 1), DOWN(1, 0), LEFT(0, -1);

    Action(int dr, int dc) { this.dr = dr; this.dc = dc; }
    public final int dr, dc;

    public static Action actionFromID(int actionID) {
        switch (actionID) {
            case 0 -> { return Action.UP; }
            case 1 -> { return Action.RIGHT; }
            case 2 -> { return Action.DOWN; }
            case 3 -> { return Action.LEFT; }
        }
        return null;
    }
}

package org.example;

public enum Direction {
    NORTH(-1, 0, "Север", "↑"), EAST(0, 1, "Восток", "→"),
    SOUTH(1, 0, "Юг", "↓"), WEST(0, -1, "Запад", "←");
    final int dr, dc;
    final String title, arrow;
    Direction(int dr, int dc, String title, String arrow) {
        this.dr = dr; this.dc = dc; this.title = title; this.arrow = arrow;
    }
    public Direction left() { return values()[(ordinal() + 3) % 4]; }
    public String title() { return title; }
    public Direction right() { return values()[(ordinal() + 1) % 4]; }
    public static Direction toward(Position from, Position to) {
        if (from.row() == to.row()) return to.col() > from.col() ? EAST : WEST;
        return to.row() > from.row() ? SOUTH : NORTH;
    }
}

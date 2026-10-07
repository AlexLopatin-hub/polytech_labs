package org.example.internal.models;

public enum CellType {
    WALL('#'), EMPTY(' '), CHEESE('C'), WATER('0'), SHOCK('~'), MOUSE('M');

    final public char symbol;
    CellType(char symbol) { this.symbol = symbol; }
}

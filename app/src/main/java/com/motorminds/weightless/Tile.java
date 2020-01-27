package com.motorminds.weightless;

public class Tile {
    public final Cell cell;
    public final int color;

    public Tile(int x, int y, int color) {
        this(new Cell(x, y), color);
    }

    public Tile(Cell cell, int color) {
        this.cell = cell;
        this.color = color;
    }

    @Override
    public String toString() {
        return "Tile{" + cell +
                ", color=" + color +
                '}';
    }
}

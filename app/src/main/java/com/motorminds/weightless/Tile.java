package com.motorminds.weightless;

public class Tile {
    public final Cell cell;
    public final int color;

    public Tile(Cell cell, int color) {
        this.cell = cell;
        this.color = color;
    }

    public Tile(int x, int y, int color) {
        this.cell = new Cell(x, y);
        this.color = color;
    }

    @Override
    public String toString() {
        return "Tile{" +
                "cell=" + cell +
                ", color=" + color +
                '}';
    }
}

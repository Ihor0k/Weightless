package com.motorminds.weightless;

public class Tile {
    public final Cell cell;
    public final int color;
    public final Type type;

    public Tile(int x, int y, int color) {
        this(new Cell(x, y), color);
    }

    public Tile(Cell cell, int color) {
        this(cell, color, Type.SIMPLE);
    }

    public Tile(Cell cell, int color, Type type) {
        this.cell = cell;
        this.color = color;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Tile{" + cell +
                ", color=" + color +
                ", type=" + type +
                '}';
    }

    public enum Type {
        SIMPLE,
        VERTICAL,
        HORIZONTAL,
        VERTICAL_HORIZONTAL,
        BOMB
    }
}

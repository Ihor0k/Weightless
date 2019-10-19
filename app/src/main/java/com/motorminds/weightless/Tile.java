package com.motorminds.weightless;

public class Tile extends Cell {
    private int color;

    public Tile(int x, int y, int color) {
        super(x, y);
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}

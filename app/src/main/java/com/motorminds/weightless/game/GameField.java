package com.motorminds.weightless.game;

import com.motorminds.weightless.Cell;
import com.motorminds.weightless.Tile;

public class GameField {
    public final int ROWS_COUNT = 6;
    public final int COLUMNS_COUNT = 4;

    private Integer[][] field;

    public GameField() {
        this.field = new Integer[ROWS_COUNT][COLUMNS_COUNT];
    }

    void setTile(Tile tile) {
        field[tile.cell.y][tile.cell.x] = tile.color;
    }

    void removeTile(int x, int y) {
        field[y][x] = null;
    }

    public Tile getTile(int x, int y) {
        Integer value = field[y][x];
        return value == null ? null : new Tile(x, y, value);
    }

    public boolean hasTile(int x, int y) {
        return field[y][x] != null;
    }

    public boolean hasNoTile(int x, int y) {
        return !hasTile(x, y);
    }

    public boolean isValidPosition(int x, int y) {
        return y >= 0 && y < ROWS_COUNT && x >= 0 && x < COLUMNS_COUNT;
    }

    void dumpField() {
        for (int i = 0; i < ROWS_COUNT; i++) {
            for (int j = 0; j < COLUMNS_COUNT; j++) {
                Tile tile = getTile(j, i);
                if (tile == null) {
                    System.out.print(" ");
                } else {
                    System.out.print(tile.color);
                }
                System.out.print(" ");
            }
            System.out.println();
        }
    }
}

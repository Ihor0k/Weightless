package com.motorminds.weightless.game;

import com.motorminds.weightless.Tile;

import java.util.HashMap;
import java.util.Map;

public class GameField {
    public final int ROWS_COUNT = 6;
    public final int COLUMNS_COUNT = 4;

    private Tile[][] field;

    GameField() {
        this.field = new Tile[ROWS_COUNT][COLUMNS_COUNT];
    }

    void setTile(Tile tile) {
        field[tile.cell.y][tile.cell.x] = tile;
    }

    void removeTile(int x, int y) {
        field[y][x] = null;
    }

    public Tile getTile(int x, int y) {
        return field[y][x];
    }

    public boolean hasTile(int x, int y) {
        return getTile(x, y) != null;
    }

    public boolean hasNoTile(int x, int y) {
        return !hasTile(x, y);
    }

    void dumpField() {
        Map<Integer, Integer> colorMap = new HashMap<>();
        for (int i = 0; i < ROWS_COUNT; i++) {
            for (int j = 0; j < COLUMNS_COUNT; j++) {
                Tile tile = getTile(j, i);
                if (tile == null) {
                    System.out.print(0);
                } else {
                    Integer num = colorMap.get(tile.color);
                    if (num == null) {
                        num = colorMap.size() + 1;
                        colorMap.put(tile.color, num);
                    }
                    System.out.print(num);
                }
                System.out.print(" ");
            }
            System.out.println();
        }
    }
}

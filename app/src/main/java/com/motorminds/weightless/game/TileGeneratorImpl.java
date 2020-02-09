package com.motorminds.weightless.game;

import com.motorminds.weightless.Cell;
import com.motorminds.weightless.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TileGeneratorImpl implements TileGenerator {
    private final static int INIT_CELLS_COUNT = 9;
    private final static float ACTION_TILE_CHANCE = (float) 0.15;

    private int[] colorPalette;
    private GameField field;
    private Random random;

    public TileGeneratorImpl(GameField field, int[] palette) {
        this.colorPalette = palette;
        this.field = field;
        this.random = new Random();
    }

    @Override
    public void initField() {
        for (int i = 0; i < INIT_CELLS_COUNT; i++) {
            Tile tile = generate();
            field.setTile(tile);
        }
    }

    @Override
    public Tile generate() {
        int value = nextColor();
        List<Cell> emptyCells = availableEmptyCells();
        if (emptyCells.isEmpty()) {
            return null;
        }
        int column = random.nextInt(emptyCells.size());
        Cell cell = emptyCells.get(column);
        Tile.Type tileType;
        if (random.nextFloat() < ACTION_TILE_CHANCE) {
            tileType = Tile.Type.randomActionType();
        } else {
            tileType = Tile.Type.SIMPLE;
        }
        return new Tile(cell, value, tileType);
    }

    private List<Cell> availableEmptyCells() {
        List<Integer> emptyCellsInColumn = new ArrayList<>(field.COLUMNS_COUNT);
        for (int i = 0; i < field.COLUMNS_COUNT; i++) {
            int emptyCells = emptyCellsInColumn(i);
            emptyCellsInColumn.add(emptyCells);
        }
        /*
        Avoid this situation:
        | 0 0 0 0 |
        | 0 0 0 0 |
        | 0 0 0 0 |
        | x x x x |
        | x x x x |
         */
        List<Cell> result = new ArrayList<>(field.COLUMNS_COUNT);
        int max = Collections.max(emptyCellsInColumn);
        int min = Collections.min(emptyCellsInColumn);
        int countOfMax = Collections.frequency(emptyCellsInColumn, max);
        if (max - min == 1 && countOfMax == 1) {
            for (int i = 0; i < emptyCellsInColumn.size(); i++) {
                int value = emptyCellsInColumn.get(i);
                if (value != max && value > 0) {
                    result.add(new Cell(i, value - 1));
                }
            }
        } else {
            for (int i = 0; i < emptyCellsInColumn.size(); i++) {
                int value = emptyCellsInColumn.get(i);
                if (value > 0) {
                    result.add(new Cell(i, value - 1));
                }
            }
        }
        return result;
    }

    private int emptyCellsInColumn(int column) {
        int n = 0;
        for (int i = 0; i < field.ROWS_COUNT; i++) {
            if (field.hasTile(column, i)) {
                break;
            }
            n++;
        }
        return n;
    }

    private int nextColor() {
        return colorPalette[random.nextInt(colorPalette.length)];
    }
}

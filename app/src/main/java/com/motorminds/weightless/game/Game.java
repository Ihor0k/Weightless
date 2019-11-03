package com.motorminds.weightless.game;

import com.motorminds.weightless.Cell;
import com.motorminds.weightless.Tile;
import com.motorminds.weightless.events.GameEvent;
import com.motorminds.weightless.events.GameEventBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Game {
    private final static int INIT_CELLS_COUNT = 9;

    private GameField field;
    private final GameEventBuilder eventBuilder;
    private final ColorGenerator colorGenerator;
    private final Random random;

    private int score;

    public Game(GameEventBuilder eventBuilder, ColorGenerator colorGenerator) {
        this(eventBuilder, colorGenerator, null, 0);
    }

    public Game(GameEventBuilder eventBuilder, ColorGenerator colorGenerator, GameField field, int score) {
        this.eventBuilder = eventBuilder;
        this.colorGenerator = colorGenerator;
        this.random = new Random();
        if (field != null) {
            this.field = field;
        } else {
            initField();
        }
        this.score = score;
    }

    private void initField() {
        this.field = new GameField();
        for (int i = 0; i < INIT_CELLS_COUNT; i++) {
            generateRandomTile();
        }
    }

    public GameEvent move(Cell from, int toColumn) {
        int fromX = from.x;
        int y = from.y;
        GameEvent event;
        if (field.hasNoTile(toColumn, y)) {
            event = moveTile(fromX, y, toColumn, y);
            GameEvent subEvent1 = checkTopTile(fromX, y);
            GameEvent subEvent2 = moveDown(toColumn, y);
            event.withEvents(subEvent1, subEvent2);
        } else {
            int toX = toColumn < fromX ? toColumn + 1 : toColumn - 1;
            event = moveTile(fromX, y, toX, y);
            GameEvent subEvent1 = checkTopTile(fromX, y);
            GameEvent subEvent2 = pushTiles(toX, y, toColumn, y);
            if (field.hasTile(toX, y)) {
                GameEvent subEvent3 = moveDown(toX, y);
                event.withEvent(subEvent3);
            }
            event.withEvents(subEvent1, subEvent2);
        }
        GameEvent event2 = generateRandomTile();
        event.beforeEvent(event2);
        field.dumpField();
        return event;
    }

    public GameEvent create(Tile tile) {
        return setTile(tile);
    }

    private GameEvent pushTiles(int tile1X, int tile1Y, int tile2X, int tile2Y) {
        Tile tile1 = field.getTile(tile1X, tile1Y);
        Tile tile2 = field.getTile(tile2X, tile2Y);
        if (tile1 == null || tile2 == null) {
            return eventBuilder.nullEvent();
        }
        int tile1Value = tile1.color;
        int tile2Value = tile2.color;
        if (tile1Value == tile2Value) {
            GameEvent event1 = removeTileAfterPushing(tile1X, tile1Y);
            GameEvent event2 = removeTileAfterPushing(tile2X, tile2Y);
            GameEvent event3 = incrementScore();
            return eventBuilder.onMultiEvents(event1, event2, event3);
        } else {
            return eventBuilder.nullEvent();
        }
    }

    private GameEvent removeTileAfterPushing(int x, int y) {
        GameEvent event = removeTile(x, y);
        GameEvent subEvent = checkTopTile(x, y);
        event.withEvent(subEvent);
        return event;
    }

    private GameEvent incrementScore() {
        this.score += 1;
        return eventBuilder.onScoreEvent(1);
    }

    private GameEvent moveDown(int x, int y) {
        int newY = bottomAvailableRow(x, y);
        if (newY <= y) {
            return eventBuilder.nullEvent();
        }
        GameEvent event1 = moveTile(x, y, x, newY);
        GameEvent event2 = checkTopTile(x, y);
        event1.withEvent(event2);
        int bottomTileY = newY + 1;
        if (field.isValidPosition(x, bottomTileY)) {
            GameEvent event3 = pushTiles(x, newY, x, bottomTileY);
            event2.withEvent(event3);
        }
        return event1;
    }

    private GameEvent checkTopTile(int x, int y) {
        int topY = y - 1;
        if (field.isValidPosition(x, topY) && field.hasTile(x, topY)) {
            return moveDown(x, topY);
        }
        return eventBuilder.nullEvent();
    }

    private GameEvent removeTile(int x, int y) {
        field.removeTile(x, y);
        return eventBuilder.onRemove(new Cell(x, y));
    }

    private GameEvent setTile(Tile tile) {
        field.setTile(tile);
        return eventBuilder.onCreate(tile);
    }

    private GameEvent moveTile(int fromX, int fromY, int toX, int toY) {
        if (fromX == toX && fromY == toY) {
            return eventBuilder.nullEvent();
        }
        Tile oldTile = field.getTile(fromX, fromY);
        field.removeTile(fromX, fromY);
        Tile newTile = new Tile(toX, toY, oldTile.color);
        field.setTile(newTile);
        return eventBuilder.onMove(oldTile.cell, newTile.cell);
    }

    private GameEvent generateRandomTile() {
        int value = colorGenerator.nextColor();
        List<Cell> emptyCells = availableEmptyCells();
        int column = random.nextInt(emptyCells.size());
        Cell cell = emptyCells.get(column);
        Tile tile = new Tile(cell.x, cell.y, value);
        return setTile(tile);
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

    private int bottomAvailableRow(int x, int y) {
        for (int i = field.ROWS_COUNT - 1; i > y; i--) {
            if (field.hasNoTile(x, i)) {
                return i;
            }
        }
        return y;
    }

    public GameField getField() {
        return field;
    }

    public int getScore() {
        return score;
    }
}

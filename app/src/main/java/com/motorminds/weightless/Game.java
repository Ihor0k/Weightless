package com.motorminds.weightless;

import com.motorminds.weightless.events.GameEvent;
import com.motorminds.weightless.events.GameEventBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Game {
    public final static int ROWS_COUNT = 6;
    public final static int COLUMNS_COUNT = 4;

    private final Tile[][] field;
    private final GameEventBuilder eventBuilder;
    private final ColorGenerator colorGenerator;

    private int score;
    private int counter = 1;

    public Game(GameEventBuilder eventBuilder, ColorGenerator colorGenerator) {
        this.eventBuilder = eventBuilder;
        this.colorGenerator = colorGenerator;
        this.field = generateField();
        this.score = 0;
    }

    public Game(GameEventBuilder eventBuilder, ColorGenerator colorGenerator, Tile[][] field, int score) {
        this.eventBuilder = eventBuilder;
        this.colorGenerator = colorGenerator;
        this.field = field;
        this.score = score;
    }

    private Tile[][] generateField() {
        // TODO
        int[][] fieldInt = {
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {1, 0, 0, 1},
                {1, 0, 0, 1},
                {1, 1, 0, 1},
                {1, 1, 1, 1}
        };
        Tile[][] field = new Tile[ROWS_COUNT][COLUMNS_COUNT];
        for (int i = 0; i < ROWS_COUNT; i++) {
            for (int j = 0; j < COLUMNS_COUNT; j++) {
                int value = fieldInt[i][j];
                if (value != 0) {
                    field[i][j] = new Tile(i, j, colorGenerator.nextColor());
                }
            }
        }
        return field;
    }

    public GameEvent move(Cell from, int toColumn) {
        int fromX = from.x;
        int y = from.y;
        GameEvent event;
        if (isEmptyCell(toColumn, y)) {
            event = moveTile(fromX, y, toColumn, y);
            GameEvent subEvent1 = checkTopTile(fromX, y);
            GameEvent subEvent2 = moveDown(toColumn, y);
            event.withEvents(subEvent1, subEvent2);
        } else {
            int toX = toColumn < fromX ? toColumn + 1 : toColumn - 1;
            event = moveTile(fromX, y, toX, y);
            GameEvent subEvent1 = checkTopTile(fromX, y);
            GameEvent subEvent2 = pushTiles(toX, y, toColumn, y);
            if (!isEmptyCell(toX, y)) {
                GameEvent subEvent3 = moveDown(toX, y);
                event.withEvent(subEvent3);
            }
            event.withEvents(subEvent1, subEvent2);
        }
        GameEvent event2 = generateRandomTile();
//        GameEvent event3 = generateRandomTile();
        event.beforeEvent(event2);
//        event2.beforeEvent(event3);
//        dumpField();
        return event;
    }

    private GameEvent pushTiles(int tile1X, int tile1Y, int tile2X, int tile2Y) {
        Tile tile1 = getTile(tile1X, tile1Y);
        Tile tile2 = getTile(tile2X, tile2Y);
        if (tile1 == null || tile2 == null) {
            return eventBuilder.nullEvent();
        }
        int tile1Value = tile1.getColor();
        int tile2Value = tile2.getColor();
        if (tile1Value == tile2Value) {
            GameEvent event1 = removeTileAfterPushing(tile1X, tile1Y);
            GameEvent event2 = removeTileAfterPushing(tile2X, tile2Y);
            GameEvent event3 = incrementScore(1);
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

    private GameEvent incrementScore(int score) {
        this.score += score;
        return eventBuilder.onScoreEvent(score);
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
        if (cellExists(x, bottomTileY)) {
            GameEvent event3 = pushTiles(x, newY, x, bottomTileY);
            event2.withEvent(event3);
        }
        return event1;
    }

    private GameEvent checkTopTile(int x, int y) {
        int topY = y - 1;
        if (cellExists(x, topY) && !isEmptyCell(x, topY)) {
            return moveDown(x, topY);
        }
        return eventBuilder.nullEvent();
    }

    private GameEvent setTileValue(int x, int y, int value) {
        getTile(x, y).setColor(value);
//        return eventBuilder.onUpdate(new Cell(x, y), value);
        return eventBuilder.nullEvent();    // TODO
    }

    private GameEvent removeTile(int x, int y) {
        field[y][x] = null;
        return eventBuilder.onRemove(new Cell(x, y));
    }

    private GameEvent setTile(Tile tile, int x, int y) {
        field[y][x] = tile;
        return eventBuilder.onCreate(new Cell(x, y), tile.getColor());
    }

    private GameEvent moveTile(int fromX, int fromY, int toX, int toY) {
        if (fromX == toX && fromY == toY) {
            return eventBuilder.nullEvent();
        }
        Tile tile = getTile(fromX, fromY);
        field[fromY][fromX] = null;
        field[toY][toX] = tile;
        return eventBuilder.onMove(new Cell(fromX, fromY), new Cell(toX, toY));
    }

    private Tile getTile(int x, int y) {
        return field[y][x];
    }

    private boolean isEmptyCell(int x, int y) {
        return getTile(x, y) == null;
    }

    private boolean cellExists(int x, int y) {
        return y >= 0 && y < ROWS_COUNT && x >= 0 && x < COLUMNS_COUNT;
    }

    private GameEvent generateRandomTile() {
        Random random = new Random();
//        int value = random.nextInt(9) + 1 + (counter - counter % 10);
        int value = colorGenerator.nextColor();
        List<Cell> emptyCells = availableEmptyCells();
        int column = random.nextInt(emptyCells.size());
        Cell cell = emptyCells.get(column);
        Tile tile = new Tile(cell.x, cell.y, value);
        counter++;
        return setTile(tile, cell.x, cell.y);
    }

    private List<Cell> availableEmptyCells() {
        List<Integer> emptyCellsInColumn = new ArrayList<>(COLUMNS_COUNT);
        for (int i = 0; i < COLUMNS_COUNT; i++) {
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
        List<Cell> result = new ArrayList<>(COLUMNS_COUNT);
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
        for (int i = 0; i < ROWS_COUNT; i++) {
            if (!isEmptyCell(column, i)) {
                break;
            }
            n++;
        }
        return n;
    }
//
//    public int leftAvailableColumn(int x, int y, int leftmost) {
//        int column = x;
//        for (int i = x - 1; i >= leftmost; i--) {
//            if (isEmptyCell(i, y)) {
//                column = i;
//            } else {
//                break;
//            }
//        }
//        return column;
//    }
//
//    public int rightAvailableColumn(int x, int y, int rightmost) {
//        int column = x;
//        for (int i = x + 1; i <= rightmost; i++) {
//            if (isEmptyCell(i, y)) {
//                column = i;
//            } else {
//                break;
//            }
//        }
//        return column;
//    }

    public int bottomAvailableRow(int x, int y) {
        for (int i = ROWS_COUNT - 1; i > y; i--) {
            if (isEmptyCell(x, i)) {
                return i;
            }
        }
        return y;
    }

    public Tile[][] getField() {
        // TODO: return copy
        return field;
    }

    private void dumpField() {
        for (int i = 0; i < ROWS_COUNT; i++) {
            for (int j = 0; j < COLUMNS_COUNT; j++) {
                Tile tile = getTile(j, i);
                if (tile == null) {
                    System.out.print(" ");
                } else {
                    System.out.print(tile.getColor());
                }
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    public int getScore() {
        return score;
    }
}

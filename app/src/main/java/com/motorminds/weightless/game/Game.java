package com.motorminds.weightless.game;

import com.motorminds.weightless.Cell;
import com.motorminds.weightless.Tile;
import com.motorminds.weightless.events.GameEvent;
import com.motorminds.weightless.events.GameEventFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Game {
    private final static int INIT_CELLS_COUNT = 9;

    private GameField field;
    private final GameEventFactory eventFactory;
    private final ColorGenerator colorGenerator;
    private final Random random;

    private int score;
    private int currentMoveScore;
    private int currentMoveCombo;
    private GameEventFactory.GameEventBuilder eventBuilder;
    private boolean gameOver;

    public Game(GameEventFactory eventFactory, ColorGenerator colorGenerator) {
        this(eventFactory, colorGenerator, null, 0);
    }

    public Game(GameEventFactory eventFactory, ColorGenerator colorGenerator, GameField field, int score) {
        this.eventFactory = eventFactory;
        this.colorGenerator = colorGenerator;
        this.random = new Random();
        if (field != null) {
            this.field = field;
        } else {
            initField();
        }
        this.score = score;
        this.currentMoveScore = 0;
        this.currentMoveCombo = 1;
        this.gameOver = false;
    }

    private void initField() {
        this.field = new GameField();
        for (int i = 0; i < INIT_CELLS_COUNT; i++) {
            generateRandomTile();
        }
    }

    public GameEvent moveTile(Cell cell, int toColumn) {
        int fromX = cell.x;
        int y = cell.y;
        int toX = wantToMove(cell, toColumn);
        if (fromX == toX) {
            return null;
        }
        GameEvent event;
        if (field.hasTile(toX, y)) {
            int realToX = toX < fromX ? toX + 1 : toX - 1;
            GameEvent moveEvent = moveTile(fromX, y, realToX, y);
            this.eventBuilder = eventFactory.builder(moveEvent);
            GameEvent checkFromEvent = checkColumn(fromX);
            GameEvent pushEvent = pushTiles(new Cell(realToX, y), new Cell(toX, y));
            GameEvent pushAndCheckFrom = eventBuilder.playTogether(pushEvent, checkFromEvent);
            GameEvent checkToEvent = checkColumn(toX);
            event = eventBuilder.playSequentially(moveEvent, pushAndCheckFrom, checkToEvent);
        } else {
            GameEvent moveEvent = moveTile(fromX, y, toX, y);
            this.eventBuilder = eventFactory.builder(moveEvent);
            GameEvent checkFromEvent = checkColumn(fromX);
            GameEvent checkToEvent = checkColumn(toX);
            GameEvent checkFromEvents = eventBuilder.playTogether(checkFromEvent, checkToEvent);
            event = eventBuilder.playSequentially(moveEvent, checkFromEvents);
        }
        GameEvent generateTileEvent = generateRandomTile();
        eventBuilder.append(event, generateTileEvent);
        this.score += currentMoveScore;
        this.currentMoveScore = 0;
        this.currentMoveCombo = 1;
        field.dumpField();
        return eventBuilder.build();
    }

    /**
     * Check all the intermediate cells between current
     * position and the desired one and returns allowed
     * column into which the cell can be moved
     *
     * @param cell     cell
     * @param toColumn column in which user wants to move the cell
     * @return column in which cell can be moved
     */
    public int wantToMove(Cell cell, int toColumn) {
        int x = cell.x;
        int y = cell.y;
        int dir = x < toColumn ? 1 : -1;
        if (x == toColumn || field.hasTile(x + dir, y)) {
            return x;
        }
        Tile tile = field.getTile(x, y);
        int newX = x + dir;
        for (int i = newX; i != toColumn + dir; i += dir) {
            Tile toTile = field.getTile(i, y);
            if (toTile == null) {
                newX = i;
            } else if (toTile.color == tile.color) {
                return i;
            } else {
                return newX;
            }
        }
        return newX;
    }

    public GameEvent createTile(Tile tile) {
        field.setTile(tile);
        return eventFactory.create(tile);
    }

    private GameEvent pushTiles(Cell... cells) {
        GameEventFactory.MultiEventBuilder eventBuilder = eventFactory.multiEventBuilder(this.eventBuilder);
        for (Cell cell : cells) {
            GameEvent event = removeTile(cell.x, cell.y);
            eventBuilder.add(event);
        }
        GameEvent event = incrementScore(cells.length);
        eventBuilder.add(event);
        return eventBuilder.build();
    }

    private GameEvent incrementScore(int val) {
        int incVal = currentMoveCombo * val;
        this.currentMoveCombo *= 2;
        this.currentMoveScore += incVal;
        return eventFactory.score(incVal);
    }

    private GameEvent checkColumn(int x) {
        int lowerY = lowerAvailableRow(x);
        if (!hasTilesAbove(x, lowerY)) {
            return null;
        }
        GameEvent stackEvent = stackTilesInColumn(x, lowerY);
        GameEvent pushEvent = pushTilesInColumn(x, lowerY);
        GameEvent subEvent = checkColumn(x);
        return eventBuilder.playSequentially(stackEvent, pushEvent, subEvent);
    }

    private boolean hasTilesAbove(int x, int y) {
        for (int i = y - 1; i >= 0; i--) {
            if (field.hasTile(x, i)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Move all tiles above y down
     *
     * @param y should be the lowest empty cell in the column
     */
    private GameEvent stackTilesInColumn(int x, int y) {
        GameEventFactory.MultiEventBuilder eventBuilder = eventFactory.multiEventBuilder(this.eventBuilder);
        for (int i = y - 1; i >= 0; i--) {
            if (field.hasTile(x, i)) {
                GameEvent event = moveTile(x, i, x, y--);
                eventBuilder.add(event);
            }
        }
        return eventBuilder.build();
    }

    /**
     * Push all tile groups of the same color above y
     */
    private GameEvent pushTilesInColumn(int x, int y) {
        GameEventFactory.MultiEventBuilder eventBuilder = eventFactory.multiEventBuilder(this.eventBuilder);
        int currentColor = field.getTile(x, y).color;
        while (y < field.ROWS_COUNT - 1 && field.getTile(x, y + 1).color == currentColor) {
            y++;
        }
        List<Cell> currentGroup = new LinkedList<>();
        for (int i = y; i >= 0; i--) {
            Tile tile = field.getTile(x, i);
            if (tile == null || tile.color != currentColor) {
                if (currentGroup.size() > 1) {
                    Cell[] cells = new Cell[currentGroup.size()];
                    GameEvent event = pushTiles(currentGroup.toArray(cells));
                    eventBuilder.add(event);
                }
                if (tile == null) {
                    break;
                }
                currentGroup.clear();
                currentColor = tile.color;
            }
            currentGroup.add(tile.cell);
        }
        return eventBuilder.build();
    }

    private GameEvent removeTile(int x, int y) {
        field.removeTile(x, y);
        return eventFactory.remove(new Cell(x, y));
    }

    private GameEvent moveTile(int fromX, int fromY, int toX, int toY) {
        if (fromX == toX && fromY == toY) {
            return null;
        }
        Tile oldTile = field.getTile(fromX, fromY);
        field.removeTile(fromX, fromY);
        Tile newTile = new Tile(toX, toY, oldTile.color);
        field.setTile(newTile);
        return eventFactory.move(oldTile.cell, newTile.cell);
    }

    private GameEvent generateRandomTile() {
        int value = colorGenerator.nextColor();
        List<Cell> emptyCells = availableEmptyCells();
        if (emptyCells.isEmpty()) {
            this.gameOver = true;
            return null;
        }
        int column = random.nextInt(emptyCells.size());
        Cell cell = emptyCells.get(column);
        Tile tile = new Tile(cell, value);
        return createTile(tile);
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

    private int lowerAvailableRow(int x) {
        for (int i = field.ROWS_COUNT - 1; i >= 0; i--) {
            if (field.hasNoTile(x, i)) {
                return i;
            }
        }
        return -1;
    }

    public GameField getField() {
        return field;
    }

    public int getScore() {
        return score;
    }

    public boolean isGameOver() {
        return gameOver;
    }
}

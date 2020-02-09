package com.motorminds.weightless.game;

import com.motorminds.weightless.Cell;
import com.motorminds.weightless.GameEventChain;
import com.motorminds.weightless.Tile;
import com.motorminds.weightless.events.GameEvent;
import com.motorminds.weightless.events.GameEventFactory;

import java.util.LinkedList;
import java.util.List;

public class Game {
    private final GameEventFactory eventFactory;
    private final TileGenerator tileGenerator;
    private GameField field;
    private int score;
    private int currentMoveScore;
    private int currentMoveCombo;
    private GameEventChain eventChain;

    public Game(GameEventFactory eventFactory, TileGenerator tileGenerator, GameField field) {
        this(eventFactory, tileGenerator, field, 0);
    }

    public Game(GameEventFactory eventFactory, TileGenerator tileGenerator, GameField field, int score) {
        this.eventFactory = eventFactory;
        this.tileGenerator = tileGenerator;
        this.field = field;
        this.score = score;
        this.currentMoveScore = 0;
        this.currentMoveCombo = 1;
    }

    public GameEventChain moveTile(Cell cell, int toColumn) {
        int fromX = cell.x;
        int y = cell.y;
        int toX = wantToMove(cell, toColumn);
        if (fromX == toX || isGameOver()) {
            return null;
        }
        this.currentMoveScore = 0;
        this.currentMoveCombo = 1;
        GameEvent event;
        if (field.hasTile(toX, y)) {
            int realToX = toX < fromX ? toX + 1 : toX - 1;
            GameEvent moveEvent = moveTile(fromX, y, realToX, y);
            this.eventChain = new GameEventChain(moveEvent);
            GameEvent checkFromEvent = checkColumn(fromX);
            GameEvent pushEvent = pushTiles(field.getTile(realToX, y), field.getTile(toX, y));
            GameEvent pushAndCheckFrom = eventChain.playTogether(pushEvent, checkFromEvent);
            GameEvent checkToEvent = checkColumn(toX);
            event = eventChain.playSequentially(moveEvent, pushAndCheckFrom, checkToEvent);
        } else {
            GameEvent moveEvent = moveTile(fromX, y, toX, y);
            this.eventChain = new GameEventChain(moveEvent);
            GameEvent checkFromEvent = checkColumn(fromX);
            GameEvent checkToEvent = checkColumn(toX);
            GameEvent checkFromEvents = eventChain.playTogether(checkFromEvent, checkToEvent);
            event = eventChain.playSequentially(moveEvent, checkFromEvents);
        }
        Tile tile = tileGenerator.generate();
        if (tile != null) {
            GameEvent createTileEvent = createTile(tile);
            eventChain.append(event, createTileEvent);
        }
        this.score += currentMoveScore;
        return eventChain;
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

    private GameEvent pushTiles(Tile... tiles) {
        GameEventFactory.MultiEventBuilder multiEventBuilder = eventFactory.multiEventBuilder(this.eventChain);
        for (Tile tile : tiles) {
            GameEvent event = removeTile(tile.cell.x, tile.cell.y);
            multiEventBuilder.add(event);
        }
        GameEvent event = incrementScore(tiles.length);
        multiEventBuilder.add(event);
        return multiEventBuilder.playTogether();
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
        return eventChain.playSequentially(stackEvent, pushEvent, subEvent);
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
        GameEventFactory.MultiEventBuilder multiEventBuilder = eventFactory.multiEventBuilder(this.eventChain);
        for (int i = y - 1; i >= 0; i--) {
            if (field.hasTile(x, i)) {
                GameEvent event = moveTile(x, i, x, y--);
                multiEventBuilder.add(event);
            }
        }
        return multiEventBuilder.playTogether();
    }

    /**
     * Push all tile groups of the same color above y
     */
    private GameEvent pushTilesInColumn(int x, int y) {
        GameEventFactory.MultiEventBuilder multiEventBuilder = eventFactory.multiEventBuilder(this.eventChain);
        int currentColor = field.getTile(x, y).color;
        while (y < field.ROWS_COUNT - 1 && field.getTile(x, y + 1).color == currentColor) {
            y++;
        }
        List<Tile> currentGroup = new LinkedList<>();
        for (int i = y; i >= 0; i--) {
            Tile tile = field.getTile(x, i);
            if (tile == null || tile.color != currentColor) {
                if (currentGroup.size() > 1) {
                    Tile[] cells = new Tile[currentGroup.size()];
                    GameEvent event = pushTiles(currentGroup.toArray(cells));
                    multiEventBuilder.add(event);
                }
                if (tile == null) {
                    break;
                }
                currentGroup.clear();
                currentColor = tile.color;
            }
            currentGroup.add(tile);
        }
        return multiEventBuilder.playTogether();
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
        for (int i = 0; i < field.COLUMNS_COUNT; i++) {
            if (field.hasNoTile(i, 0)) {
                return false;
            }
        }
        return true;
    }
}

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
            event = eventChain.playSequentially(moveEvent, pushAndCheckFrom);
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
        return eventFactory.create(tile, () -> field.setTile(tile));
    }

    private GameEvent pushTiles(Tile... tiles) {
        GameEventFactory.MultiEventBuilder removeEventBuilder = eventFactory.multiEventBuilder(eventChain);
        for (Tile tile : tiles) {
            GameEvent event = removeTile(tile.cell);
            removeEventBuilder.add(event);
        }
        GameEvent scoreEvent = incrementScore(tiles.length);
        GameEventFactory.MultiEventBuilder actionEventBuilder = eventFactory.multiEventBuilder(eventChain);
        for (Tile tile : tiles) {
            GameEvent event = checkActionTile(tile);
            actionEventBuilder.add(event);
        }
        GameEventFactory.MultiEventBuilder checkEventBuilder = eventFactory.multiEventBuilder(eventChain);
        for (Tile tile : tiles) {
            GameEvent event = checkColumn(tile.cell.x);
            checkEventBuilder.add(event);
        }
        GameEvent removeEvent = removeEventBuilder.playTogether();
        GameEvent removeAndScoreEvent = eventChain.playTogether(removeEvent, scoreEvent);
        GameEvent actionEvent = actionEventBuilder.playTogether();
        GameEvent checkEvent = checkEventBuilder.playTogether();
        return eventChain.playSequentially(removeAndScoreEvent, actionEvent, checkEvent);
    }

    private GameEvent checkActionTile(Tile tile) {
        switch (tile.type) {
            case SIMPLE:
                return null;
            case VERTICAL:
                return pushVertical(tile.cell);
            case HORIZONTAL:
                return pushHorizontal(tile.cell);
            case VERTICAL_HORIZONTAL:
                return pushVerticalHorizontal(tile.cell);
            case BOMB:
                return pushBomb(tile.cell);
        }
        return null;
    }

    private GameEvent pushVertical(Cell cell) {
        int x = cell.x;
        GameEventFactory.MultiEventBuilder multiEventBuilder = eventFactory.multiEventBuilder(eventChain);
        int topY = cell.y - 1;
        int bottomY = cell.y + 1;
        while (topY >= 0 || bottomY < field.ROWS_COUNT) {
            Tile topTile = topY >= 0 ? field.getTile(x, topY) : null;
            Tile bottomTile = bottomY < field.ROWS_COUNT ? field.getTile(x, bottomY) : null;
            GameEvent topEvent = removeAndCheckTile(topTile);
            GameEvent bottomEvent = removeAndCheckTile(bottomTile);
            GameEvent event = eventChain.playTogether(topEvent, bottomEvent);
            multiEventBuilder.add(event);
            topY--;
            bottomY++;
        }
        return multiEventBuilder.playSequentially();
    }

    private GameEvent pushHorizontal(Cell cell) {
        int y = cell.y;
        GameEventFactory.MultiEventBuilder multiEventBuilder = eventFactory.multiEventBuilder(eventChain);
        int leftX = cell.x - 1;
        int rightX = cell.x + 1;
        while (leftX >= 0 || rightX < field.COLUMNS_COUNT) {
            Tile leftTile = leftX >= 0 ? field.getTile(leftX, y) : null;
            Tile rightTile = rightX < field.COLUMNS_COUNT ? field.getTile(rightX, y) : null;
            GameEvent leftEvent = removeAndCheckTile(leftTile);
            GameEvent rightEvent = removeAndCheckTile(rightTile);
            GameEvent event = eventChain.playTogether(leftEvent, rightEvent);
            multiEventBuilder.add(event);
            leftX--;
            rightX++;
        }
        return multiEventBuilder.playSequentially();
    }

    private GameEvent pushVerticalHorizontal(Cell cell) {
        GameEvent verticalEvent = pushVertical(cell);
        GameEvent horizontalEvent = pushHorizontal(cell);
        return eventChain.playTogether(verticalEvent, horizontalEvent);
    }

    private GameEvent pushBomb(Cell cell) {
        boolean hasLeft = cell.x - 1 >= 0;
        boolean hasRight = cell.x + 1 < field.COLUMNS_COUNT;
        boolean hasTop = cell.y - 1 > 0;
        boolean hasBottom = cell.y + 1 < field.ROWS_COUNT;
        GameEventFactory.MultiEventBuilder eventBuilder = eventFactory.multiEventBuilder(eventChain);
        if (hasLeft) {
            if (hasTop) {
                eventBuilder.add(removeAndCheckTile(field.getTile(cell.x - 1, cell.y - 1)));
            }
            eventBuilder.add(removeAndCheckTile(field.getTile(cell.x - 1, cell.y)));
            if (hasBottom) {
                eventBuilder.add(removeAndCheckTile(field.getTile(cell.x - 1, cell.y + 1)));
            }
        }
        if (hasRight) {
            if (hasTop) {
                eventBuilder.add(removeAndCheckTile(field.getTile(cell.x + 1, cell.y - 1)));
            }
            eventBuilder.add(removeAndCheckTile(field.getTile(cell.x + 1, cell.y)));
            if (hasBottom) {
                eventBuilder.add(removeAndCheckTile(field.getTile(cell.x + 1, cell.y + 1)));
            }
        }
        if (hasTop) {
            eventBuilder.add(removeAndCheckTile(field.getTile(cell.x, cell.y - 1)));
        }
        if (hasBottom) {
            eventBuilder.add(removeAndCheckTile(field.getTile(cell.x, cell.y + 1)));
        }
        return eventBuilder.playTogether();
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
        return eventChain.playSequentially(stackEvent, pushEvent);
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
        GameEventFactory.MultiEventBuilder multiEventBuilder = eventFactory.multiEventBuilder(eventChain);
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
        GameEventFactory.MultiEventBuilder multiEventBuilder = eventFactory.multiEventBuilder(eventChain);
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

    private GameEvent removeAndCheckTile(Tile tile) {
        if (tile == null) {
            return null;
        }
        GameEvent removeEvent = removeTile(tile.cell);
        GameEvent actionEvent = checkActionTile(tile);
        GameEvent checkColumnEvent = checkColumn(tile.cell.x);
        return eventChain.playSequentially(removeEvent, actionEvent, checkColumnEvent);
    }

    private GameEvent removeTile(Cell cell) {
        return eventFactory.remove(cell, () -> field.removeTile(cell.x, cell.y));
    }

    private GameEvent moveTile(int fromX, int fromY, int toX, int toY) {
        if (fromX == toX && fromY == toY) {
            return null;
        }
        Tile oldTile = field.getTile(fromX, fromY);
        Tile newTile = new Tile(toX, toY, oldTile.color, oldTile.type);
        return eventFactory.move(oldTile.cell, newTile.cell, () -> {
            field.removeTile(fromX, fromY);
            field.setTile(newTile);
        });
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

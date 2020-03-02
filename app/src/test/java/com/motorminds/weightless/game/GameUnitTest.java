package com.motorminds.weightless.game;

import com.motorminds.weightless.GameEventChain;
import com.motorminds.weightless.Tile;
import com.motorminds.weightless.events.GameEventFactory;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GameUnitTest {

    // Colors
    private static int COLOR_1 = 1;
    private static int COLOR_2 = 2;
    private static int COLOR_3 = 3;
    private static int COLOR_4 = 4;

    private static GameEventFactory eventFactory;

    @Mock
    private TileGenerator tileGenerator;

    private Game game;
    private GameField field;

    @BeforeClass
    public static void initClass() {
        eventFactory = mock(GameEventFactory.class);
        when(eventFactory.multiEventBuilder(any(GameEventChain.class))).thenCallRealMethod();
    }

    @Before
    public void init() {
        this.game = new Game(eventFactory, tileGenerator, new GameField());
        this.field = game.getField();
    }

    @Test
    public void move() {
        Tile tile = makeTile(0, 5, COLOR_1);
        field.setTile(tile);

        game.moveTile(tile.cell, 3);

        assertTrue(field.hasNoTile(0, 5));
        assertEquals(tile.color, field.getTile(3, 5).color);
    }

    @Test
    public void dropFromOne() {
        Tile tile1 = makeTile(0, 4, COLOR_1);
        Tile tile2 = makeTile(0, 5, COLOR_1);

        game.moveTile(tile1.cell, 3);

        assertTrue(field.hasNoTile(0, 4));
        assertEquals(tile1.color, field.getTile(3, 5).color);
    }

    @Test
    public void dropFromTwo() {
        Tile tile1 = makeTile(0, 3, COLOR_1);
        Tile tile2 = makeTile(0, 4, COLOR_1);
        Tile tile3 = makeTile(0, 5, COLOR_1);

        game.moveTile(tile1.cell, 3);

        assertTrue(field.hasNoTile(0, 3));
        assertEquals(tile1.color, field.getTile(3, 5).color);
    }

    @Test
    public void pushTilesOfSameColor() {
        Tile tile1 = makeTile(0, 5, COLOR_1);

        Tile tile2 = makeTile(3, 5, COLOR_1);

        game.moveTile(tile1.cell, 3);

        assertTrue(field.hasNoTile(0, 5));
        assertTrue(field.hasNoTile(2, 5));
        assertTrue(field.hasNoTile(3, 5));
    }

    @Test
    public void pushTilesOfDifferentColors() {
        Tile tile1 = makeTile(0, 5, COLOR_1);

        Tile tile2 = makeTile(3, 5, COLOR_2);

        game.moveTile(tile1.cell, 3);

        assertTrue(field.hasTile(2, 5));
        assertTrue(field.hasTile(3, 5));
        assertEquals(tile1.color, field.getTile(2, 5).color);
        assertEquals(tile2, field.getTile(3, 5));
    }

    @Test
    public void moveOnTileOfSameColor() {
        Tile tile1 = makeTile(0, 4, COLOR_1);
        Tile tile2 = makeTile(0, 5, COLOR_2);

        Tile tile3 = makeTile(3, 5, COLOR_1);

        game.moveTile(tile1.cell, 3);

        assertTrue(field.hasTile(3, 4));
        assertTrue(field.hasTile(3, 5));
        assertEquals(tile1.color, field.getTile(3, 4).color);
        assertEquals(tile3, field.getTile(3, 5));
    }

    @Test
    public void dropOnTileOfSameColor() {
        Tile tile1 = makeTile(0, 3, COLOR_1);
        Tile tile2 = makeTile(0, 4, COLOR_1);
        Tile tile3 = makeTile(0, 5, COLOR_1);

        Tile tile4 = makeTile(3, 5, COLOR_1);

        game.moveTile(tile1.cell, 3);

        assertTrue(field.hasNoTile(3, 4));
        assertTrue(field.hasNoTile(3, 5));
    }


    @Test
    public void dropOnTileOfDifferentColors() {
        Tile tile1 = makeTile(0, 3, COLOR_1);
        Tile tile2 = makeTile(0, 4, COLOR_1);
        Tile tile3 = makeTile(0, 5, COLOR_1);

        Tile tile4 = makeTile(3, 5, COLOR_2);

        game.moveTile(tile1.cell, 3);

        assertEquals(tile1.color, field.getTile(3, 4).color);
        assertEquals(tile4, field.getTile(3, 5));
    }

    @Test
    public void dropOnThreeTilesOfSameColor() {
        Tile tile1 = makeTile(0, 1, COLOR_1);
        Tile tile2 = makeTile(0, 2, COLOR_1);
        Tile tile3 = makeTile(0, 3, COLOR_1);
        Tile tile4 = makeTile(0, 4, COLOR_1);
        Tile tile5 = makeTile(0, 5, COLOR_1);

        Tile tile6 = makeTile(3, 3, COLOR_1);
        Tile tile7 = makeTile(3, 4, COLOR_1);
        Tile tile8 = makeTile(3, 5, COLOR_1);

        game.moveTile(tile1.cell, 3);

        assertTrue(field.hasNoTile(3, 2));
        assertTrue(field.hasNoTile(3, 3));
        assertTrue(field.hasNoTile(3, 4));
        assertTrue(field.hasNoTile(3, 5));
    }

    @Test
    public void dropColumnOfOne() {
        Tile tile1 = makeTile(0, 4, COLOR_1);
        Tile tile2 = makeTile(0, 5, COLOR_2);

        game.moveTile(tile2.cell, 3);

        assertTrue(field.hasNoTile(0, 4));
        assertEquals(tile1.color, field.getTile(0, 5).color);
    }

    @Test
    public void dropColumnOfThreeOfDifferentColors() {
        Tile tile1 = makeTile(0, 2, COLOR_1);
        Tile tile2 = makeTile(0, 3, COLOR_2);
        Tile tile3 = makeTile(0, 4, COLOR_3);
        Tile tile4 = makeTile(0, 5, COLOR_4);

        game.moveTile(tile4.cell, 3);

        assertTrue(field.hasNoTile(0, 2));
        assertEquals(tile1.color, field.getTile(0, 3).color);
        assertEquals(tile2.color, field.getTile(0, 4).color);
        assertEquals(tile3.color, field.getTile(0, 5).color);
    }

    @Test
    public void dropColumnOfThreeOfSameColor() {
        Tile tile1 = makeTile(0, 2, COLOR_1);
        Tile tile2 = makeTile(0, 3, COLOR_1);
        Tile tile3 = makeTile(0, 4, COLOR_1);
        Tile tile4 = makeTile(0, 5, COLOR_1);

        game.moveTile(tile4.cell, 3);

        assertTrue(field.hasNoTile(0, 3));
        assertTrue(field.hasNoTile(0, 4));
        assertTrue(field.hasNoTile(0, 5));
    }

    @Test
    public void dropColumnOnTilesOfSameColor() {
        Tile tile1 = makeTile(0, 1, COLOR_1);
        Tile tile2 = makeTile(0, 2, COLOR_1);
        Tile tile3 = makeTile(0, 3, COLOR_2);
        Tile tile4 = makeTile(0, 4, COLOR_1);
        Tile tile5 = makeTile(0, 5, COLOR_1);

        game.moveTile(tile3.cell, 3);

        assertTrue(field.hasNoTile(0, 1));
        assertTrue(field.hasNoTile(0, 2));
        assertTrue(field.hasNoTile(0, 3));
        assertTrue(field.hasNoTile(0, 4));
        assertTrue(field.hasNoTile(0, 5));
    }


    @Test
    public void dropColumnThatCausesAnotherDrop() {
        Tile tile2 = makeTile(0, 1, COLOR_2);
        Tile tile3 = makeTile(0, 2, COLOR_1);
        Tile tile4 = makeTile(0, 3, COLOR_1);
        Tile tile5 = makeTile(0, 4, COLOR_2);
        Tile tile6 = makeTile(0, 5, COLOR_1);

        game.moveTile(tile6.cell, 3);

        assertTrue(field.hasNoTile(0, 1));
        assertTrue(field.hasNoTile(0, 2));
        assertTrue(field.hasNoTile(0, 3));
        assertTrue(field.hasNoTile(0, 4));
        assertTrue(field.hasNoTile(0, 5));
    }

    @Test
    public void pushCausesColumnDrop() {
        Tile tile1 = makeTile(0, 5, COLOR_1);

        Tile tile2 = makeTile(3, 4, COLOR_2);
        Tile tile3 = makeTile(3, 5, COLOR_1);

        game.moveTile(tile1.cell, 3);

        assertEquals(tile2.color, field.getTile(3, 5).color);
    }

    @Test
    public void pushSimpleToHorizontal() {
        Tile tile1 = makeTile(0, 5, COLOR_1);
        Tile tile2 = makeTile(2, 5, COLOR_1, Tile.Type.HORIZONTAL);
        Tile tile3 = makeTile(3, 5, COLOR_2);

        game.moveTile(tile1.cell, 2);

        assertTrue(field.hasNoTile(3, 5));
    }

    @Test
    public void pushHorizontalToSimple() {
        Tile tile1 = makeTile(0, 5, COLOR_1);
        Tile tile2 = makeTile(2, 5, COLOR_1, Tile.Type.HORIZONTAL);
        Tile tile3 = makeTile(3, 5, COLOR_2);

        game.moveTile(tile2.cell, 0);

        assertTrue(field.hasNoTile(3, 5));
    }

    @Test
    public void pushVertical() {
        Tile tile1 = makeTile(0, 4, COLOR_1);
        Tile tile2 = makeTile(0, 5, COLOR_1);

        Tile tile3 = makeTile(3, 3, COLOR_3);
        Tile tile4 = makeTile(3, 4, COLOR_1, Tile.Type.VERTICAL);
        Tile tile5 = makeTile(3, 5, COLOR_2);

        game.moveTile(tile1.cell, 3);

        assertTrue(field.hasNoTile(3, 3));
        assertTrue(field.hasNoTile(3, 4));
        assertTrue(field.hasNoTile(3, 5));
    }

    @Test
    public void pushBomb() {
        Tile tile1 = makeTile(0, 4, COLOR_1);
        Tile tile2 = makeTile(0, 5, COLOR_1);

        Tile tile3 = makeTile(1, 5, COLOR_1);

        Tile tile4 = makeTile(2, 3, COLOR_3);
        Tile tile5 = makeTile(2, 4, COLOR_1, Tile.Type.BOMB);
        Tile tile6 = makeTile(2, 5, COLOR_2);

        Tile tile7 = makeTile(3, 3, COLOR_1);
        Tile tile8 = makeTile(3, 4, COLOR_2);
        Tile tile9 = makeTile(3, 5, COLOR_3);

        game.moveTile(tile1.cell, 2);

        assertTrue(field.hasNoTile(1, 5));
        assertTrue(field.hasNoTile(2, 5));
        assertTrue(field.hasNoTile(3, 5));
    }

    @Test
    public void actionCausesAnotherAction() {
        Tile tile1 = makeTile(0, 4, COLOR_1);
        Tile tile2 = makeTile(0, 5, COLOR_1);

        Tile tile3 = makeTile(3, 4, COLOR_1, Tile.Type.VERTICAL);
        Tile tile4 = makeTile(3, 5, COLOR_2, Tile.Type.HORIZONTAL);

        game.moveTile(tile1.cell, 3);

        assertTrue(field.hasNoTile(0, 5));
    }

    private Tile makeTile(int x, int y, int color) {
        return makeTile(x, y, color, Tile.Type.SIMPLE);
    }

    private Tile makeTile(int x, int y, int color, Tile.Type type) {
        Tile tile = new Tile(x, y, color, type);
        field.setTile(tile);
        return tile;
    }
}
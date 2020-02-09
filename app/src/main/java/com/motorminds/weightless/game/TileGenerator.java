package com.motorminds.weightless.game;

import com.motorminds.weightless.Tile;

public interface TileGenerator {
    void initField();
    Tile generate();
}

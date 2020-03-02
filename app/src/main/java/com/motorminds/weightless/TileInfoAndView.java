package com.motorminds.weightless;

import com.motorminds.weightless.view.TileView;

public class TileInfoAndView {
    public final int color;
    public final Tile.Type type;
    public final TileView view;

    public TileInfoAndView(int color, Tile.Type type, TileView view) {
        this.color = color;
        this.type = type;
        this.view = view;
    }
}

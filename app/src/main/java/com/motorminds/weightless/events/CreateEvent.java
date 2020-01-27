package com.motorminds.weightless.events;

import android.animation.Animator;

import com.motorminds.weightless.GameContract;
import com.motorminds.weightless.Tile;

public class CreateEvent extends AbstractCellEvent {
    private final Tile tile;

    CreateEvent(GameContract.View view, Tile tile) {
        super(view);
        this.tile = tile;
    }

    @Override
    public Animator getAnimator() {
        return view.createTile(tile);
    }

    @Override
    public String toString() {
        return "Create{" + tile + "}";
    }
}

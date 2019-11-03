package com.motorminds.weightless.events;

import android.animation.Animator;

import com.motorminds.weightless.GameContract;
import com.motorminds.weightless.Tile;

public class CreateEvent extends AbstractCellEvent {
    private final Tile tile;

    public CreateEvent(GameContract.View view, Tile tile) {
        super(view);
        this.tile = tile;
    }

    @Override
    public Animator getEventAnimator() {
        return view.createTile(tile);
    }

    @Override
    public String toString() {
        return "CreateEvent{" +
                "tile=" + tile +
                "} " + super.toString();
    }
}

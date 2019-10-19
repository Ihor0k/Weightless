package com.motorminds.weightless.events;

import android.animation.Animator;

import com.motorminds.weightless.Cell;
import com.motorminds.weightless.GameContract;

public class CreateEvent extends AbstractCellEvent {
    private final Cell cell;
    private final int color;

    public CreateEvent(GameContract.View view, Cell cell, int value) {
        super(view);
        this.cell = cell;
        this.color = value;
    }

    @Override
    public Animator getEventAnimator() {
        return view.createTile(cell, color);
    }

    @Override
    public String toString() {
        return "CreateEvent{" +
                "cell=" + cell +
                ", color=" + color +
                ", " + super.toString() +
                '}' ;
    }
}

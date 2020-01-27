package com.motorminds.weightless.events;

import android.animation.Animator;

import com.motorminds.weightless.Cell;
import com.motorminds.weightless.GameContract;

public class RemoveEvent extends AbstractCellEvent {
    private final Cell cell;

    RemoveEvent(GameContract.View view, Cell cell) {
        super(view);
        this.cell = cell;
    }

    @Override
    public Animator getAnimator() {
        return view.removeTile(cell);
    }

    @Override
    public String toString() {
        return "Remove{" + cell + '}';
    }
}

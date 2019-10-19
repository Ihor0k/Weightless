package com.motorminds.weightless.events;

import android.animation.Animator;

import com.motorminds.weightless.Cell;
import com.motorminds.weightless.GameContract;

public class RemoveEvent extends AbstractCellEvent {
    private final Cell cell;

    public RemoveEvent(GameContract.View view, Cell cell) {
        super(view);
        this.cell = cell;
    }

    @Override
    public Animator getEventAnimator() {
        return view.removeTile(cell);
    }

    @Override
    public String toString() {
        return "RemoveEvent{" +
                "cell=" + cell +
                ", " + super.toString() +
                '}';
    }
}

package com.motorminds.weightless.events;

import android.animation.Animator;

import com.motorminds.weightless.Cell;
import com.motorminds.weightless.GameContract;

public class MoveEvent extends AbstractCellEvent {
    private final Cell from;
    private final Cell to;

    MoveEvent(GameContract.View view, Cell from, Cell to, EventAction action) {
        super(view, action);
        this.from = from;
        this.to = to;
    }

    @Override
    public Animator getEventAnimator() {
        return view.moveTile(from, to);
    }

    @Override
    public String toString() {
        return "Move{" + from + "=>" + to + '}';
    }
}

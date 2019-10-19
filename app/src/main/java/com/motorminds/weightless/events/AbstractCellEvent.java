package com.motorminds.weightless.events;

import com.motorminds.weightless.GameContract;

public abstract class AbstractCellEvent extends AbstractGameEvent {
    protected final GameContract.View view;

    public AbstractCellEvent(GameContract.View view) {
        this.view = view;
    }
}

package com.motorminds.weightless.events;

import com.motorminds.weightless.GameContract;

public abstract class AbstractCellEvent implements GameEvent {
    protected final GameContract.View view;

    AbstractCellEvent(GameContract.View view) {
        this.view = view;
    }
}

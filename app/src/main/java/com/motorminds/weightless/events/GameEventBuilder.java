package com.motorminds.weightless.events;

import android.widget.TextView;

import com.motorminds.weightless.Cell;
import com.motorminds.weightless.GameContract;

public class GameEventBuilder {
    private GameContract.View view;
    private TextView scoreView;

    public GameEventBuilder(GameContract.View view, TextView scoreView) {
        this.view = view;
        this.scoreView = scoreView;
    }

    public GameEvent onMove(Cell from, Cell to) {
        return new MoveEvent(view, from, to);
    }

    public GameEvent onCreate(Cell cell, int value) {
        return new CreateEvent(view, cell, value);
    }

    public GameEvent onRemove(Cell cell) {
        return new RemoveEvent(view, cell);
    }

    public GameEvent onMultiEvents(GameEvent... events) {
        return new MultiEvent(view, events);
    }

    public GameEvent nullEvent() {
        return new MultiEvent(view);
    }

    public GameEvent onScoreEvent(int score) {
        return new ScoreEvent(scoreView, score);
    }
}

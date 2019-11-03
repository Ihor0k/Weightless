package com.motorminds.weightless.events;

import android.widget.TextView;

import com.motorminds.weightless.Cell;
import com.motorminds.weightless.GameContract;
import com.motorminds.weightless.Tile;

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

    public GameEvent onCreate(Tile tile) {
        return new CreateEvent(view, tile);
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

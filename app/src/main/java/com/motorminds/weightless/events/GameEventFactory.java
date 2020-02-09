package com.motorminds.weightless.events;

import android.widget.TextView;

import com.motorminds.weightless.Cell;
import com.motorminds.weightless.GameContract;
import com.motorminds.weightless.GameEventChain;
import com.motorminds.weightless.Tile;

import java.util.ArrayList;
import java.util.List;

public class GameEventFactory {
    private GameContract.View view;
    private TextView scoreView;

    public GameEventFactory(GameContract.View view, TextView scoreView) {
        this.view = view;
        this.scoreView = scoreView;
    }

    public GameEvent move(Cell from, Cell to) {
        return new MoveEvent(view, from, to);
    }

    public GameEvent create(Tile tile) {
        return new CreateEvent(view, tile);
    }

    public GameEvent remove(Cell cell) {
        return new RemoveEvent(view, cell);
    }

    public GameEvent score(int score) {
        return new ScoreEvent(scoreView, score);
    }

    public MultiEventBuilder multiEventBuilder(GameEventChain eventChain) {
        return new MultiEventBuilder(eventChain);
    }

    public static class MultiEventBuilder {
        private List<GameEvent> events;
        private GameEventChain eventChain;

        private MultiEventBuilder(GameEventChain eventChain) {
            this.events = new ArrayList<>();
            this.eventChain = eventChain;
        }

        public void add(GameEvent event) {
            if (event != null) {
                events.add(event);
            }
        }

        public GameEvent playTogether() {
            return eventChain.playTogether(events.toArray(new GameEvent[0]));
        }

        public GameEvent playSequentially() {
            return eventChain.playSequentially(events.toArray(new GameEvent[0]));
        }
    }
}

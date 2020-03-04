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

    public GameEvent move(Cell from, Cell to, EventAction action) {
        return new MoveEvent(view, from, to, action);
    }

    public GameEvent create(Tile tile, EventAction action) {
        return new CreateEvent(view, tile, action);
    }

    public GameEvent remove(Cell cell, EventAction action) {
        return new RemoveEvent(view, cell, action);
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

        public void add(EventProducer producer) {
            GameEvent event = producer.get();
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

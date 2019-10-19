package com.motorminds.weightless.events;

import android.animation.Animator;
import android.animation.AnimatorSet;

import com.motorminds.weightless.GameContract;

import java.util.Arrays;

public class MultiEvent extends AbstractCellEvent {
    private final GameEvent[] events;

    public MultiEvent(GameContract.View view, GameEvent... events) {
        super(view);
        this.events = events;
    }

    @Override
    public Animator getEventAnimator() {
        AnimatorSet animatorSet = new AnimatorSet();
        if (events.length > 0) {
            Animator[] animators = new Animator[events.length];
            for (int i = 0; i < events.length; i++) {
                animators[i] = events[i].getAnimator();
            }
            animatorSet.playTogether(animators);
        }
        return animatorSet;
    }

    @Override
    public String toString() {
        return "MultiEvent{" +
                "events=" + Arrays.toString(events) +
                ", " + super.toString() +
                '}';
    }
}

package com.motorminds.weightless.events;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

import com.motorminds.weightless.GameContract;

public abstract class AbstractCellEvent implements GameEvent {
    private final EventAction action;
    protected final GameContract.View view;

    AbstractCellEvent(GameContract.View view, EventAction action) {
        this.action = action;
        this.view = view;
    }

    protected abstract Animator getEventAnimator();

    @Override
    public Animator getAnimator() {
        Animator animator = getEventAnimator();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                action.execute();
            }
        });
        return animator;
    }
}

package com.motorminds.weightless.events;

import android.animation.Animator;
import android.animation.AnimatorSet;

import com.motorminds.weightless.GameContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractGameEvent implements GameEvent {
    private List<GameEvent> withEvents;
    private List<GameEvent> beforeEvents;

    public AbstractGameEvent() {
        this.withEvents = new ArrayList<>();
        this.beforeEvents = new ArrayList<>();
    }

    @Override
    public void withEvent(GameEvent event) {
        withEvents.add(event);
    }

    @Override
    public void withEvents(GameEvent... events) {
        withEvents.addAll(Arrays.asList(events));
    }

    @Override
    public void beforeEvent(GameEvent event) {
        beforeEvents.add(event);
    }

    @Override
    public Animator getAnimator() {
        Animator animator = getEventAnimator();

        List<Animator> animators = new ArrayList<>(3);
        animators.add(animator);
        if (!withEvents.isEmpty()) {
            AnimatorSet childAnimatorSet = new AnimatorSet();
            Animator[] childAnimators = getChildAnimators(withEvents);
            childAnimatorSet.playTogether(childAnimators);

            animators.add(childAnimatorSet);
        }
        if (!beforeEvents.isEmpty()) {
            AnimatorSet childAnimatorSet = new AnimatorSet();
            Animator[] childAnimators = getChildAnimators(beforeEvents);
            childAnimatorSet.playSequentially(childAnimators);

            animators.add(childAnimatorSet);
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(animators);
        return animatorSet;
    }

    private Animator[] getChildAnimators(List<GameEvent> events) {
        Animator[] childAnimators = new Animator[events.size()];
        for (int i = 0; i < events.size(); i++) {
            childAnimators[i] = events.get(i).getAnimator();
        }
        return childAnimators;
    }

    protected abstract Animator getEventAnimator();

    @Override
    public String toString() {
        return "withEvents=" + withEvents +
                ", beforeEvents=" + beforeEvents;
    }
}

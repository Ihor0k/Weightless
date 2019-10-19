package com.motorminds.weightless.events;

import android.animation.Animator;

public interface GameEvent {
    Animator getAnimator();
    void withEvent(GameEvent event);
    void withEvents(GameEvent... events);
    void beforeEvent(GameEvent event);
}

package com.motorminds.weightless.events;

@FunctionalInterface
public interface EventProducer {
    GameEvent get();
}

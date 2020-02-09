package com.motorminds.weightless;

import android.animation.Animator;
import android.animation.AnimatorSet;

import com.motorminds.weightless.events.GameEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameEventChain {
    private Map<GameEvent, Node> eventNodeMap;
    private Node rootNode;

    public GameEventChain(GameEvent rootEvent) {
        this.eventNodeMap = new HashMap<>();
        this.rootNode = registerEvent(rootEvent);
    }

    public GameEvent playTogether(GameEvent... events) {
        Node node = new Node();
        for (GameEvent event : events) {
            Node eventNode = registerEvent(event);
            merge(node, eventNode);
        }
        for (GameEvent event : events) {
            if (event != null) {
                node.events.add(event);
            }
        }
        return firstNonNull(events);
    }

    public GameEvent playSequentially(GameEvent... events) {
        if (events.length == 0) {
            return null;
        }
        Node parentNode = registerEvent(events[0]);
        for (int i = 1; i < events.length; i++) {
            Node node = registerEvent(events[i]);
            if (parentNode.childNode == null) {
                parentNode.childNode = node;
            } else {
                merge(parentNode.childNode, node);
            }
            parentNode = node;
        }
        return firstNonNull(events);
    }

    public void append(GameEvent appendTo, GameEvent event) {
        Node parentNode = registerEvent(appendTo);
        append(parentNode, event);
    }

    private GameEvent firstNonNull(GameEvent... events) {
        for (GameEvent event : events) {
            if (event != null) {
                return event;
            }
        }
        return null;
    }

    private void append(Node node, GameEvent event) {
        if (node.childNode == null) {
            node.childNode = registerEvent(event);
        } else {
            append(node.childNode, event);
        }
    }

    private Node registerEvent(GameEvent event) {
        if (event == null) {
            return new Node();
        }
        Node node = eventNodeMap.get(event);
        if (node == null) {
            node = new Node();
            node.events.add(event);
        }
        eventNodeMap.put(event, node);
        return node;
    }

    private void merge(Node mainNode, Node subNode) {
        mainNode.events.addAll(subNode.events);
        for (GameEvent event : subNode.events) {
            eventNodeMap.put(event, mainNode);
        }
        if (mainNode.childNode == null) {
            mainNode.childNode = subNode.childNode;
        } else if (subNode.childNode != null) {
            merge(mainNode.childNode, subNode.childNode);
        }
    }

    public Animator getAnimator() {
        List<Animator> animatorSets = new ArrayList<>();
        collectAnimators(rootNode, animatorSets);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(animatorSets);
        return animatorSet;
    }

    private void collectAnimators(Node node, List<Animator> animatorSets) {
        if (node == null) return;
        Set<GameEvent> events = node.events;
        if (!events.isEmpty()) {
            List<Animator> animators = new ArrayList<>(events.size());
            for (GameEvent event : events) {
                animators.add(event.getAnimator());
            }
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(animators);
            animatorSets.add(animatorSet);
        }
        collectAnimators(node.childNode, animatorSets);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameEventChain that = (GameEventChain) o;
        return rootNode.equals(that.rootNode);
    }

    @Override
    public int hashCode() {
        return rootNode.hashCode();
    }

    private static class Node {
        private Set<GameEvent> events;
        private Node childNode;

        Node() {
            this.events = new LinkedHashSet<>();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return events.equals(node.events) &&
                    childNode.equals(node.childNode);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(new Object[]{events, childNode});
        }
    }
}

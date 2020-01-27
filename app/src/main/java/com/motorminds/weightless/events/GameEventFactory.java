package com.motorminds.weightless.events;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.widget.TextView;

import com.motorminds.weightless.Cell;
import com.motorminds.weightless.GameContract;
import com.motorminds.weightless.Tile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameEventFactory {
    private GameContract.View view;
    private TextView scoreView;

    public GameEventFactory(GameContract.View view, TextView scoreView) {
        this.view = view;
        this.scoreView = scoreView;
    }

    public GameEventBuilder builder(GameEvent rootEvent) {
        return new GameEventBuilder(rootEvent);
    }

    private static class Node {
        private Set<GameEvent> events;
        private Node childNode;

        Node() {
            this.events = new LinkedHashSet<>();
        }
    }

    public static class GameEventBuilder {
        private Map<GameEvent, Node> eventNodeMap;
        private GameEvent rootEvent;

        private GameEventBuilder(GameEvent rootEvent) {
            this.eventNodeMap = new HashMap<>();
            this.rootEvent = rootEvent;
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

        private Animator getAnimator() {
            Node rootNode = registerEvent(rootEvent);
            List<Animator> animatorSets = new ArrayList<>();
            collectAnimators(rootNode, animatorSets);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playSequentially(animatorSets);
            return animatorSet;
        }

        public GameEvent build() {
            return GameEventBuilder.this::getAnimator;
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

    public MultiEventBuilder multiEventBuilder(GameEventBuilder builder) {
        return new MultiEventBuilder(builder);
    }

    public GameEvent score(int score) {
        return new ScoreEvent(scoreView, score);
    }

    public static class MultiEventBuilder {
        private List<GameEvent> events;
        private GameEventBuilder builder;

        private MultiEventBuilder(GameEventBuilder builder) {
            this.events = new ArrayList<>();
            this.builder = builder;
        }

        public void add(GameEvent event) {
            if (event != null) {
                events.add(event);
            }
        }

        public GameEvent build() {
            return builder.playTogether(events.toArray(new GameEvent[0]));
        }
    }
}

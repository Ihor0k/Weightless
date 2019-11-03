package com.motorminds.weightless;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.widget.TextView;

import com.motorminds.weightless.events.GameEvent;
import com.motorminds.weightless.events.GameEventBuilder;
import com.motorminds.weightless.game.ColorGenerator;
import com.motorminds.weightless.game.Game;
import com.motorminds.weightless.game.GameField;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class GamePresenter implements GameContract.Presenter {
    private TextView scoreView;
    private Game game;
    private GameContract.View view;
    private GameEventBuilder eventBuilder;
    private ColorGenerator colorGenerator;
    private SharedPreferences preferences;

    public GamePresenter(Context context, GameContract.View view, TextView scoreView, SharedPreferences preferences) {
        this.preferences = preferences;
        this.eventBuilder = new GameEventBuilder(view, scoreView);
        this.colorGenerator = new ColorGeneratorImpl(context);
        this.game = deserializeGame(preferences);
        this.view = view;
        this.scoreView = scoreView;
        view.setPresenter(this);
        initView();
    }

    @Override
    public Cell wantToMove(Cell cell, int toColumn) {
        GameField field = game.getField();
        int x = cell.x;
        int y = cell.y;
        int dir = x < toColumn ? 1 : -1;
        if (x == toColumn || field.hasTile(x + dir, y)) {
            return null;
        }
        Tile tile = field.getTile(x, y);
        int newX = x + dir;
        for (int i = newX; i != toColumn + dir; i += dir) {
            Tile toTile = field.getTile(i, y);
            if (toTile == null) {
                System.out.println(i + " -> continue");
                newX = i;
                continue;
            } else if (toTile.color == tile.color) {
                newX = i;
            }
            break;
        }
        return new Cell(newX, y);
    }

    @Override
    public void moveTile(Cell from, Cell to) {
        GameEvent event = game.move(from, to.x);
        animateEvent(event);
    }

    @Override
    public void createTile(Tile tile) {
        GameEvent event = game.create(tile);
        animateEvent(event);
    }

    @Override
    public void serialize() {
        SharedPreferences.Editor editor = this.preferences.edit();
        GameField field = game.getField();
        int score = game.getScore();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(field);
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.putString("field", Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT));
        editor.putInt("score", score);
        editor.apply();
    }

    @Override
    public void restart() {
        this.game = new Game(this.eventBuilder, this.colorGenerator);
        this.preferences.edit().clear().apply();
        initView();
    }

    private void animateEvent(GameEvent event) {
        Animator animator = event.getAnimator();
        animator.setDuration(100);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.disable();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.enable();
            }
        });
        animator.start();
    }

    private void initView() {
        this.view.init(game.getField());
        scoreView.setText(String.valueOf(game.getScore()));
    }

    private Game deserializeGame(SharedPreferences preferences) {
        String fieldString = preferences.getString("field", null);
        if (fieldString != null) {
            byte[] fieldBytes = Base64.decode(fieldString, Base64.DEFAULT);
            ByteArrayInputStream bais = new ByteArrayInputStream(fieldBytes);
            try (ObjectInputStream ois = new ObjectInputStream(bais)) {
                GameField field = (GameField) ois.readObject();
                int score = preferences.getInt("score", 0);
                return new Game(eventBuilder, colorGenerator, field, score);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return new Game(eventBuilder, colorGenerator);
            }
        } else {
            return new Game(eventBuilder, colorGenerator);
        }
    }
}

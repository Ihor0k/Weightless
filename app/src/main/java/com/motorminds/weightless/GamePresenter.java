package com.motorminds.weightless;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.widget.TextView;

import com.motorminds.weightless.events.GameEvent;
import com.motorminds.weightless.events.GameEventFactory;
import com.motorminds.weightless.game.ColorGenerator;
import com.motorminds.weightless.game.Game;
import com.motorminds.weightless.game.GameField;
import com.motorminds.weightless.view.GameOverPopup;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class GamePresenter implements GameContract.Presenter {
    private TextView scoreView;
    private Game game;
    private GameContract.View view;
    private GameEventFactory eventFactory;
    private ColorGenerator colorGenerator;
    private GameOverPopup gameOverPopup;
    private SharedPreferences preferences;

    public GamePresenter(Context context, GameContract.View view, TextView scoreView, GameOverPopup gameOverPopup, SharedPreferences preferences) {
        this.preferences = preferences;
        this.eventFactory = new GameEventFactory(view, scoreView);
        this.colorGenerator = new ColorGeneratorImpl(context);
        this.game = deserializeGame(preferences);
        this.view = view;
        this.scoreView = scoreView;
        this.gameOverPopup = gameOverPopup;
        view.setPresenter(this);
        initView();
    }

    @Override
    public Cell wantToMove(Cell cell, int toColumn) {
        int newX = game.wantToMove(cell, toColumn);
        if (cell.x == newX) return null;
        return new Cell(newX, cell.y);
    }

    @Override
    public void moveTile(Cell from, Cell to) {
        GameEvent event = game.moveTile(from, to.x);
        animateEvent(event);
        if (game.isGameOver()) {
            gameOverPopup.show(game.getScore());
        }
    }

    @Override
    public Cell wantToCreate(int column) {
        GameField field = game.getField();
        for (int y = field.ROWS_COUNT - 1; y >= 0; y--) {
            if (field.hasNoTile(column, y)) {
                return new Cell(column, y);
            }
        }
        return null;
    }

    @Override
    public void createTile(Tile tile) {
        GameEvent event = game.createTile(tile);
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
        gameOverPopup.hide();
        this.game = new Game(this.eventFactory, this.colorGenerator);
        this.preferences.edit().clear().apply();
        initView();
    }

    private void animateEvent(GameEvent event) {
        if (event == null) return;
        Animator animator = event.getAnimator();
        animator.setDuration(150);
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
                return new Game(eventFactory, colorGenerator, field, score);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return new Game(eventFactory, colorGenerator);
            }
        } else {
            return new Game(eventFactory, colorGenerator);
        }
    }
}

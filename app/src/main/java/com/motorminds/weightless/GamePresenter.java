package com.motorminds.weightless;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.motorminds.weightless.events.GameEvent;
import com.motorminds.weightless.events.GameEventFactory;
import com.motorminds.weightless.game.Game;
import com.motorminds.weightless.game.GameField;
import com.motorminds.weightless.game.TileGeneratorImpl;
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
    private int[] colorPalette;
    private GameOverPopup gameOverPopup;
    private SharedPreferences preferences;

    public GamePresenter(Context context, GameContract.View view, TextView scoreView, GameOverPopup gameOverPopup, SharedPreferences preferences) {
        this.preferences = preferences;
        this.eventFactory = new GameEventFactory(view, scoreView);
        int[] colorPalette = new int[]{
                ContextCompat.getColor(context, R.color.cellYellow),
                ContextCompat.getColor(context, R.color.cellRed),
                ContextCompat.getColor(context, R.color.cellGreen),
                ContextCompat.getColor(context, R.color.cellWhite)
        };
        this.colorPalette = colorPalette;
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
        GameEventChain eventChain = game.moveTile(from, to.x);
        if (eventChain != null) {
            animate(eventChain.getAnimator());
        }
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
        if (event != null) {
            animate(event.getAnimator());
        }
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
        this.game = newGame();
        this.preferences.edit().clear().apply();
        initView();
    }

    private void animate(Animator animator) {
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
                TileGeneratorImpl tileGenerator = new TileGeneratorImpl(field, colorPalette);
                return new Game(eventFactory, tileGenerator, field, score);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return newGame();
            }
        } else {
            return newGame();
        }
    }

    private Game newGame() {
        GameField field = new GameField();
        TileGeneratorImpl tileGenerator = new TileGeneratorImpl(field, colorPalette);
        tileGenerator.initField();
        return new Game(eventFactory, tileGenerator, field);
    }
}

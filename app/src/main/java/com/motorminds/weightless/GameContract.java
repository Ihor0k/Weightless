package com.motorminds.weightless;

import android.animation.Animator;

import com.motorminds.weightless.game.GameField;

public interface GameContract {
    interface View {
        void init(GameField field);

        Animator createTile(Tile tile);

        Animator moveTile(Cell from, Cell to);

        Animator removeTile(Cell cell);

        void setPresenter(Presenter presenter);

        void enable();

        void disable();
    }

    interface Presenter {
        Cell wantToMove(Cell cell, int toColumn);

        void moveTile(Cell from, Cell to);

        void createTile(Tile tile);

        void serialize();

        void restart();
    }
}

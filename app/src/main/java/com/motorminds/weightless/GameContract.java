package com.motorminds.weightless;

import android.animation.Animator;

public interface GameContract {
    interface View {
        void init(Tile[][] field);

        Animator createTile(Cell cell, int color);

        Animator moveTile(Cell from, Cell to);

        Animator removeTile(Cell cell);

        void setPresenter(Presenter presenter);

        void enable();

        void disable();
    }

    interface Presenter {
        //      MoveToCell wantToMove(Cell cell, int toColumn);
        Cell wantToMove(Cell cell, int toColumn);

        void moveTile(Cell from, Cell to);

        void createTile(Cell cell, int color);

        void serialize();

        void restart();
    }
}

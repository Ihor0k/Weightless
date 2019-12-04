package com.motorminds.weightless.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.motorminds.weightless.R;

public class GameOverPopup extends PopupWindow {
    private TextView scoreView;
    private View parent;

    public GameOverPopup(Context context, View parent) {
        super(context);
        this.parent = parent;
        LayoutInflater inflater = LayoutInflater.from(context);
        View gameOverView = inflater.inflate(R.layout.game_over, null);
        this.scoreView = gameOverView.findViewById(R.id.game_over_score);
        setContentView(gameOverView);
    }

    public void hide() {
        dismiss();
        parent.setAlpha(1);
    }

    public void show(int score) {
        parent.setAlpha(0.5F);
        scoreView.setText(String.valueOf(score));
        showAtLocation(parent, Gravity.CENTER, 0, 0);
    }
}

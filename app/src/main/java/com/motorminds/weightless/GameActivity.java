package com.motorminds.weightless;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.motorminds.weightless.view.GameOverPopup;
import com.motorminds.weightless.view.GameView;

public class GameActivity extends AppCompatActivity {
    private GameContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove notification bar
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        GameView gameView = new GameView(this);
        setContentView(gameView);

        GameContract.View boardView = findViewById(R.id.board);
        TextView scoreView = findViewById(R.id.score);
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        GameOverPopup gameOverPopup = new GameOverPopup(this, gameView);
        this.presenter = new GamePresenter(this, boardView, scoreView, gameOverPopup, preferences);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.presenter.serialize();
    }

    public void onRestart(View view) {
        this.presenter.restart();
    }
}

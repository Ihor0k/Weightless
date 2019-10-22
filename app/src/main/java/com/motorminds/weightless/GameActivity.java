package com.motorminds.weightless;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {
    private GameContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove notification bar
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_game);

        GameContract.View boardView = findViewById(R.id.board);
        TextView scoreView = findViewById(R.id.score);
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        this.presenter = new GamePresenter(this, boardView, scoreView, preferences);
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

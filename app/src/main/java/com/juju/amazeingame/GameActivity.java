package com.juju.amazeingame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

public class GameActivity extends AppCompatActivity {
    final private String GAME_FRAGMENT_TAG = "GAME_FRAGMENT";

    private GameFragment mDialog = null;
    private GameView mView = null;
    private GameEngine mEngine = null;
    private Player mPlayer = null;
    private Maze mMaze = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Block.setContext(this);
        mPlayer = new Player(this);
        mMaze = new Maze();
        mView = findViewById(R.id.gameView);
        mView.setData(mMaze, mPlayer);
        mEngine = new GameEngine(this, mPlayer, mMaze);
        mDialog = new GameFragment();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEngine.start();
    }

    @Override
    protected void onPause() {
        mEngine.stop();
        super.onPause();
    }

    public void restart(boolean newMaze) {
        mEngine.restart(newMaze);
    }

    public void showDialog() {
        mDialog.updateDeathCount(mEngine.getDeathCount());
        mDialog.show(getSupportFragmentManager(), GAME_FRAGMENT_TAG);
    }
}

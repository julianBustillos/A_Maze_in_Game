package com.juju.amazeingame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    final private String RULES_FRAGMENT_TAG = "RULES_FRAGMENT";

    private int mCountBackPressed = 0;
    private Toast mQuitToast = null;
    private Button mPlayButton = null;
    private Button mRulesButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mQuitToast = Toast.makeText(this, getResources().getString(R.string.mainActivityQuit), Toast.LENGTH_SHORT);
        mPlayButton = findViewById(R.id.playButton);
        mPlayButton.setOnClickListener(this);
        mRulesButton = findViewById(R.id.rulesButton);
        mRulesButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mCountBackPressed = 0;
        switch (v.getId()) {
            case R.id.playButton:
                Intent i = new Intent(this, GameActivity.class);
                startActivity(i);
                break;
            case R.id.rulesButton:
                RulesFragment rulesFragment = new RulesFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag(RULES_FRAGMENT_TAG);
                if (prev == null) {
                    fragmentTransaction.addToBackStack(null);
                    rulesFragment.show(fragmentTransaction, RULES_FRAGMENT_TAG);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mCountBackPressed == 1) {
            mQuitToast.cancel();
            finish();
        }
        mCountBackPressed++;
        mQuitToast.show();
    }
}

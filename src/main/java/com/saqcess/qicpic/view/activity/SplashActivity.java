package com.saqcess.qicpic.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.saqcess.qicpic.R;
import com.saqcess.qicpic.app.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {
    private static final long SPLASH_TIME_MS = 2000;
    private Handler handler = new Handler();
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }
            session = new SessionManager(getApplicationContext());
        handler.postDelayed(runnable, SPLASH_TIME_MS);

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (session.isLoggedIn()) {
                // User is already logged in. Take him to main activity
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }else{
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }

        }
    };
}
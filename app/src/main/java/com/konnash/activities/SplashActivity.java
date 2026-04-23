package com.konnash.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.konnash.R;
import com.konnash.utils.PrefsManager;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY_MS = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(this::navigateNext, SPLASH_DELAY_MS);
    }

    private void navigateNext() {
        PrefsManager prefs = PrefsManager.getInstance(this);

        Intent intent;
        if (!prefs.isOnboarded()) {
            // First launch → language selection
            intent = new Intent(this, LanguageActivity.class);
        } else {
            // Already onboarded → go to main
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}

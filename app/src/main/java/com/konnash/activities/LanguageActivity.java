package com.konnash.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.konnash.R;
import com.konnash.utils.PrefsManager;

public class LanguageActivity extends AppCompatActivity {

    private Button btnArabic, btnEnglish, btnFrench, btnTurkish, btnConfirm;
    private String selectedLanguage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        btnArabic  = findViewById(R.id.btn_arabic);
        btnEnglish = findViewById(R.id.btn_english);
        btnFrench  = findViewById(R.id.btn_french);
        btnTurkish = findViewById(R.id.btn_turkish);
        btnConfirm = findViewById(R.id.btn_confirm);

        setupLanguageButton(btnArabic,  "ar",  true);
        setupLanguageButton(btnEnglish, "en",  false);
        setupLanguageButton(btnFrench,  "fr",  false);
        setupLanguageButton(btnTurkish, "tr",  false);

        btnConfirm.setOnClickListener(v -> {
            if ("ar".equals(selectedLanguage)) {
                PrefsManager.getInstance(this).setLanguage(selectedLanguage);
                startActivity(new Intent(this, OnboardingActivity.class));
                finish();
            }
        });
    }

    private void setupLanguageButton(Button btn, String langCode, boolean available) {
        btn.setOnClickListener(v -> {
            selectedLanguage = langCode;
            highlightSelected(btn);
            btnConfirm.setVisibility(View.VISIBLE);

            if (available) {
                btnConfirm.setText(getString(R.string.confirm));
                btnConfirm.setAlpha(1f);
            } else {
                btnConfirm.setText(getString(R.string.language_unavailable));
                btnConfirm.setAlpha(0.6f);
            }
        });
    }

    private void highlightSelected(Button selected) {
        Button[] all = {btnArabic, btnEnglish, btnFrench, btnTurkish};
        for (Button b : all) {
            if (b == selected) {
                b.setBackgroundResource(R.drawable.bg_language_button_selected);
                b.setTextColor(ContextCompat.getColor(this, R.color.white));
            } else {
                b.setBackgroundResource(R.drawable.bg_language_button);
                b.setTextColor(ContextCompat.getColor(this, R.color.primary_blue));
            }
        }
    }
}
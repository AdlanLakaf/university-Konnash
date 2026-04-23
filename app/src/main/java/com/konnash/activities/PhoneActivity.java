package com.konnash.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.konnash.R;
import com.konnash.utils.PrefsManager;

public class PhoneActivity extends AppCompatActivity {

    private EditText etPhone;
    private Button   btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        etPhone     = findViewById(R.id.et_phone);
        btnContinue = findViewById(R.id.btn_continue);

        btnContinue.setOnClickListener(v -> {
            String phone = etPhone.getText().toString().trim();
            if (TextUtils.isEmpty(phone) || phone.length() < 9) {
                Toast.makeText(this,
                        getString(R.string.error_invalid_phone),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            // Save phone & mark onboarded
            PrefsManager prefs = PrefsManager.getInstance(this);
            prefs.setPhone(phone);
            prefs.setOnboarded(true);

            // Go to cash book onboarding slide
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}

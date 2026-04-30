package com.konnash.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.konnash.R;

/**
 * First screen for adding a client.
 * Presents two options:
 *   1. Add new client manually → opens AddClientActivity
 *   2. Import from contacts    → button visible, no logic yet
 */
public class AddClientEntryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client_entry);

        // Back
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // Add new client manually → open the form
        findViewById(R.id.btn_add_new_client).setOnClickListener(v -> {
            Intent intent = new Intent(this, AddClientActivity.class);
            startActivity(intent);
        });

        // Import contacts — button visible, logic deferred
        // (no action attached intentionally)
    }
}

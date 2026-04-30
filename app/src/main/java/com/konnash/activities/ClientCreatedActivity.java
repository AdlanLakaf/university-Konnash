package com.konnash.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.konnash.R;

/**
 * Client created success screen.
 * UI only — no business logic.
 * "Done" button pops back to the credit book.
 * "Add another" re-opens the entry screen.
 */
public class ClientCreatedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_created);

        // Back arrow
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // Done — go back to credit book (clear the add-client stack)
        findViewById(R.id.btn_done).setOnClickListener(v -> {
            // Navigate back to MainActivity (credit book tab)
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Add another client
        findViewById(R.id.tv_add_another).setOnClickListener(v -> {
            Intent intent = new Intent(this, AddClientEntryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }
}

package com.konnash.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.konnash.R;
import com.konnash.database.DatabaseHelper;
import com.konnash.models.Tag;

/**
 * Simple form to create a new tag.
 * Fields: name (+ color swatch placeholder — color fixed for now).
 * On confirm: inserts tag into DB and returns RESULT_OK.
 */
public class AddTagActivity extends AppCompatActivity {

    private EditText etTagName;
    private String selectedColor = "#9B59B6"; // default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tag);

        View colorSwatch = findViewById(R.id.view_color_swatch);
        colorSwatch.setBackgroundColor(android.graphics.Color.parseColor(selectedColor));

        colorSwatch.setOnClickListener(v -> {
            ColorPickerBottomSheet sheet = new ColorPickerBottomSheet();
            sheet.setOnColorSelectedListener(hex -> {
                selectedColor = hex;
                colorSwatch.setBackgroundColor(android.graphics.Color.parseColor(hex));
            });
            sheet.show(getSupportFragmentManager(), "color_picker");
        });


        etTagName = findViewById(R.id.et_tag_name);

        // Close without saving
        findViewById(R.id.btn_close).setOnClickListener(v -> finish());

        // Confirm
        findViewById(R.id.btn_confirm).setOnClickListener(v -> saveTag());
    }

    private void saveTag() {
        String name = etTagName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_empty_name), Toast.LENGTH_SHORT).show();
            return;
        }

        // Default color — can be expanded later with a color picker
        Tag tag = new Tag(name, "#7BAED6");
        DatabaseHelper.getInstance(this).insertTag(tag);

        setResult(RESULT_OK);
        finish();
    }
}

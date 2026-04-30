package com.konnash.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.konnash.R;
import com.konnash.database.DatabaseHelper;
import com.konnash.models.Client;
import com.konnash.models.Tag;

import java.util.ArrayList;
import java.util.List;

import android.widget.EditText;

/**
 * Add-client form activity.
 * Fields: name, phone, address.
 * Tags section with "Manage Tags" button that opens TagsActivity for selection.
 * On confirm: inserts the client into DB + links selected tags, then navigates to ClientCreatedActivity.
 */
public class AddClientActivity extends AppCompatActivity {

    private static final int REQUEST_TAGS = 1001;

    private EditText etName, etPhone, etAddress;
    private LinearLayout llSelectedTags;
    private HorizontalScrollView hsvSelectedTags;
    private TextView tvTagsHint;
    private TextView tvManageTagsLabel;

    /** Tags chosen by the user in TagsActivity. */
    private final List<Tag> selectedTags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client);

        etName          = findViewById(R.id.et_name);
        etPhone         = findViewById(R.id.et_phone);
        etAddress       = findViewById(R.id.et_address);
        llSelectedTags  = findViewById(R.id.ll_selected_tags);
        hsvSelectedTags = findViewById(R.id.hsv_selected_tags);
        tvTagsHint      = findViewById(R.id.tv_tags_hint);
        tvManageTagsLabel = findViewById(R.id.tv_manage_tags_label);

        // Back
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // Manage tags → open TagsActivity for selection
        findViewById(R.id.btn_manage_tags).setOnClickListener(v -> {
            Intent intent = new Intent(this, TagsActivity.class);
            intent.putExtra(TagsActivity.EXTRA_SELECTION_MODE, true);
            // Pass already-selected tag ids so TagsActivity can pre-check them
            long[] ids = new long[selectedTags.size()];
            for (int i = 0; i < selectedTags.size(); i++) ids[i] = selectedTags.get(i).getId();
            intent.putExtra(TagsActivity.EXTRA_SELECTED_IDS, ids);
            startActivityForResult(intent, REQUEST_TAGS);
        });

        // Confirm — save client
        findViewById(R.id.btn_confirm).setOnClickListener(v -> saveClient());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAGS && resultCode == RESULT_OK && data != null) {
            long[] ids    = data.getLongArrayExtra(TagsActivity.RESULT_SELECTED_IDS);
            String[] names  = data.getStringArrayExtra(TagsActivity.RESULT_SELECTED_NAMES);
            String[] colors = data.getStringArrayExtra(TagsActivity.RESULT_SELECTED_COLORS);

            selectedTags.clear();
            if (ids != null && names != null && colors != null) {
                for (int i = 0; i < ids.length; i++) {
                    Tag t = new Tag(names[i], colors[i]);
                    t.setId(ids[i]);
                    selectedTags.add(t);
                }
            }
            refreshTagChips();
        }
    }

    /** Render selected tag chips in the HorizontalScrollView. */
    private void refreshTagChips() {
        llSelectedTags.removeAllViews();

        if (selectedTags.isEmpty()) {
            hsvSelectedTags.setVisibility(View.GONE);
            tvTagsHint.setVisibility(View.VISIBLE);
            tvManageTagsLabel.setText(getString(R.string.btn_add_tags));
            return;
        }

        hsvSelectedTags.setVisibility(View.VISIBLE);
        tvTagsHint.setVisibility(View.GONE);
        tvManageTagsLabel.setText(getString(R.string.btn_manage_tags));

        for (Tag tag : selectedTags) {
            TextView chip = new TextView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.setMarginEnd(8);
            chip.setLayoutParams(lp);
            chip.setText(tag.getName());
            chip.setTextSize(12f);
            chip.setTextColor(Color.WHITE);
            chip.setPadding(24, 8, 24, 8);
            try {
                chip.setBackgroundColor(Color.parseColor(tag.getColor()));
            } catch (Exception e) {
                chip.setBackgroundResource(R.drawable.bg_tag_chip_selected);
            }
            llSelectedTags.addView(chip);
        }
    }

    private void saveClient() {
        String name    = etName.getText().toString().trim();
        String phone   = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_empty_name), Toast.LENGTH_SHORT).show();
            return;
        }

        // Build client object
        Client client = new Client(name, phone, address);

        // Persist to DB
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        long clientId = db.insertClient(client);

        // Link selected tags
        for (Tag tag : selectedTags) {
            db.addTagToClient(clientId, tag.getId());
        }

        // Navigate to success screen
        Intent intent = new Intent(this, ClientCreatedActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}

package com.konnash.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.konnash.R;
import com.konnash.adapters.TagsAdapter;
import com.konnash.database.DatabaseHelper;
import com.konnash.models.Tag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Tags management / selection screen.
 *
 * Modes:
 *  - Normal (EXTRA_SELECTION_MODE = false / not set):
 *      User can add/delete tags. No result returned.
 *  - Selection (EXTRA_SELECTION_MODE = true):
 *      User selects tags; tapping back / done returns RESULT_OK with selected ids/names/colors.
 */
public class TagsActivity extends AppCompatActivity {

    public static final String EXTRA_SELECTION_MODE  = "selection_mode";
    public static final String EXTRA_SELECTED_IDS    = "selected_ids";
    public static final String RESULT_SELECTED_IDS   = "result_ids";
    public static final String RESULT_SELECTED_NAMES = "result_names";
    public static final String RESULT_SELECTED_COLORS= "result_colors";

    private View            llEmptyState;
    private RecyclerView    rvTags;
    private TagsAdapter     adapter;
    private final List<Tag> tagList = new ArrayList<>();

    /** Ids that were already selected when this screen was opened. */
    private final Set<Long> preSelectedIds = new HashSet<>();

    private boolean selectionMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);

        AppCompatButton btn = findViewById(R.id.btn_confirm);

        btn.setOnClickListener(v -> {
            finishWithResult();
        });

        selectionMode = getIntent().getBooleanExtra(EXTRA_SELECTION_MODE, false);

        // Pre-selected ids (from AddClientActivity)
        long[] ids = getIntent().getLongArrayExtra(EXTRA_SELECTED_IDS);
        if (ids != null) for (long id : ids) preSelectedIds.add(id);

        llEmptyState = findViewById(R.id.ll_empty_state);
        rvTags       = findViewById(R.id.rv_tags);

        // Setup RecyclerView
        adapter = new TagsAdapter(tagList, selectionMode, preSelectedIds, this::onTagDeleted);
        rvTags.setLayoutManager(new LinearLayoutManager(this));
        rvTags.setAdapter(adapter);

        // Close button — return selected tags if in selection mode
        findViewById(R.id.btn_close).setOnClickListener(v -> finishWithResult());

        // Add new tag button
        findViewById(R.id.btn_add_new_tag).setOnClickListener(v -> {
            Intent intent = new Intent(this, AddTagActivity.class);
            startActivityForResult(intent, 1002);
        });

        loadTags();
    }

    @Override
    public void onBackPressed() {
        finishWithResult();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1002 && resultCode == RESULT_OK) {
            loadTags(); // refresh after adding a tag
        }
    }

    private void loadTags() {
        tagList.clear();
        tagList.addAll(DatabaseHelper.getInstance(this).getAllTags());
        adapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void updateEmptyState() {
        boolean empty = tagList.isEmpty();
        llEmptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        rvTags.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private void onTagDeleted(Tag tag) {
        DatabaseHelper.getInstance(this).deleteTag(tag.getId());
        tagList.remove(tag);
        adapter.notifyDataSetChanged();
        updateEmptyState();
    }

    /** Return selected tags to caller (only relevant in selection mode). */
    private void finishWithResult() {
        if (selectionMode) {
            Set<Long> selectedIds = adapter.getSelectedIds();
            List<Tag> selectedTags = new ArrayList<>();
            for (Tag t : tagList) {
                if (selectedIds.contains(t.getId())) selectedTags.add(t);
            }

            long[]   ids    = new long[selectedTags.size()];
            String[] names  = new String[selectedTags.size()];
            String[] colors = new String[selectedTags.size()];
            for (int i = 0; i < selectedTags.size(); i++) {
                ids[i]    = selectedTags.get(i).getId();
                names[i]  = selectedTags.get(i).getName();
                colors[i] = selectedTags.get(i).getColor();
            }

            Intent result = new Intent();
            result.putExtra(RESULT_SELECTED_IDS,    ids);
            result.putExtra(RESULT_SELECTED_NAMES,  names);
            result.putExtra(RESULT_SELECTED_COLORS, colors);
            setResult(RESULT_OK, result);
        }
        finish();
    }
}

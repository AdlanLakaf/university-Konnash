package com.konnash.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.konnash.R;
import com.konnash.models.Tag;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.ViewHolder> {

    public interface OnTagDeleteListener {
        void onDelete(Tag tag);
    }

    private final List<Tag>           tags;
    private final boolean             selectionMode;
    private final Set<Long>           selectedIds = new HashSet<>();
    private final OnTagDeleteListener deleteListener;

    public TagsAdapter(List<Tag> tags, boolean selectionMode, Set<Long> preSelectedIds, OnTagDeleteListener deleteListener) {
        this.tags           = tags;
        this.selectionMode  = selectionMode;
        this.deleteListener = deleteListener;
        if (preSelectedIds != null) selectedIds.addAll(preSelectedIds);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tag, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tag tag = tags.get(position);

        holder.tvName.setText(tag.getName());

        // Apply tag color to badge background
        try {
            int color = Color.parseColor(tag.getColor());
            holder.badgeLayout.setBackgroundColor(color);
            holder.tvName.setTextColor(Color.WHITE);
        } catch (Exception e) {
            holder.badgeLayout.setBackgroundResource(R.drawable.bg_name_badge);
            holder.tvName.setTextColor(Color.parseColor("#7FA8D4"));
        }

        if (selectionMode) {
            holder.cbSelected.setVisibility(View.VISIBLE);
            holder.cbSelected.setChecked(selectedIds.contains(tag.getId()));

            // FIX #2: Make checkbox non-clickable so only the row drives selection,
            // preventing the checkbox from consuming the event and double-toggling.
            holder.cbSelected.setClickable(false);
            holder.cbSelected.setFocusable(false);

            // FIX #1: Re-fetch tag via adapterPosition to avoid stale captured reference.
            holder.itemView.setOnClickListener(v -> {
                int pos = holder.getAdapterPosition();
                if (pos == RecyclerView.NO_ID) return;
                long id = tags.get(pos).getId();
                if (selectedIds.contains(id)) {
                    selectedIds.remove(id);
                    holder.cbSelected.setChecked(false);
                } else {
                    selectedIds.add(id);
                    holder.cbSelected.setChecked(true);
                }
            });

            holder.itemView.setOnLongClickListener(null);

        } else {
            holder.cbSelected.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(null);

            // FIX #3: Show confirmation dialog before deleting.
            holder.itemView.setOnLongClickListener(v -> {
                int pos = holder.getAdapterPosition();
                if (pos == RecyclerView.NO_ID) return true;
                Tag t = tags.get(pos);
                new AlertDialog.Builder(v.getContext())
                        .setTitle(R.string.delete_tag_title)
                        .setMessage(v.getContext().getString(R.string.delete_tag_message, t.getName()))
                        .setPositiveButton(R.string.delete, (dialog, which) -> {
                            if (deleteListener != null) deleteListener.onDelete(t);
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
                return true;
            });
        }
    }

    @Override
    public int getItemCount() { return tags.size(); }

    public Set<Long> getSelectedIds() { return selectedIds; }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout badgeLayout;
        TextView     tvName;
        CheckBox     cbSelected;

        ViewHolder(View itemView) {
            super(itemView);
            badgeLayout = itemView.findViewById(R.id.badge_layout);
            tvName      = itemView.findViewById(R.id.tv_tag_name);
            cbSelected  = itemView.findViewById(R.id.cb_tag_selected);
        }
    }
}
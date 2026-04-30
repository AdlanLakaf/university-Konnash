package com.konnash.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.konnash.R;

public class ColorPickerBottomSheet extends BottomSheetDialogFragment {

    public interface OnColorSelectedListener {
        void onColorSelected(String hex);
    }

    private OnColorSelectedListener listener;

    private static final String[] COLORS = {
            "#9B59B6", "#E91E8C", "#E74C3C", "#E8825A", "#F0C030", "#E0E0E0",
            "#5DADE2", "#3F51B5", "#5C6BC0", "#9575CD", "#B0BEC5", "#7E57C2",
            "#A1887F", "#9E9E9E", "#AED581", "#66BB6A", "#4DB6AC", "#26C6DA"
    };

    public void setOnColorSelectedListener(OnColorSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_color_picker, container, false);

        GridLayout grid = view.findViewById(R.id.grid_colors);

        for (String color : COLORS) {
            View swatch = new View(requireContext());
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = dpToPx(88);
            lp.height = dpToPx(88);
            lp.setMargins(dpToPx(6), dpToPx(6), dpToPx(6), dpToPx(6));
            swatch.setLayoutParams(lp);
            swatch.setBackgroundColor(android.graphics.Color.parseColor(color));
            // rounded corners
            swatch.setBackground(getRoundedDrawable(color));
            swatch.setOnClickListener(v -> {
                if (listener != null) listener.onColorSelected(color);
                dismiss();
            });
            grid.addView(swatch);
        }

        view.findViewById(R.id.btn_cancel).setOnClickListener(v -> dismiss());

        return view;
    }

    private android.graphics.drawable.GradientDrawable getRoundedDrawable(String color) {
        android.graphics.drawable.GradientDrawable d = new android.graphics.drawable.GradientDrawable();
        d.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
        d.setCornerRadius(dpToPx(12));
        d.setColor(android.graphics.Color.parseColor(color));
        return d;
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
package com.konnash.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.konnash.R;
import com.konnash.models.OnboardingSlideModel;

import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.SlideViewHolder> {

    private final List<OnboardingSlideModel> slides;

    public OnboardingAdapter(List<OnboardingSlideModel> slides) {
        this.slides = slides;
    }

    @NonNull
    @Override
    public SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_onboarding_slide, parent, false);
        return new SlideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlideViewHolder holder, int position) {
        OnboardingSlideModel slide = slides.get(position);
        holder.ivImage.setImageResource(slide.getImageResId());

        String title = slide.getTitle();
        if (title == null || title.isEmpty()) {
            holder.tvTitle.setVisibility(View.GONE);
        } else {
            holder.tvTitle.setVisibility(View.VISIBLE);
            holder.tvTitle.setText(
                    androidx.core.text.HtmlCompat.fromHtml(
                            title,
                            androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY));
        }

        holder.tvDesc.setText(
                androidx.core.text.HtmlCompat.fromHtml(
                        slide.getDescription(),
                        androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    @Override
    public int getItemCount() { return slides.size(); }

    static class SlideViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView  tvTitle;
        TextView  tvDesc;

        SlideViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_slide_image);
            tvTitle = itemView.findViewById(R.id.tv_slide_title);
            tvDesc  = itemView.findViewById(R.id.tv_slide_desc);
        }
    }
}

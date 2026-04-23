// FILE NAME: OnboardingActivity.java
// PATH: app/src/main/java/com/konnash/activities/OnboardingActivity.java

package com.konnash.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.konnash.R;
import com.konnash.adapters.OnboardingAdapter;
import com.konnash.models.OnboardingSlideModel;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2        viewPager;
    private LinearLayout      layoutDots;
    private Button            btnStart;
    private OnboardingAdapter adapter;
    private List<ImageView>   dots = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager  = findViewById(R.id.viewpager_onboarding);
        layoutDots = findViewById(R.id.layout_dots);
        btnStart   = findViewById(R.id.btn_start);

        // Fix 1 — force LTR on the internal RecyclerView inside ViewPager2
        RecyclerView recyclerView = (RecyclerView) viewPager.getChildAt(0);
        recyclerView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);

        // Fix 2 — force LTR on the dots row so dot 1 is on the left, last dot on the right
        layoutDots.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);

        List<OnboardingSlideModel> slides = buildSlides();
        adapter = new OnboardingAdapter(slides);
        viewPager.setAdapter(adapter);

        setupDots(slides.size());
        updateDots(0);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateDots(position);
            }
        });

        btnStart.setOnClickListener(v -> {
            startActivity(new Intent(this, PhoneActivity.class));
            finish();
        });
    }

    private List<OnboardingSlideModel> buildSlides() {
        List<OnboardingSlideModel> slides = new ArrayList<>();

        slides.add(new OnboardingSlideModel(
                R.drawable.ic_onboarding_welcome,
                getString(R.string.onboarding_title_1),
                getString(R.string.onboarding_desc_1)));

        slides.add(new OnboardingSlideModel(
                R.drawable.ic_onboarding_debts,
                getString(R.string.onboarding_title_2),
                getString(R.string.onboarding_desc_2)));

        slides.add(new OnboardingSlideModel(
                R.drawable.ic_onboarding_clients,
                getString(R.string.onboarding_title_3),
                getString(R.string.onboarding_desc_3)));

        slides.add(new OnboardingSlideModel(
                R.drawable.ic_onboarding_remind,
                getString(R.string.onboarding_title_4),
                getString(R.string.onboarding_desc_4)));

        slides.add(new OnboardingSlideModel(
                R.drawable.ic_onboarding_photo,
                getString(R.string.onboarding_title_5),
                getString(R.string.onboarding_desc_5)));

        return slides;
    }

    private void setupDots(int count) {
        dots.clear();
        layoutDots.removeAllViews();
        for (int i = 0; i < count; i++) {
            ImageView dot = new ImageView(this);
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(10, 10);
            params.setMargins(6, 0, 6, 0);
            dot.setLayoutParams(params);
            dot.setImageResource(R.drawable.dot_inactive);
            dots.add(dot);
            layoutDots.addView(dot);
        }
    }

    private void updateDots(int activeIndex) {
        for (int i = 0; i < dots.size(); i++) {
            dots.get(i).setImageResource(
                    i == activeIndex ? R.drawable.dot_active : R.drawable.dot_inactive);
        }
    }
}
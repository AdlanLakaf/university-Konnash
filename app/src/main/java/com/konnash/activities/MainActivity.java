package com.konnash.activities;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.konnash.R;
import com.konnash.fragments.CashBookFragment;
import com.konnash.fragments.DebtBookFragment;
import com.konnash.fragments.MoreFragment;

public class MainActivity extends AppCompatActivity {

    private LinearLayout tabDebt, tabCash, tabMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabDebt = findViewById(R.id.tab_debt);
        tabCash = findViewById(R.id.tab_cash);
        tabMore = findViewById(R.id.tab_more);

        // Default tab: Cash Book
        loadFragment(new CashBookFragment());
        setActiveTab(tabCash);

        tabDebt.setOnClickListener(v -> {
            loadFragment(new DebtBookFragment());
            setActiveTab(tabDebt);
        });

        tabCash.setOnClickListener(v -> {
            loadFragment(new CashBookFragment());
            setActiveTab(tabCash);
        });

        tabMore.setOnClickListener(v -> {
            loadFragment(new MoreFragment());
            setActiveTab(tabMore);
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    private void setActiveTab(LinearLayout activeTab) {
        // Reset all tabs to unselected color
        int unselected = ContextCompat.getColor(this, R.color.nav_unselected);
        int selected   = ContextCompat.getColor(this, R.color.primary_blue);

        for (LinearLayout tab : new LinearLayout[]{tabDebt, tabCash, tabMore}) {
            // Find child TextView and set its color
            if (tab.getChildCount() >= 2) {
                android.widget.TextView tv =
                        (android.widget.TextView) tab.getChildAt(1);
                tv.setTextColor(tab == activeTab ? selected : unselected);
            }
        }
    }
}

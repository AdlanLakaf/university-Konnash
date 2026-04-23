package com.konnash.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.konnash.R;

/**
 * Debt Book tab — placeholder.
 * Onboarding screens (Images 7–8–12–13) describe this module.
 * Full implementation (clients, debts, WhatsApp reminders) is
 * out of scope for this educational build.
 */
public class DebtBookFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_debt_book, container, false);
    }
}

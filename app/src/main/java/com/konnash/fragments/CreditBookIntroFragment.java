package com.konnash.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.konnash.R;
import com.konnash.activities.AddClientEntryActivity;
import com.konnash.database.DatabaseHelper;

/**
 * Credit Book intro fragment — shown when no clients exist yet.
 * Shows illustration + Add Client button.
 */
public class CreditBookIntroFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_credit_book_intro, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Back
        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            if (getActivity() != null) getActivity().onBackPressed();
        });

        // Add Client → entry screen
        view.findViewById(R.id.btn_add_client_intro).setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), AddClientEntryActivity.class));
        });
    }
}

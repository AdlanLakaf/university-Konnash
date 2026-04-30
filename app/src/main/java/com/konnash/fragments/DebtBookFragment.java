package com.konnash.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.konnash.R;
import com.konnash.database.DatabaseHelper;

/**
 * DebtBookFragment — Credit Book tab controller.
 * Decides whether to show the intro (no clients) or the main list.
 */
public class DebtBookFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_debt_book, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showCreditBookFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        showCreditBookFragment();
    }

    private void showCreditBookFragment() {
        int clientCount = DatabaseHelper.getInstance(requireContext()).getClientCount();
        Fragment target = clientCount > 0
                ? new CreditBookFragment()
                : new CreditBookIntroFragment();

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.credit_book_container, target)
                .commit();
    }
}

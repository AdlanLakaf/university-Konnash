package com.konnash.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.konnash.R;
import com.konnash.activities.AddTransactionActivity;
import com.konnash.adapters.TransactionAdapter;
import com.konnash.database.DatabaseHelper;
import com.konnash.models.Transaction;
import com.konnash.utils.DateUtils;
import com.konnash.utils.PrefsManager;

import java.util.List;

public class CashBookFragment extends Fragment {

    private static final int REQUEST_ADD_TX = 101;

    private TextView           tvBalance;
    private TextView           tvTotalIncome;
    private TextView           tvTotalExpense;
    private TextView           tvTransactionsCount;
    private RecyclerView       rvTransactions;
    private Button             btnIncome;
    private Button             btnExpense;
    private View               emptyState;

    private DatabaseHelper     db;
    private TransactionAdapter adapter;
    private List<Transaction>  transactions;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Decide which layout to show based on whether there are transactions
        db = DatabaseHelper.getInstance(requireContext());
        transactions = db.getAllTransactions();

        boolean hasTx = !transactions.isEmpty();
        boolean cashIntroSeen = PrefsManager.getInstance(requireContext()).isCashIntroSeen();

        if (!hasTx && !cashIntroSeen) {
            // Show intro/empty state layout (Image 1 & Image 2)
            return inflateIntroLayout(inflater, container);
        } else {
            PrefsManager.getInstance(requireContext()).setCashIntroSeen(true);
            return inflateMainLayout(inflater, container);
        }
    }

    // ─── Intro Layout (first time, no transactions) ────────

    private View inflateIntroLayout(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_cash_book_intro, container, false);

        Button btnIncomeIntro  = view.findViewById(R.id.btn_income_intro);
        Button btnExpenseIntro = view.findViewById(R.id.btn_expense_intro);

        btnIncomeIntro.setOnClickListener(v -> openAddTransaction(Transaction.TYPE_INCOME));
        btnExpenseIntro.setOnClickListener(v -> openAddTransaction(Transaction.TYPE_EXPENSE));

        return view;
    }

    // ─── Main Layout (has transactions OR intro already seen) ─

    private View inflateMainLayout(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_cash_book, container, false);

        tvBalance           = view.findViewById(R.id.tv_balance);
        tvTotalIncome       = view.findViewById(R.id.tv_total_income);
        tvTotalExpense      = view.findViewById(R.id.tv_total_expense);
        tvTransactionsCount = view.findViewById(R.id.tv_transactions_count);
        rvTransactions      = view.findViewById(R.id.rv_transactions);
        btnIncome           = view.findViewById(R.id.btn_income);
        btnExpense          = view.findViewById(R.id.btn_expense);

        // RecyclerView setup
        rvTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvTransactions.addItemDecoration(
                new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        adapter = new TransactionAdapter(requireContext(), transactions);
        rvTransactions.setAdapter(adapter);

        // Load data
        refreshUI();

        btnIncome.setOnClickListener(v -> openAddTransaction(Transaction.TYPE_INCOME));
        btnExpense.setOnClickListener(v -> openAddTransaction(Transaction.TYPE_EXPENSE));

        // Quick action buttons (stub – archive/reports/close are UI-only for now)
        view.findViewById(R.id.btn_archive).setOnClickListener(v -> {
            // TODO: navigate to archive screen
        });
        view.findViewById(R.id.btn_reports).setOnClickListener(v -> {
            // TODO: navigate to reports screen
        });
        view.findViewById(R.id.btn_close).setOnClickListener(v -> {
            // TODO: close/end period
        });

        return view;
    }

    // ─── Data refresh ──────────────────────────────────────

    private void refreshUI() {
        double income  = db.getTotalIncome();
        double expense = db.getTotalExpense();
        double balance = income - expense;

        tvBalance.setText(DateUtils.formatAmount(balance));
        tvTotalIncome.setText("الدخل: " + DateUtils.formatAmount(income));
        tvTotalExpense.setText("المصروف: " + DateUtils.formatAmount(expense));
        tvTransactionsCount.setText("المعاملات (" + transactions.size() + ")");

        // Reload list
        transactions.clear();
        transactions.addAll(db.getAllTransactions());
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    // ─── Navigation ────────────────────────────────────────

    private void openAddTransaction(String type) {
        Intent intent = new Intent(requireContext(), AddTransactionActivity.class);
        intent.putExtra(AddTransactionActivity.EXTRA_TYPE, type);
        startActivityForResult(intent, REQUEST_ADD_TX);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_TX
                && resultCode == android.app.Activity.RESULT_OK) {
            // Transaction was saved → refresh UI
            // If we're on intro layout, switch to main layout
            if (tvBalance == null) {
                // Re-inflate the whole fragment
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new CashBookFragment())
                        .commit();
            } else {
                refreshUI();
            }
        }
    }
}

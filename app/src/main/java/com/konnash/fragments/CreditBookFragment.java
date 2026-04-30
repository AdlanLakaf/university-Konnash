package com.konnash.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.konnash.R;
import com.konnash.activities.AddClientActivity;
import com.konnash.activities.AddClientEntryActivity;
import com.konnash.adapters.ClientsAdapter;
import com.konnash.database.DatabaseHelper;
import com.konnash.models.Client;

import java.util.ArrayList;
import java.util.List;

/**
 * Credit Book fragment — shown when clients exist.
 * Displays summary card and a list of clients.
 */
public class CreditBookFragment extends Fragment {

    private RecyclerView   rvClients;
    private ClientsAdapter adapter;
    private final List<Client> clientList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_credit_book, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvClients = view.findViewById(R.id.rv_clients);
        adapter   = new ClientsAdapter(clientList);
        rvClients.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvClients.setAdapter(adapter);

        // FAB replaces the old header button
        view.findViewById(R.id.fab_add_client).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), AddClientActivity.class))
        );

//        // Search
//        ((EditText) view.findViewById(R.id.et_search)).addTextChangedListener(new android.text.TextWatcher() {
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                adapter.filter(s.toString());
//            }
//            public void afterTextChanged(android.text.Editable s) {}
//        });

        loadClients();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadClients();
    }

    private void loadClients() {
        clientList.clear();
        clientList.addAll(DatabaseHelper.getInstance(requireContext()).getAllClients());
        adapter.notifyDataSetChanged();

        // Update count label
        TextView tvCount = getView() == null ? null : getView().findViewById(R.id.tv_clients_count);
        if (tvCount != null) tvCount.setText("العملاء (" + clientList.size() + ")");
    }
}

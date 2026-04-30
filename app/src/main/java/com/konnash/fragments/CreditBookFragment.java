package com.konnash.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.konnash.R;
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

        // Add client button in header
        view.findViewById(R.id.btn_add_client_header).setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), AddClientEntryActivity.class));
        });

        // Back button
        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            if (getActivity() != null) getActivity().onBackPressed();
        });

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
    }
}

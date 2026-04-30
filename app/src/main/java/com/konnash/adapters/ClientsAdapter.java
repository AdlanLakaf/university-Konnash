package com.konnash.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.konnash.R;
import com.konnash.models.Client;

import java.util.List;

public class ClientsAdapter extends RecyclerView.Adapter<ClientsAdapter.ViewHolder> {

    private final List<Client> clients;

    public ClientsAdapter(List<Client> clients) {
        this.clients = clients;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_client, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Client client = clients.get(position);

        holder.tvName.setText(client.getName());
        holder.tvPhone.setText(client.getPhone() != null && !client.getPhone().isEmpty()
                ? client.getPhone()
                : "");
        // Debt amount: placeholder — no transaction logic yet
        holder.tvDebt.setText("0.00 د.ج.");
    }

    @Override
    public int getItemCount() { return clients.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvDebt;

        ViewHolder(View itemView) {
            super(itemView);
            tvName  = itemView.findViewById(R.id.tv_client_name);
            tvPhone = itemView.findViewById(R.id.tv_client_phone);
            tvDebt  = itemView.findViewById(R.id.tv_client_debt);
        }
    }
}

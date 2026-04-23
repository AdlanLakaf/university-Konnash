package com.konnash.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.konnash.R;
import com.konnash.models.Transaction;
import com.konnash.utils.DateUtils;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private final Context           context;
    private final List<Transaction> transactions;

    public TransactionAdapter(Context context, List<Transaction> transactions) {
        this.context      = context;
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction tx = transactions.get(position);

        boolean isIncome = tx.isIncome();

        // Type label
        holder.tvTypeLabel.setText(isIncome
                ? context.getString(R.string.income)
                : context.getString(R.string.expense));

        // Balance after label
        holder.tvBalanceAfter.setText("الرصيد " + DateUtils.formatAmount(tx.getBalanceAfter()));

        // Amount
        holder.tvAmount.setText(DateUtils.formatAmount(tx.getAmount()));
        holder.tvAmount.setTextColor(ContextCompat.getColor(context,
                isIncome ? R.color.income_green : R.color.expense_red));

        // Time
        holder.tvTime.setText(DateUtils.formatTimestamp(tx.getTimestamp()));

        // Icon background + drawable
        /*
         * [IMAGE PLACEHOLDER]
         * ic_plus_white  → white "+" icon for income  (20dp)
         * ic_minus_white → white "−" icon for expense (20dp)
         * Both placed in res/drawable/
         */
        if (isIncome) {
            holder.layoutIcon.setBackgroundResource(R.drawable.bg_income_icon);
            holder.ivTypeIcon.setImageResource(R.drawable.ic_plus_placeholder);
        } else {
            holder.layoutIcon.setBackgroundResource(R.drawable.bg_expense_icon);
            holder.ivTypeIcon.setImageResource(R.drawable.ic_minus_placeholder);
        }

        // Separator line for all but last item
        holder.divider.setVisibility(
                position < transactions.size() - 1 ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() { return transactions.size(); }

    // ─── ViewHolder ─────────────────────────────────────────

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutIcon;
        ImageView    ivTypeIcon;
        TextView     tvTypeLabel;
        TextView     tvBalanceAfter;
        TextView     tvAmount;
        TextView     tvTime;
        View         divider;

        TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutIcon     = itemView.findViewById(R.id.layout_icon);
            ivTypeIcon     = itemView.findViewById(R.id.iv_type_icon);
            tvTypeLabel    = itemView.findViewById(R.id.tv_type_label);
            tvBalanceAfter = itemView.findViewById(R.id.tv_balance_after);
            tvAmount       = itemView.findViewById(R.id.tv_amount);
            tvTime         = itemView.findViewById(R.id.tv_time);
            divider        = itemView.findViewById(R.id.divider);
        }
    }
}

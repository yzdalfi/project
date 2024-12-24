package com.example.project3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private final ArrayList<Transaction> transactionList;
    private final OnTransactionClickListener listener;

    // Interface untuk menangani klik item
    public interface OnTransactionClickListener {
        void onTransactionClick(Transaction transaction);
    }

    public TransactionAdapter(ArrayList<Transaction> transactionList, OnTransactionClickListener listener) {
        this.transactionList = transactionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        holder.tvDate.setText(transaction.getDate());
        holder.tvDescription.setText(transaction.getDescription());
        holder.tvAmount.setText(String.format(Locale.getDefault(), "Rp %.2f", transaction.getAmount()));
        holder.tvCategory.setText(transaction.getCategory());

        holder.itemView.setOnClickListener(v -> listener.onTransactionClick(transaction));
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvDescription, tvAmount, tvCategory;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvCategory = itemView.findViewById(R.id.tvCategory);
        }
    }
}

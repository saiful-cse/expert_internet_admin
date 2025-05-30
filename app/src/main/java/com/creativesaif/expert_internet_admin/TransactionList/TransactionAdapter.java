package com.creativesaif.expert_internet_admin.TransactionList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.creativesaif.expert_internet_admin.NewsFeed.News;
import com.creativesaif.expert_internet_admin.NewsFeed.NewsAdapter;
import com.creativesaif.expert_internet_admin.R;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.EachTransactionView>{

    Context context;
    List<Transaction> transactionList;

    public TransactionAdapter(Context context, List<Transaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public EachTransactionView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.card_each_txn, null);
        return new EachTransactionView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EachTransactionView eachTransactionView, int i) {

        Transaction transaction = transactionList.get(i);

        eachTransactionView.tvDate.setText(transaction.getDate());
        eachTransactionView.tvTxnId.setText(transaction.getTxn_id());
        eachTransactionView.tvDetails.setText(transaction.getDetails());
        eachTransactionView.tvAmount.setText(transaction.getCredit());
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    class EachTransactionView extends RecyclerView.ViewHolder {

        TextView tvDate, tvTxnId, tvDetails, tvAmount;

        public EachTransactionView(@NonNull View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.tvDate);
            tvTxnId = itemView.findViewById(R.id.tvTxnId);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }
    }
}

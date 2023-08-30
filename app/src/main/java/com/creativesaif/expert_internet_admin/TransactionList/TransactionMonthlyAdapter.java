package com.creativesaif.expert_internet_admin.TransactionList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.creativesaif.expert_internet_admin.R;

import java.util.List;

public class TransactionMonthlyAdapter extends RecyclerView.Adapter<TransactionMonthlyAdapter.EachTransactionView>{

    Context context;
    List<Transaction> transactionList;

    public TransactionMonthlyAdapter(Context context, List<Transaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public EachTransactionView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.card_each_txn_admin_monthly, null);
        return new EachTransactionView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EachTransactionView eachTransactionView, int i) {

        Transaction transaction = transactionList.get(i);

        eachTransactionView.tvDate.setText(transaction.getDate());
        eachTransactionView.tvTxnId.setText(transaction.getTxn_id());
        eachTransactionView.tvempid.setText(transaction.getEmpid());
        eachTransactionView.tvMethod.setText(transaction.getMethod());
        eachTransactionView.tvDetails.setText(transaction.getDetails());
        eachTransactionView.tvCredit.setText(transaction.getCredit());
        eachTransactionView.tvDebit.setText(transaction.getDebit());
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    class EachTransactionView extends RecyclerView.ViewHolder {

        TextView tvDate, tvTxnId, tvDetails, tvCredit, tvDebit, tvempid, tvMethod;

        public EachTransactionView(@NonNull View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.tvtxnDate);
            tvTxnId = itemView.findViewById(R.id.tvtxnId);
            tvDetails = itemView.findViewById(R.id.tvtxnDetails);
            tvCredit = itemView.findViewById(R.id.tvtxnCredit);
            tvDebit = itemView.findViewById(R.id.tvtxnDebit);
            tvempid = itemView.findViewById(R.id.tvempid);
            tvMethod = itemView.findViewById(R.id.tvmethod);
        }
    }
}

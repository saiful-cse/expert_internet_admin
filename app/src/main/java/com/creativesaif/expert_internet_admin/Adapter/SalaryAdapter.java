package com.creativesaif.expert_internet_admin.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.creativesaif.expert_internet_admin.Model.Salary;
import com.creativesaif.expert_internet_admin.Model.SalaryWrapper;
import com.creativesaif.expert_internet_admin.R;

import java.util.ArrayList;

public class SalaryAdapter extends RecyclerView.Adapter<SalaryAdapter.MyViewHolder>{

    private final Context mContext;
    private ArrayList<Salary> salaryArrayList;

    public SalaryAdapter(Context mContext, ArrayList<Salary> salaryArrayList) {
        this.mContext = mContext;
        this.salaryArrayList = salaryArrayList;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSalaryList(ArrayList<Salary> salaryArrayList1){
        this.salaryArrayList = salaryArrayList1;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.card_each_salary, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        final Salary salary = salaryArrayList.get(i);
        myViewHolder.tvSalDate.setText(salary.getDate());
        myViewHolder.tvSalEmpId.setText(salary.getEmployee_id());
        myViewHolder.tvSalMonth.setText(salary.getMonth());
        myViewHolder.tvSalAmount.setText(salary.getAmount());
    }

    @Override
    public int getItemCount() {
        return salaryArrayList.size();
    }

    public static  class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvSalDate, tvSalEmpId, tvSalMonth, tvSalAmount;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSalDate = itemView.findViewById(R.id.tvSalDate);
            tvSalEmpId = itemView.findViewById(R.id.tvSalEmpId);
            tvSalMonth = itemView.findViewById(R.id.tvSalMonth);
            tvSalAmount = itemView.findViewById(R.id.tvSalAmount);
        }
    }
}

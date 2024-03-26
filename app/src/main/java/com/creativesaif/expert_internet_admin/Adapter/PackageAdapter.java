package com.creativesaif.expert_internet_admin.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.creativesaif.expert_internet_admin.DeviceUrl.PackageUse;
import com.creativesaif.expert_internet_admin.R;

import java.util.List;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.EachPkgView>{
    Context context;
    List<PackageUse> packageList;

    public PackageAdapter(Context context, List<PackageUse> packageList) {
        this.context = context;
        this.packageList = packageList;
    }

    @NonNull
    @Override
    public EachPkgView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.card_each_pkg, null);
        return new PackageAdapter.EachPkgView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EachPkgView eachPkgView, int i) {
        PackageUse packageUse = packageList.get(i);

        eachPkgView.tvPkgNmae.setText(packageUse.getPkgNmae());
        eachPkgView.tvQtn.setText(packageUse.getPkgQtn());
    }

    @Override
    public int getItemCount() {
        return packageList.size();
    }

    class EachPkgView extends RecyclerView.ViewHolder {
        TextView tvPkgNmae, tvQtn;

        public EachPkgView(@NonNull View itemView) {
            super(itemView);

            tvPkgNmae = itemView.findViewById(R.id.tvDashPkgName);
            tvQtn = itemView.findViewById(R.id.tvDashQtn);
        }
    }
}

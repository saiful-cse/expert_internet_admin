package com.creativesaif.expert_internet_admin.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.creativesaif.expert_internet_admin.Model.Client;
import com.creativesaif.expert_internet_admin.Model.Package;
import com.creativesaif.expert_internet_admin.R;

import java.util.List;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.MyViewHolder>{

    private final Context context;
    private List<Package> packageList;

    public PackageAdapter(Context context, List<Package> packageList) {
        this.context = context;
        this.packageList = packageList;
    }

    public void setPackageList(List<Package> packageList){
        this.packageList = packageList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view =  LayoutInflater.from(context).inflate(R.layout.each_package_card, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder( PackageAdapter.MyViewHolder myViewHolder, int i) {

        final Package pack = packageList.get(i);
        myViewHolder.tv1.setText("Package ID: "+pack.getPkgId());
        myViewHolder.tv2.setText("Package Name: "+pack.getTitle());
        myViewHolder.tv3.setText("Speed: "+pack.getSpeed());
        myViewHolder.tv4.setText("BDT: "+pack.getPrice()+" TK/Monthly");
    }

    @Override
    public int getItemCount() {
        if (packageList !=null & packageList.size() > 0){
            return packageList.size();
        }else{
            return 0;
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv1, tv2, tv3, tv4;
        Button btnUpgrade;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv1 = itemView.findViewById(R.id.tvpkgid);
            tv2 = itemView.findViewById(R.id.tvpkgname);
            tv3 = itemView.findViewById(R.id.tvpkgspeed);
            tv4 = itemView.findViewById(R.id.tvpkgprice);

        }
    }
}

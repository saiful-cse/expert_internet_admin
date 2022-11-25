package com.creativesaif.expert_internet_admin.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.creativesaif.expert_internet_admin.ClientList.ClientDetails;
import com.creativesaif.expert_internet_admin.ClientList.ClientRegUpdate;
import com.creativesaif.expert_internet_admin.Model.Client;
import com.creativesaif.expert_internet_admin.R;

import java.util.ArrayList;
import java.util.List;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.MyViewHolder>{

    // creating a variable for array list and context.
    private final Context mContext;
    private ArrayList<Client> clientArrayList;

    // creating a constructor for our variables.
    public ClientAdapter(Context mContext, ArrayList<Client> clientArrayList) {
        this.mContext = mContext;
        this.clientArrayList = clientArrayList;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setClientList(ArrayList<Client> clientArrayList){
        this.clientArrayList = clientArrayList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view =  LayoutInflater.from(mContext).inflate(R.layout.card_each_client, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientAdapter.MyViewHolder myViewHolder, int i) {

        final Client client = clientArrayList.get(i);
        myViewHolder.tv1.setText(client.getName());
        myViewHolder.tv2.setText("Phone: "+client.getPhone()+", Mode: "+client.getMode());
        myViewHolder.tv3.setText("PPP: "+client.getPppName()+", Zone: "+client.getZone());
        myViewHolder.tv4.setText("Area: "+client.getArea());
        myViewHolder.tv5.setText(client.getExpireDate());
        myViewHolder.tv6.setText("Payment: "+client.getPaymentMethod());
        myViewHolder.tv7.setText(client.getTakeTime());

        myViewHolder.tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i;

              if(client.getRegistered().equals("0")){

                    i = new Intent(mContext, ClientRegUpdate.class);
                    i.putExtra("id", client.getId());
                }
                else{
                    i = new Intent(mContext, ClientDetails.class);
                    i.putExtra("id", client.getId());
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return clientArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv1 = itemView.findViewById(R.id.cardtv1);
            tv2 = itemView.findViewById(R.id.cardtv2);
            tv3 = itemView.findViewById(R.id.cardtv3);
            tv4 = itemView.findViewById(R.id.cardtv4);
            tv5 = itemView.findViewById(R.id.cardExpdate);
            tv6 = itemView.findViewById(R.id.cardMethod);
            tv7 = itemView.findViewById(R.id.cardTaketime);
        }
    }
}

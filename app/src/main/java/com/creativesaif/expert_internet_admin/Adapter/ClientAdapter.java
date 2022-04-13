package com.creativesaif.expert_internet_admin.Adapter;

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

import java.util.List;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.MyViewHolder>{

    private final Context mContext;
    private List<Client> clientList;

    public ClientAdapter(Context mContext, List<Client> clientList) {
        this.mContext = mContext;
        this.clientList = clientList;
    }

    public void setClientList(List<Client> clientList){
        this.clientList = clientList;
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

        final Client client = clientList.get(i);
        myViewHolder.tv1.setText(client.getName());
        myViewHolder.tv2.setText("Phone: "+client.getPhone());
        myViewHolder.tv3.setText("PPP: "+client.getPppName());
        myViewHolder.tv4.setText("Area: "+client.getArea());

        myViewHolder.tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i;

                if (client.getRegistered().equals("0")){

                    i = new Intent(mContext, ClientRegUpdate.class);
                    i.putExtra("id", client.getId());

                }else{
                    i = new Intent(mContext, ClientDetails.class);
                    i.putExtra("id", client.getId());
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                mContext.startActivity(i);


                //Toast.makeText(mContext.getApplicationContext(), "You have clicked", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        if (clientList !=null & clientList.size() > 0){
            return clientList.size();
        }else{
            return 0;
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv1, tv2, tv3, tv4;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv1 = itemView.findViewById(R.id.cardtv1);
            tv2 = itemView.findViewById(R.id.cardtv2);
            tv3 = itemView.findViewById(R.id.cardtv3);
            tv4 = itemView.findViewById(R.id.cardtv4);
        }
    }
}

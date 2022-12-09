package com.creativesaif.expert_internet_admin.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.creativesaif.expert_internet_admin.ClientList.ClientDetails;
import com.creativesaif.expert_internet_admin.ClientList.ClientRegUpdate;
import com.creativesaif.expert_internet_admin.Model.Client;
import com.creativesaif.expert_internet_admin.R;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> implements Filterable {
    // creating a variable for array list and context.
    private ArrayList<Client> clientList;
    private ArrayList<Client> filteredClientList;
    private Context context;

    // creating a constructor for our variables.
    public SearchAdapter(Context context, ArrayList<Client> clientArrayList){
        this.context = context;
        this.clientList = clientArrayList;
        this.filteredClientList = clientArrayList;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setClientList(ArrayList<Client> clientArrayList){
        this.clientList = clientArrayList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view =  LayoutInflater.from(context).inflate(R.layout.card_each_client, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        final Client client = filteredClientList.get(i);
        myViewHolder.tv1.setText(client.getName());
        if (client.getMode().equals("Disable")){
            myViewHolder.tvmode.setTextColor(Color.RED);
        }else{
            myViewHolder.tvmode.setTextColor(Color.GREEN);
        }
        myViewHolder.tvmode.setText(client.getMode());
        myViewHolder.tv2.setText("Phone: "+client.getPhone());
        myViewHolder.tv3.setText("PPP: "+client.getPppName());
        myViewHolder.tvzone.setText("Zone: "+client.getZone());
        myViewHolder.tvpkgid.setText("Package: "+client.getPkgId());
        myViewHolder.tv4.setText("Area: "+client.getArea());
        myViewHolder.tv5.setText(client.getExpireDate());
        myViewHolder.tv6.setText("Payment: "+client.getPaymentMethod());
        myViewHolder.tv7.setText(client.getTakeTime());

        myViewHolder.tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i;

                if(client.getRegistered().equals("0")){

                    i = new Intent(context, ClientRegUpdate.class);
                    i.putExtra("id", client.getId());
                }
                else{
                    i = new Intent(context, ClientDetails.class);
                    i.putExtra("id", client.getId());
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredClientList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String searchString = charSequence.toString();
                if (searchString.isEmpty()){
                    filteredClientList = clientList;
                }else{
                    ArrayList<Client> tempFilteredClientList = new ArrayList<>();
                    for (Client client : clientList){
                        //search for client name
                        if (client.getName().toLowerCase().contains(searchString)
                                || client.getPppName().toLowerCase().contains(searchString)
                                || client.getArea().toLowerCase().contains(searchString)
                                || client.getMode().toLowerCase().contains(searchString)
                                || client.getZone().toLowerCase().contains(searchString)
                                || client.getPaymentMethod().toLowerCase().contains(searchString)
                                || client.getPhone().toLowerCase().contains(searchString)
                                || client.getPkgId().toLowerCase().contains(searchString)){
                            tempFilteredClientList.add(client);
                        }
                    }
                    filteredClientList = tempFilteredClientList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredClientList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredClientList = (ArrayList<Client>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv1, tv2, tvmode, tv3,tvzone, tvpkgid, tv4, tv5, tv6, tv7;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv1 = itemView.findViewById(R.id.cardtv1);
            tv2 = itemView.findViewById(R.id.cardtv2);
            tvmode = itemView.findViewById(R.id.cardtvmode);
            tv3 = itemView.findViewById(R.id.cardtv3);
            tvzone = itemView.findViewById(R.id.cardtvzone);
            tvpkgid = itemView.findViewById(R.id.cardtvpkgid);
            tv4 = itemView.findViewById(R.id.cardtv4);
            tv5 = itemView.findViewById(R.id.cardExpdate);
            tv6 = itemView.findViewById(R.id.cardMethod);
            tv7 = itemView.findViewById(R.id.cardTaketime);
        }
    }
}

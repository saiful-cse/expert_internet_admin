package com.creativesaif.expert_internet_admin.ClientList;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.creativesaif.expert_internet_admin.NewsFeed.News;
import com.creativesaif.expert_internet_admin.NewsFeed.NewsAdapter;
import com.creativesaif.expert_internet_admin.NewsFeed.NewsDetails;
import com.creativesaif.expert_internet_admin.R;

import java.util.List;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.EachClientView>{

    private Context context;
    private List<Client> clientList;

    public ClientAdapter(Context context, List<Client> clientList) {
        this.context = context;
        this.clientList = clientList;
    }

    @NonNull
    @Override
    public EachClientView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View rootView = LayoutInflater.from(context).inflate(R.layout.card_each_client, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rootView.setLayoutParams(lp);
        return new EachClientView(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull EachClientView eachClientView, int i) {

        final Client client = clientList.get(i);

        eachClientView.textViewName.setText(client.getName());
        eachClientView.textViewId.setText("#"+client.getId()+", "+client.getArea()+", Payment: "+client.getPayment_method());
        eachClientView.textViewPhone.setText("Mobile: "+client.getPhone()+", PPPoE: "+client.getUsername());

        eachClientView.textViewName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ClientDetails.class);
                i.putExtra("id", client.getId());
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return clientList.size();
    }

    class EachClientView extends RecyclerView.ViewHolder {
        List<Client> clientListView;

        TextView textViewName, textViewId, textViewPhone;

        private EachClientView(@NonNull View itemView) {
            super(itemView);

            clientListView = clientList;

            textViewName = itemView.findViewById(R.id.client_name);
            textViewId = itemView.findViewById(R.id.client_id);
            textViewPhone = itemView.findViewById(R.id.client_phone);

        }
    }
}

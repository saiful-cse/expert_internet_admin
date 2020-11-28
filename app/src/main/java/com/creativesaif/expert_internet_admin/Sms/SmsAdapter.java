package com.creativesaif.expert_internet_admin.Sms;

import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.creativesaif.expert_internet_admin.ClientList.ClientAdapter;
import com.creativesaif.expert_internet_admin.R;

import java.util.List;

public class SmsAdapter extends RecyclerView.Adapter<SmsAdapter.EachSmsView>{
    private Context context;
    private List<Sms> smsList;

    public SmsAdapter(Context context, List<Sms> smsList) {
        this.context = context;
        this.smsList = smsList;
    }


    @NonNull
    @Override
    public EachSmsView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.each_sms, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rootView.setLayoutParams(lp);
        return new EachSmsView(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull EachSmsView eachSmsView, int i) {
        Sms sms = smsList.get(i);

        eachSmsView.sms_details.setText("Time: "+sms.getCreated_at()+"\nClient ID: "+sms.getClient_id()+", SMS ID: "+sms.getMsg_id()+", Tag: "+sms.getTag());
        eachSmsView.sms_body.setText(sms.getMsg_body());
    }


    @Override
    public int getItemCount() {
        return smsList.size();
    }

    static class EachSmsView extends RecyclerView.ViewHolder{

        TextView sms_details, sms_body;

        public EachSmsView(@NonNull View itemView) {
            super(itemView);

            sms_details = itemView.findViewById(R.id.sms_deatils);
            sms_body = itemView.findViewById(R.id.sms_body);

            sms_body.setTextIsSelectable(true);

        }
    }
}

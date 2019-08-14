package com.creativesaif.expert_internet_admin.Notice;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creativesaif.expert_internet_admin.ClientList.ClientDetails;
import com.creativesaif.expert_internet_admin.ClientList.ClientDetailsEdit;
import com.creativesaif.expert_internet_admin.MySingleton;
import com.creativesaif.expert_internet_admin.NewsFeed.NewsDetails;
import com.creativesaif.expert_internet_admin.ProgressDialog;
import com.creativesaif.expert_internet_admin.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.EachNoticeView>{

    private Context context;
    private List<Notice> noticeList;


    public NoticeAdapter(Context context, List<Notice> noticeList) {
        this.context = context;
        this.noticeList = noticeList;
    }

    @NonNull
    @Override
    public EachNoticeView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.card_each_notice, null);
        return new EachNoticeView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EachNoticeView eachNoticeView, int i) {

        final Notice notice = noticeList.get(i);

        eachNoticeView.textViewSerial.setText("SL. "+notice.getId());
        eachNoticeView.textViewDate.setText(notice.getCreated_at());
        eachNoticeView.textViewNotice.setText(notice.getNotice());

        eachNoticeView.action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, NoticeEdit.class);
                i.putExtra("id",notice.getId());
                i.putExtra("notice",notice.getNotice());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return noticeList.size();
    }


    class EachNoticeView extends RecyclerView.ViewHolder {

        TextView textViewSerial, textViewDate, textViewNotice;
        Button action;

        private EachNoticeView(@NonNull View itemView) {
            super(itemView);

            textViewSerial = itemView.findViewById(R.id.notice_serial_no);
            textViewDate = itemView.findViewById(R.id.notice_date);
            textViewNotice = itemView.findViewById(R.id.notice_desc);

            action = itemView.findViewById(R.id.btn_action);
        }
    }

}

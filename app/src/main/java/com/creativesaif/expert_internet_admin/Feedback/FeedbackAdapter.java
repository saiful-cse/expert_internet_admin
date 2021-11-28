package com.creativesaif.expert_internet_admin.Feedback;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.creativesaif.expert_internet_admin.R;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.EachFeedback>{

    private Context context;
    private  List<Feedback> feedbackList;


    public FeedbackAdapter(Context context, List<Feedback> feedbackList) {
        this.context = context;
        this.feedbackList = feedbackList;
    }

    @NonNull
    @Override
    public EachFeedback onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.card_each_feedback, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rootView.setLayoutParams(lp);
        return new EachFeedback(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull EachFeedback eachFeedback, int i) {

        final Feedback feedback = feedbackList.get(i);

        eachFeedback.textViewSl.setText("#ID: "+feedback.getId()+"--C.ID: "+feedback.getClient_id()+"\nTime: "+feedback.getCreated_at());
        eachFeedback.textViewFeedback.setText(feedback.getFeedback());
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    class EachFeedback extends RecyclerView.ViewHolder {

        TextView textViewSl, textViewFeedback;


        EachFeedback(@NonNull View itemView) {
            super(itemView);

            textViewSl = itemView.findViewById(R.id.feedbackSl);
            textViewFeedback = itemView.findViewById(R.id.feedbackFeedback);
        }
    }
}

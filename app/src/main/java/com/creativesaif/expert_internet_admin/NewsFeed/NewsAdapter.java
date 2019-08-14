package com.creativesaif.expert_internet_admin.NewsFeed;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.creativesaif.expert_internet_admin.MainActivity;
import com.creativesaif.expert_internet_admin.R;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.EachNewsView>{

    private Context context;
    private List<News> newsList;

    public NewsAdapter(Context context, List<News> newsList) {
        this.context = context;
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public EachNewsView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.card_each_news, null);
        return new EachNewsView(view);

    }

    @Override
    public void onBindViewHolder(@NonNull EachNewsView eachNewsView, int i) {

        News news = newsList.get(i);

        Glide.with(this.context)
                .load(context.getString(R.string.base_url)+context.getString(R.string.news_image_path)+news.getImage_path())
                .error(R.drawable.ic_menu_gallery)
                .into(eachNewsView.imageView);

        eachNewsView.title.setText(news.getTitle());
        eachNewsView.description.setText(news.getDescription());
        eachNewsView.created_at.setText(news.getCreated_at());

    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }


    class EachNewsView extends RecyclerView.ViewHolder{
        List<News> newsListViewHolder;

        ImageView imageView;
        TextView title, description, created_at;
        Button readmore;

        private EachNewsView(@NonNull View itemView) {
            super(itemView);
            newsListViewHolder = newsList;

            imageView = itemView.findViewById(R.id.news_imageView);
            title = itemView.findViewById(R.id.news_title);
            description = itemView.findViewById(R.id.news_desc);
            created_at = itemView.findViewById(R.id.news_created_at);

            readmore = itemView.findViewById(R.id.btn_readmore);

            readmore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context, "Item Clicked", Toast.LENGTH_LONG).show();

                    int position = getAdapterPosition();

                    News news = newsListViewHolder.get(position);
                    Intent i = new Intent(context, NewsDetails.class);
                    i.putExtra("news",news);
                    context.startActivity(i);
                }
            });

        }
    }
}

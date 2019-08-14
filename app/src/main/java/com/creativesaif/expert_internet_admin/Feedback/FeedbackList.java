package com.creativesaif.expert_internet_admin.Feedback;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creativesaif.expert_internet_admin.MySingleton;
import com.creativesaif.expert_internet_admin.NewsFeed.News;
import com.creativesaif.expert_internet_admin.NewsFeed.NewsAdapter;
import com.creativesaif.expert_internet_admin.Notice.Notice;
import com.creativesaif.expert_internet_admin.Notice.NoticeRead;
import com.creativesaif.expert_internet_admin.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FeedbackList extends AppCompatActivity {

    private String last_id = "0";
    private boolean isLoading = true;

    // initialize adapter and data structure here
    private FeedbackAdapter feedbackAdapter;
    private ArrayList<Feedback> feedbackArrayList;

    RecyclerView recyclerView;

    LinearLayout linearLayout;

    //refresh posts
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*
        id initialize here
         */
        //assign all objects to avoid nullPointerException
        feedbackArrayList = new ArrayList<>();
        feedbackAdapter = new FeedbackAdapter(this,feedbackArrayList);

        recyclerView = findViewById(R.id.recyclerViewFeedback);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        //now set adapter to recyclerView
        recyclerView.setAdapter(feedbackAdapter);

        linearLayout = findViewById(R.id.progress_layout);

        swipeRefreshLayout = findViewById(R.id.feedback_refresh);


        //if internet is connected, then posts is load from server
        if (isNetworkConnected()){

            feedback_load();

        }else{
            Snackbar.make(findViewById(android.R.id.content),"Please!! Check Internet Connection or Try again later.",Snackbar.LENGTH_LONG).show();
        }

        //reload or refresh posts
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isNetworkConnected()){

                    Toast.makeText(FeedbackList.this,"Please!! Check internet connection.",Toast.LENGTH_SHORT).show();

                }else if(!isLoading){
                    Toast.makeText(FeedbackList.this,"One request is being process, Try again later.",Toast.LENGTH_SHORT).show();
                }
                else{
                    feedback_load();
                }
            }
        });

        // here add a recyclerView listener, to listen to scrolling,
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            //this is the ONLY method that we need, ignore the rest
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    // Recycle view scrolling downwards...
                    // this if statement detects when user reaches the end of recyclerView, this is only time we should load more
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                        // remember "!" is the same as "== false"
                        // here we are now allowed to load more, but we need to be careful
                        // we must check if itShouldLoadMore variable is true [unlocked]
                        if (isLoading && isNetworkConnected()) {
                            more_feedback_load();
                        }
                    }

                }
            }
        });

    }
    //Internet connection check
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    void progressEnable()
    {
        linearLayout.setVisibility(View.VISIBLE);
    }

    void progressDisable()
    {
        linearLayout.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void feedback_load()
    {
        swipeRefreshLayout.setRefreshing(true);
        isLoading = false;
        String url = getString(R.string.base_url)+getString(R.string.feedback_read);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Toast.makeText(MainActivity.this,response,Toast.LENGTH_SHORT).show();

                isLoading = true;
                swipeRefreshLayout.setRefreshing(false);

                try{

                    feedbackArrayList.clear();
                    JSONObject jsonObject = new JSONObject(response);

                    boolean m = jsonObject.has("message");
                    if (m)
                    {
                        String message = jsonObject.getString("message");
                        Toast.makeText(FeedbackList.this,message,Toast.LENGTH_SHORT).show();

                    }else
                    {
                        JSONArray jsonArray = jsonObject.getJSONArray("feedback");

                        for (int i=0; i<=jsonArray.length(); i++)
                        {

                            Feedback feedback = new Feedback();

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            feedback.setId(jsonObject1.getString("id"));
                            feedback.setClient_id(jsonObject1.getString("client_id"));
                            feedback.setCreated_at(jsonObject1.getString("created_at"));
                            feedback.setFeedback(jsonObject1.getString("feedback"));


                            feedbackArrayList.add(feedback);

                            last_id = jsonObject1.getString("id");

                            feedbackAdapter.notifyDataSetChanged();
                        }

                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isLoading = true;
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(FeedbackList.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance().addToRequestQueue(stringRequest);

    }

    public void more_feedback_load()
    {
        progressEnable();
        isLoading = false;
        String url = getString(R.string.base_url)+getString(R.string.feedback_read)+"?last_id="+last_id;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Toast.makeText(MainActivity.this,response,Toast.LENGTH_SHORT).show();

                isLoading = true;
                progressDisable();

                try{

                    JSONObject jsonObject = new JSONObject(response);

                    boolean m = jsonObject.has("message");
                    if (m)
                    {
                        String message = jsonObject.getString("message");
                        Toast.makeText(FeedbackList.this,message,Toast.LENGTH_SHORT).show();

                    }else
                    {
                        JSONArray jsonArray = jsonObject.getJSONArray("feedback");

                        for (int i=0; i<=jsonArray.length(); i++)
                        {

                            Feedback feedback = new Feedback();

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            feedback.setId(jsonObject1.getString("id"));
                            feedback.setClient_id(jsonObject1.getString("client_id"));
                            feedback.setCreated_at(jsonObject1.getString("created_at"));
                            feedback.setFeedback(jsonObject1.getString("feedback"));


                            feedbackArrayList.add(feedback);

                            last_id = jsonObject1.getString("id");

                            feedbackAdapter.notifyDataSetChanged();
                        }

                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isLoading = true;
                progressDisable();
                Toast.makeText(FeedbackList.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance().addToRequestQueue(stringRequest);

    }
}

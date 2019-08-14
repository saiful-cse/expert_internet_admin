package com.creativesaif.expert_internet_admin.Notice;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creativesaif.expert_internet_admin.ClientList.ClientList;
import com.creativesaif.expert_internet_admin.MainActivity;
import com.creativesaif.expert_internet_admin.MySingleton;
import com.creativesaif.expert_internet_admin.NewsFeed.News;
import com.creativesaif.expert_internet_admin.NewsFeed.NewsAdapter;
import com.creativesaif.expert_internet_admin.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NoticeRead extends AppCompatActivity {

    private String last_id = "0";
    private boolean isLoading = true;

    // initialize adapter and data structure here
    private NoticeAdapter noticeAdapter;
    private ArrayList<Notice> noticeArrayList;

    RecyclerView recyclerView;

    LinearLayout linearLayout;

    //refresh posts
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_read);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        /*
        id initialize here
         */
        //assign all objects to avoid nullPointerException
        noticeArrayList = new ArrayList<>();
        noticeAdapter = new NoticeAdapter(this,noticeArrayList);

        recyclerView = findViewById(R.id.recyclerViewNotice);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        //now set adapter to recyclerView
        recyclerView.setAdapter(noticeAdapter);

        linearLayout = findViewById(R.id.progress_layout);

        swipeRefreshLayout = findViewById(R.id.notice_refresh);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                startActivity(new Intent(NoticeRead.this, NoticeCreate.class));
            }
        });

        //if internet is connected, then posts is load from server
        if (isNetworkConnected()){

            notice_load();

        }else{
            Snackbar.make(findViewById(android.R.id.content),"Please!! Check Internet Connection or Try again later.",Snackbar.LENGTH_LONG).show();
        }

        //reload or refresh posts
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isNetworkConnected()){

                    Toast.makeText(NoticeRead.this,"Please!! Check internet connection.",Toast.LENGTH_SHORT).show();

                }else if(!isLoading){
                    Toast.makeText(NoticeRead.this,"One request is being process, Try again later.",Toast.LENGTH_SHORT).show();
                }
                else{
                    notice_load();
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
                            more_notice_load();
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

    public void notice_load()
    {
        swipeRefreshLayout.setRefreshing(true);
        isLoading = false;
        String url = getString(R.string.base_url)+getString(R.string.all_notice_read);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Toast.makeText(MainActivity.this,response,Toast.LENGTH_SHORT).show();

                isLoading = true;
                swipeRefreshLayout.setRefreshing(false);

                try{

                    noticeArrayList.clear();
                    JSONObject jsonObject = new JSONObject(response);

                    boolean m = jsonObject.has("message");
                    if (m)
                    {
                        String message = jsonObject.getString("message");
                        Toast.makeText(NoticeRead.this,message,Toast.LENGTH_SHORT).show();

                    }else
                    {
                        JSONArray jsonArray = jsonObject.getJSONArray("notice");

                        for (int i=0; i<=jsonArray.length(); i++)
                        {

                            Notice notice = new Notice();

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            notice.setId(jsonObject1.getString("id"));
                            notice.setCreated_at(jsonObject1.getString("created_at"));
                            notice.setNotice(jsonObject1.getString("notice"));


                            noticeArrayList.add(notice);

                            last_id = jsonObject1.getString("id");

                            noticeAdapter.notifyDataSetChanged();
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
                Toast.makeText(NoticeRead.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance().addToRequestQueue(stringRequest);

    }

    public void more_notice_load()
    {
        progressEnable();
        isLoading = false;
        String url = getString(R.string.base_url)+getString(R.string.all_notice_read)+"?last_id="+last_id;;

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
                        Toast.makeText(NoticeRead.this,message,Toast.LENGTH_SHORT).show();

                    }else
                    {
                        JSONArray jsonArray = jsonObject.getJSONArray("notice");

                        for (int i=0; i<=jsonArray.length(); i++)
                        {

                            Notice notice = new Notice();

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            notice.setId(jsonObject1.getString("id"));
                            notice.setCreated_at(jsonObject1.getString("created_at"));
                            notice.setNotice(jsonObject1.getString("notice"));


                            noticeArrayList.add(notice);

                            last_id = jsonObject1.getString("id");

                            noticeAdapter.notifyDataSetChanged();
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
                Toast.makeText(NoticeRead.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance().addToRequestQueue(stringRequest);

    }

}

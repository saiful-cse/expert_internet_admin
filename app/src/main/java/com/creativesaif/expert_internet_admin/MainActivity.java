package com.creativesaif.expert_internet_admin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creativesaif.expert_internet_admin.ClientList.ClientList;
import com.creativesaif.expert_internet_admin.ClientList.ClientReg;
import com.creativesaif.expert_internet_admin.ClientList.ClientRegUpdate;
import com.creativesaif.expert_internet_admin.Dashboard.Dashboard;
import com.creativesaif.expert_internet_admin.Feedback.FeedbackList;
import com.creativesaif.expert_internet_admin.NewsFeed.News;
import com.creativesaif.expert_internet_admin.NewsFeed.NewsAdapter;
import com.creativesaif.expert_internet_admin.Notice.NoticeRead;
import com.creativesaif.expert_internet_admin.NewsFeed.NewsAdd;
import com.creativesaif.expert_internet_admin.Sms.SmsHistory;
import com.creativesaif.expert_internet_admin.TransactionList.TransactionList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String last_id = "0";
    private boolean isLoading = true;

    // initialize adapter and data structure here
    private NewsAdapter newsAdapter;
    private ArrayList<News> newsArrayList;

    RecyclerView recyclerView;

    LinearLayout linearLayout;

    //refresh posts
    SwipeRefreshLayout swipeRefreshLayout;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                startActivity(new Intent(MainActivity.this, NewsAdd.class));
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        /*
        id initialize here
         */
        //assign all objects to avoid nullPointerException
        newsArrayList = new ArrayList<>();
        newsAdapter = new NewsAdapter(this,newsArrayList);

        recyclerView = findViewById(R.id.recyclerViewMain);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        //now set adapter to recyclerView
        recyclerView.setAdapter(newsAdapter);

        linearLayout = findViewById(R.id.progress_layout);

        swipeRefreshLayout = findViewById(R.id.post_refresh);

        sharedPreferences = getApplicationContext().getSharedPreferences("users", MODE_PRIVATE);

        //if internet is connected, then posts is load from server
        if (isNetworkConnected()){

            news_load();

        }else{
            TextView textView = findViewById(R.id.warning_viw);
            textView.setVisibility(View.VISIBLE);
        }

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
                            more_news_load();
                        }
                    }

                }
            }
        });


        //reload or refresh posts
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isNetworkConnected() && isLoading){
                    news_load();

                }
                else{
                    Snackbar.make(findViewById(android.R.id.content),"Please!! Check internet connection.",Snackbar.LENGTH_LONG).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

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
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            MainActivity.super.onBackPressed();
                            sharedPreferences.edit().clear().apply();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        String admin_id = sharedPreferences.getString("admin_id", null);


        if (id == R.id.nav_dashboard) {

            assert admin_id != null;
            if(admin_id.equals("9161") || admin_id.equals("8991")) {

                startActivity(new Intent(MainActivity.this, Dashboard.class));

            }else {
                Toast.makeText(getApplicationContext(), "You are not permitted to view", Toast.LENGTH_LONG).show();
            }

        }else if (id == R.id.nav_client_reg) {

            startActivity(new Intent(MainActivity.this, ClientReg.class));

        } else if (id == R.id.nav_clientlist) {

            startActivity(new Intent(MainActivity.this, ClientList.class));

        } else if (id == R.id.nav_search) {

            //startActivity(new Intent(MainActivity.this, SearchPage.class));

        } else if (id == R.id.nav_notice) {
            startActivity(new Intent(MainActivity.this, NoticeRead.class));

        } else if (id == R.id.nav_feedback) {

            startActivity(new Intent(MainActivity.this, FeedbackList.class));

        } else if (id == R.id.nav_txnlist) {

            startActivity(new Intent(MainActivity.this, TransactionList.class));
        }else if (id == R.id.nav_smsHistory) {

            startActivity(new Intent(MainActivity.this, SmsHistory.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*
    News load via recyclerView
     */

    public void news_load()
    {
        swipeRefreshLayout.setRefreshing(true);
        isLoading = false;
        String url = getString(R.string.base_url)+getString(R.string.news_read);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                isLoading = true;
                swipeRefreshLayout.setRefreshing(false);

                try{

                    newsArrayList.clear();
                    JSONObject jsonObject = new JSONObject(response);

                    boolean m = jsonObject.has("message");
                    if (m)
                    {
                        String message = jsonObject.getString("message");
                        Toast.makeText(MainActivity.this,message,Toast.LENGTH_SHORT).show();

                    }else
                    {
                        JSONArray jsonArray = jsonObject.getJSONArray("news");

                        for (int i=0; i<=jsonArray.length(); i++)
                        {
                            News news  = new News();

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            news.setId(jsonObject1.getString("id"));
                            news.setTitle(jsonObject1.getString("title"));
                            news.setDescription(jsonObject1.getString("description"));
                            news.setImage_path(jsonObject1.getString("image_path"));
                            news.setCreated_at(jsonObject1.getString("created_at"));

                            newsArrayList.add(news);

                            last_id = jsonObject1.getString("id");

                            newsAdapter.notifyDataSetChanged();
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
                Toast.makeText(MainActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance().addToRequestQueue(stringRequest);

    }


    /*
    more News load via recyclerView
     */

    public void more_news_load()
    {
        progressEnable();
        isLoading = false;
        String url = getString(R.string.base_url)+getString(R.string.news_read)+"?last_id="+last_id;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                isLoading = true;
                progressDisable();

                try{

                    JSONObject jsonObject = new JSONObject(response);

                    boolean m = jsonObject.has("message");
                    if (m)
                    {
                        String message = jsonObject.getString("message");
                        Toast.makeText(MainActivity.this,message,Toast.LENGTH_SHORT).show();

                    }else
                    {
                        JSONArray jsonArray = jsonObject.getJSONArray("news");

                        for (int i=0; i<=jsonArray.length(); i++)
                        {
                            News news  = new News();

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            news.setTitle(jsonObject1.getString("title"));
                            news.setDescription(jsonObject1.getString("description"));
                            news.setImage_path(jsonObject1.getString("image_path"));
                            news.setCreated_at(jsonObject1.getString("created_at"));

                            newsArrayList.add(news);

                            last_id = jsonObject1.getString("id");

                            newsAdapter.notifyDataSetChanged();
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
                Toast.makeText(MainActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance().addToRequestQueue(stringRequest);

    }

    //Internet connection check
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }



}

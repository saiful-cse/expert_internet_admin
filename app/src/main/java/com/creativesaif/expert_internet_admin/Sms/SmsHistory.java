package com.creativesaif.expert_internet_admin.Sms;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creativesaif.expert_internet_admin.ClientList.Client;
import com.creativesaif.expert_internet_admin.ClientList.ClientAdapter;
import com.creativesaif.expert_internet_admin.MySingleton;
import com.creativesaif.expert_internet_admin.R;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SmsHistory extends AppCompatActivity {

    RecyclerView recyclerView;
    private SmsAdapter smsAdapter;
    private ArrayList<Sms> smsArrayList;
    private String last_id = "0";
    boolean isLoading = true;
    ProgressBar progressBarMore;

    private ShimmerFrameLayout mShimmerViewContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_history);
        mShimmerViewContainer = findViewById(R.id.shimmer_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        smsArrayList = new ArrayList<>();
        smsAdapter = new SmsAdapter(this, smsArrayList);

        recyclerView = findViewById(R.id.recyclerViewSmsHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(smsAdapter);

        progressBarMore = findViewById(R.id.progressBarMore);

        if (!isNetworkConnected()){

            Toast.makeText(getApplicationContext(),"Please!! Check internet connection.",Toast.LENGTH_SHORT).show();

        }else if(!isLoading){
            Toast.makeText(getApplicationContext(),"One request is being process, Try again later.",Toast.LENGTH_SHORT).show();
        }
        else{
            load_sms_history();
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
                            more_load_sms_history();
                        }
                    }

                }
            }
        });

    }


    private void load_sms_history() {

        mShimmerViewContainer.startShimmer();
        isLoading = false;
        String url = getString(R.string.base_url)+getString(R.string.sms_history);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();

                isLoading = true;
                mShimmerViewContainer.stopShimmer();
                mShimmerViewContainer.setVisibility(View.GONE);

                try{

                    smsArrayList.clear();
                    JSONObject jsonObject = new JSONObject(response);

                    boolean m = jsonObject.has("message");
                    if (m)
                    {
                        String message = jsonObject.getString("message");
                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

                    }else
                    {
                        JSONArray jsonArray = jsonObject.getJSONArray("sms_history");

                        for (int i=0; i<=jsonArray.length(); i++)
                        {
                            Sms sms = new Sms();

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            sms.setMsg_id(jsonObject1.getString("msg_id"));
                            sms.setClient_id(jsonObject1.getString("client_id"));
                            sms.setMsg_body(jsonObject1.getString("msg_body"));
                            sms.setTag(jsonObject1.getString("tag"));
                            sms.setCreated_at(jsonObject1.getString("created_at"));

                            smsArrayList.add(sms);

                            last_id = jsonObject1.getString("msg_id");

                            smsAdapter.notifyDataSetChanged();
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
               mShimmerViewContainer.stopShimmer();
                mShimmerViewContainer.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance().addToRequestQueue(stringRequest);
    }


    private void more_load_sms_history() {

        progressBarMore.setVisibility(View.VISIBLE);
        isLoading = false;
        String url = getString(R.string.base_url)+getString(R.string.sms_history)+"?last_id="+last_id;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                isLoading = true;
                progressBarMore.setVisibility(View.GONE);
                try{

                    JSONObject jsonObject = new JSONObject(response);

                    boolean m = jsonObject.has("message");
                    if (m)
                    {
                        String message = jsonObject.getString("message");
                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

                    }else
                    {
                        JSONArray jsonArray = jsonObject.getJSONArray("sms_history");

                        for (int i=0; i<=jsonArray.length(); i++)
                        {
                            Sms sms = new Sms();

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            sms.setMsg_id(jsonObject1.getString("msg_id"));
                            sms.setClient_id(jsonObject1.getString("client_id"));
                            sms.setMsg_body(jsonObject1.getString("msg_body"));
                            sms.setTag(jsonObject1.getString("tag"));
                            sms.setCreated_at(jsonObject1.getString("created_at"));

                            smsArrayList.add(sms);

                            last_id = jsonObject1.getString("msg_id");

                            smsAdapter.notifyDataSetChanged();
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
                progressBarMore.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance().addToRequestQueue(stringRequest);
    }

    //Internet connection check
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
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

    @Override
    public void onResume() {
        super.onResume();
       mShimmerViewContainer.startShimmer();
    }

    @Override
    protected void onPause() {
        mShimmerViewContainer.stopShimmer();
        super.onPause();
    }
}

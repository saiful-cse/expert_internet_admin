package com.creativesaif.expert_internet_admin.ClientList;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creativesaif.expert_internet_admin.MySingleton;
import com.creativesaif.expert_internet_admin.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AleartList extends Fragment {

    RecyclerView recyclerView;
    private ClientAdapter clientAdapter;
    private ArrayList<Client> clientArrayList;
    private String last_id = "0";
    boolean isLoading = true;

    public String total;

    LinearLayout linearLayout;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_aleart_list, container, false);

        swipeRefreshLayout = view.findViewById(R.id.alert_refresh);
        clientArrayList = new ArrayList<>();
        clientAdapter = new ClientAdapter(getActivity(), clientArrayList);
        linearLayout = view.findViewById(R.id.progress_layout);

        recyclerView = view.findViewById(R.id.recyclerViewAlertClient);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(clientAdapter);

        /*
        if (!isLoading){
            Toast.makeText(getContext(),"One request is being process, Try again later.",Toast.LENGTH_SHORT).show();

        }else if(isNetworkConnected()){
            load_alert_client();
        }else{
            Toast.makeText(getContext(),"Please!! Check internet connection.",Toast.LENGTH_SHORT).show();
            isLoading = true;
        }
        */

        //reload or refresh posts
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isNetworkConnected()){

                    Toast.makeText(getContext(),"Please!! Check internet connection.",Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);

                }else if(!isLoading){
                    Toast.makeText(getContext(),"One request is being process, Try again later.",Toast.LENGTH_SHORT).show();
                }
                else{
                    load_alert_client();
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
                            more_load_alert_client();
                        }
                    }

                }
            }
        });

        return view;
    }

    private void load_alert_client() {

        swipeRefreshLayout.setRefreshing(true);
        isLoading = false;
        String url = getString(R.string.base_url)+getString(R.string.alert_client_read);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Toast.makeText(getContext(),response,Toast.LENGTH_SHORT).show();

                isLoading = true;
                swipeRefreshLayout.setRefreshing(false);

                try{

                    clientArrayList.clear();
                    JSONObject jsonObject = new JSONObject(response);

                    total = jsonObject.getString("total");

                    Toast.makeText(getContext(),"Total alert client is: "+total,Toast.LENGTH_LONG).show();

                    boolean m = jsonObject.has("message");
                    if (m)
                    {
                        String message = jsonObject.getString("message");
                        Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();

                    }else
                    {
                        JSONArray jsonArray = jsonObject.getJSONArray("alert_client");

                        for (int i=0; i<=jsonArray.length(); i++)
                        {
                            Client client = new Client();

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            client.setId(jsonObject1.getString("id"));
                            client.setName(jsonObject1.getString("name"));
                            client.setPhone(jsonObject1.getString("phone"));
                            client.setArea(jsonObject1.getString("area"));

                            clientArrayList.add(client);

                            last_id = jsonObject1.getString("id");

                            clientAdapter.notifyDataSetChanged();
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
                Toast.makeText(getContext(),error.toString(),Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance().addToRequestQueue(stringRequest);
    }


    private void more_load_alert_client() {

        progressEnable();
        isLoading = false;
        String url = getString(R.string.base_url)+getString(R.string.alert_client_read)+"?last_id="+last_id;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Toast.makeText(getContext(),response,Toast.LENGTH_SHORT).show();

                isLoading = true;
                progressDisable();

                try{

                    JSONObject jsonObject = new JSONObject(response);

                    boolean m = jsonObject.has("message");
                    if (m)
                    {
                        String message = jsonObject.getString("message");
                        Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();

                    }else
                    {
                        JSONArray jsonArray = jsonObject.getJSONArray("alert_client");

                        for (int i=0; i<=jsonArray.length(); i++)
                        {
                            Client client = new Client();

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            client.setId(jsonObject1.getString("id"));
                            client.setName(jsonObject1.getString("name"));
                            client.setPhone(jsonObject1.getString("phone"));
                            client.setArea(jsonObject1.getString("area"));

                            clientArrayList.add(client);

                            last_id = jsonObject1.getString("id");

                            clientAdapter.notifyDataSetChanged();
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
                Toast.makeText(getContext(),error.toString(),Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance().addToRequestQueue(stringRequest);
    }

    //Internet connection check
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

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
}

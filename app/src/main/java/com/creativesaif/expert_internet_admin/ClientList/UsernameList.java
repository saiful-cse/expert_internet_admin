package com.creativesaif.expert_internet_admin.ClientList;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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

public class UsernameList extends Fragment {

    RecyclerView recyclerView;
    private ClientAdapter clientAdapter;
    private ArrayList<Client> clientArrayList;
    private String last_id = "0";
    boolean isLoading = true;

    LinearLayout linearLayout;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_active_list, container, false);

        swipeRefreshLayout = view.findViewById(R.id.active_refresh);
        clientArrayList = new ArrayList<>();
        clientAdapter = new ClientAdapter(getActivity(), clientArrayList);
        linearLayout = view.findViewById(R.id.progress_layout);

        recyclerView = view.findViewById(R.id.recyclerViewActiveClient);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(clientAdapter);

        //load_username();

        //reload or refresh posts
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isNetworkConnected()){

                    Toast.makeText(getContext(),"Please!! Check internet connection.",Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);

                } else{
                    load_username();
                }
            }
        });


        return view;
    }

    private void load_username() {

        swipeRefreshLayout.setRefreshing(true);
        isLoading = false;
        String url = getString(R.string.base_url)+getString(R.string.usernamelist);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Toast.makeText(getContext(),response,Toast.LENGTH_SHORT).show();

                swipeRefreshLayout.setRefreshing(false);

                try{

                    clientArrayList.clear();
                    JSONObject jsonObject = new JSONObject(response);

                    boolean m = jsonObject.has("message");
                    if (m)
                    {
                        String message = jsonObject.getString("message");
                        Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();

                    }else
                    {
                        JSONArray jsonArray = jsonObject.getJSONArray("username");

                        for (int i=0; i<=jsonArray.length(); i++)
                        {
                            Client client = new Client();

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            client.setName(jsonObject1.getString("name")+" ("+jsonObject1.getString("mode")+")");
                            client.setId(jsonObject1.getString("id"));
                            client.setPhone(jsonObject1.getString("phone"));
                            client.setArea(jsonObject1.getString("area"));
                            client.setUsername(jsonObject1.getString("username"));

                            clientArrayList.add(client);

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

    //Internet connection check
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}

package com.creativesaif.expert_internet_admin.TaskList;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creativesaif.expert_internet_admin.Adapter.ClientAdapter;
import com.creativesaif.expert_internet_admin.Adapter.TaskAdapter;
import com.creativesaif.expert_internet_admin.Login;
import com.creativesaif.expert_internet_admin.Model.Client;
import com.creativesaif.expert_internet_admin.Model.ClientWrapper;
import com.creativesaif.expert_internet_admin.MySingleton;
import com.creativesaif.expert_internet_admin.Network.ApiInterface;
import com.creativesaif.expert_internet_admin.Network.RetrofitApiClient;
import com.creativesaif.expert_internet_admin.R;
import com.creativesaif.expert_internet_admin.TransactionList.Transaction;
import com.creativesaif.expert_internet_admin.TransactionList.TransactionList;
import com.creativesaif.expert_internet_admin.URL_config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;

public class PendingTask extends Fragment {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private ArrayList<Task> taskArrayList;
    private SharedPreferences preferences;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.activity_task_view, container, false);

        preferences = view.getContext().getSharedPreferences("users", MODE_PRIVATE);
        taskArrayList = new ArrayList<>();
        taskAdapter = new TaskAdapter(this.getActivity(), taskArrayList);
        swipeRefreshLayout = view.findViewById(R.id.layout_refresh);
        recyclerView = view.findViewById(R.id.recyclerViewTask);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(taskAdapter);

        if (isConnected()){
            load_pending_task();
        }else{
            Toast.makeText(getActivity(),"Check Internet Connection",Toast.LENGTH_LONG).show();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnected()){
                    load_pending_task();
                }else{
                    Toast.makeText(getActivity(),"Check Internet Connection",Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
    }

    private void load_pending_task() {

        swipeRefreshLayout.setRefreshing(true);
        String url = URL_config.BASE_URL+URL_config.TASK_PENDING+preferences.getString("employee_id", null);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(String response) {

                //Toast.makeText(TransactionList.this,response,Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);

                try{

                    taskArrayList.clear();
                    JSONObject jsonObject = new JSONObject(response);

                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("200")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("tasks");

                        for (int i=0; i<=jsonArray.length(); i++)
                        {
                            Task task = new Task();

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            task.setCreated_at(jsonObject1.getString("created_at"));
                            task.setId(jsonObject1.getString("id"));
                            task.setDescription(jsonObject1.getString("description"));
                            task.setCompleted(jsonObject1.getString("completed"));
                            task.setAssign_by(jsonObject1.getString("assign_by"));
                            task.setAssign_on(jsonObject1.getString("assign_on"));

                            taskArrayList.add(task);

                            taskAdapter.notifyDataSetChanged();
                        }

                    }else{
                        Toast.makeText(getActivity(),message.toString(),Toast.LENGTH_LONG).show();
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        MySingleton.getInstance().addToRequestQueue(stringRequest);
    }


    //Internet connection check
    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

}

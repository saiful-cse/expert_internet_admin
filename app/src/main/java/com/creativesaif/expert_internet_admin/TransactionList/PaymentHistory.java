package com.creativesaif.expert_internet_admin.TransactionList;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creativesaif.expert_internet_admin.ClientList.ClientDetails;
import com.creativesaif.expert_internet_admin.Login;
import com.creativesaif.expert_internet_admin.MySingleton;
import com.creativesaif.expert_internet_admin.ProgressDialog;
import com.creativesaif.expert_internet_admin.R;
import com.creativesaif.expert_internet_admin.URL_config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PaymentHistory extends AppCompatActivity {

    private String id;
    private TransactionAdapter transactionAdapter;
    private ArrayList<Transaction> transactionArrayList;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        id = getIntent().getStringExtra("id");
        transactionArrayList = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(this,transactionArrayList);

        recyclerView = findViewById(R.id.recyclerViewTnxRow);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        //now set adapter to recyclerView
        recyclerView.setAdapter(transactionAdapter);

        if (id == null){
           finish();
        }else if(!isNetworkConnected()){
            Toast.makeText(getApplicationContext(), "Please!! Check internet connection.", Toast.LENGTH_SHORT).show();

        }else{
            load_payment_details(id);
        }

    }

    private void load_payment_details(String got_id) {

        progressDialog.showDialog();
        String url = URL_config.BASE_URL + URL_config.PAYMENT_DETAILS +"?id="+got_id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{

                    progressDialog.hideDialog();
                    transactionArrayList.clear();
                    JSONObject jsonObject = new JSONObject(response);

                    boolean m = jsonObject.has("message");
                    if (m)
                    {
                        String message = jsonObject.getString("message");
                        Toast.makeText(PaymentHistory.this,message,Toast.LENGTH_LONG).show();

                    }else
                    {
                        JSONArray jsonArray = jsonObject.getJSONArray("txn_details");

                        for (int i=0; i<=jsonArray.length(); i++)
                        {
                            Transaction transaction = new Transaction();

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            transaction.setDate(jsonObject1.getString("date"));
                            transaction.setTxn_id(jsonObject1.getString("txn_id"));
                            transaction.setCredit(jsonObject1.getString("amount"));
                            transaction.setDetails(jsonObject1.getString("details"));

                            transactionArrayList.add(transaction);

                            transactionAdapter.notifyDataSetChanged();

                        }

                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                Toast.makeText(PaymentHistory.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Internet connection check
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
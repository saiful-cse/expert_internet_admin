package com.creativesaif.expert_internet_admin.Note;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creativesaif.expert_internet_admin.Adapter.SalaryAdapter;
import com.creativesaif.expert_internet_admin.Login;
import com.creativesaif.expert_internet_admin.MainActivity;
import com.creativesaif.expert_internet_admin.Model.Client;
import com.creativesaif.expert_internet_admin.Model.Salary;
import com.creativesaif.expert_internet_admin.Model.SalaryWrapper;
import com.creativesaif.expert_internet_admin.MySingleton;
import com.creativesaif.expert_internet_admin.Network.ApiInterface;
import com.creativesaif.expert_internet_admin.Network.RetrofitApiClient;
import com.creativesaif.expert_internet_admin.ProgressDialog;
import com.creativesaif.expert_internet_admin.R;
import com.creativesaif.expert_internet_admin.Salary.AddSalary;
import com.creativesaif.expert_internet_admin.Salary.SalaryList;
import com.creativesaif.expert_internet_admin.URL_config;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;


public class NoteView extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;
    private TextView tvNote, tvupdateDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        sharedPreferences = getApplicationContext().getSharedPreferences("users", MODE_PRIVATE);
        progressDialog = new ProgressDialog(this);
        tvNote = findViewById(R.id.tvnote);
        tvupdateDate = findViewById(R.id.tvupdatedate);

        if (isConnected()){
            loadNote();
        }else{
            finish();
            Toast.makeText(NoteView.this, "No Internet Connection",Toast.LENGTH_LONG).show();
        }

        FloatingActionButton fab = findViewById(R.id.fabeditnote);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Objects.equals(sharedPreferences.getString("super_admin", null), "1")){
                    startActivity(new Intent(NoteView.this, NoteEdit.class));
                }
            }
        });
    }

    public void loadNote() {

        progressDialog.showDialog();
        String url = URL_config.BASE_URL+URL_config.NOTE_VIEW;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

               progressDialog.hideDialog();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("200")) {
                        tvupdateDate.setText("Last updated "+jsonObject.getString("updated_at"));
                        tvNote.setText(jsonObject.getString("note"));
                    }else{
                        Toast.makeText(NoteView.this,message,Toast.LENGTH_LONG).show();
                    }

                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                //Toast.makeText(Login.this,"Connection Error!! Try again",Toast.LENGTH_LONG).show();
                Toast.makeText(NoteView.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                map.put("jwt", sharedPreferences.getString("jwt", null));

                return map;

            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 8, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance().addToRequestQueue(stringRequest);
    }


    //Internet connection check
    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
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
}

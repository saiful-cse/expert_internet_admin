package com.creativesaif.expert_internet_admin.Notice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creativesaif.expert_internet_admin.ClientList.ClientDetails;
import com.creativesaif.expert_internet_admin.ClientList.ClientDetailsEdit;
import com.creativesaif.expert_internet_admin.Login;
import com.creativesaif.expert_internet_admin.Model.Client;
import com.creativesaif.expert_internet_admin.Model.DetailsWrapper;
import com.creativesaif.expert_internet_admin.MySingleton;
import com.creativesaif.expert_internet_admin.Network.ApiInterface;
import com.creativesaif.expert_internet_admin.Network.RetrofitApiClient;
import com.creativesaif.expert_internet_admin.ProgressDialog;
import com.creativesaif.expert_internet_admin.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class NoticeCreate extends AppCompatActivity {

    private EditText editTextActiveClientMsg, editTextNotice;
    private ProgressDialog progressDialog;
    private String jwt, activeClientMessage, notice;
    private SharedPreferences preferences;

    private ApiInterface apiInterface;
    private Client client;

    /*
    Area load from server
     */
    private RecyclerView mRecyclerView;

    private Button btnSelection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_create);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // ---------- notice ------------
        preferences = getApplicationContext().getSharedPreferences("users", MODE_PRIVATE);
        apiInterface = RetrofitApiClient.getClient().create(ApiInterface.class);
        client = new Client();
        jwt = preferences.getString("jwt", null);

        editTextNotice = findViewById(R.id.edNotice);
        progressDialog = new ProgressDialog(this);
        Button notice_Post = findViewById(R.id.btnNoticePost);

        notice_Post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notice = editTextNotice.getText().toString().trim();

                if (notice.isEmpty()){
                    Snackbar.make(findViewById(android.R.id.content),"Write a notice",Snackbar.LENGTH_LONG).show();

                }else if(!isNetworkConnected()){
                    Snackbar.make(findViewById(android.R.id.content),"Please check internet connection.",Snackbar.LENGTH_LONG).show();
                }else {
                    notice_create();
                }
            }
        });


        // ----- sms service for alert client -----

        Button buttonActiveSmsSend = findViewById(R.id.btnActiveSmsSend);

        Button btnBillExpireWarning = findViewById(R.id.btnbillwarning);
        btnBillExpireWarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                warningBillExpire();
            }
        });


        editTextActiveClientMsg = findViewById(R.id.edActiveClientSMs);
        buttonActiveSmsSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activeClientMessage = editTextActiveClientMsg.getText().toString().trim();
                jwt = preferences.getString("jwt", null);

                if (jwt == null ){
                    finish();
                    startActivity(new Intent(NoticeCreate.this, Login.class));

                } else if(activeClientMessage.isEmpty()){
                    Toast.makeText(NoticeCreate.this,"Write SMS",Toast.LENGTH_SHORT).show();

                }else if(!isNetworkConnected()){
                    Toast.makeText(NoticeCreate.this,"Check Internet Connection.",Toast.LENGTH_SHORT).show();

                }else{
                    //confirm dialog
                    warning_active_client_sms();
                }

            }
        });
    }

    public void billExpireWarningSend(Client mClient) {
        progressDialog.showDialog();
        Call<DetailsWrapper> call = apiInterface.bilExpireWarningSend(mClient);
        call.enqueue(new Callback<DetailsWrapper>() {
            @SuppressLint("ResourceType")
            @Override
            public void onResponse(Call<DetailsWrapper> call, retrofit2.Response<DetailsWrapper> response) {

                progressDialog.hideDialog();

                DetailsWrapper detailsWrapper = response.body();
                assert detailsWrapper != null;

                if (detailsWrapper.getStatus() == 401) {
                    //Go to phone verification step
                    loginWarningShow(detailsWrapper.getMessage());

                }if (detailsWrapper.getStatus() == 200) {
                    Toast.makeText(getApplicationContext(), detailsWrapper.getMessage(), Toast.LENGTH_LONG).show();
                    finish();

                }else{
                    warningShow(detailsWrapper.getMessage());
                    //Toast.makeText(getApplicationContext(), detailsWrapper.getMessage(), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<DetailsWrapper> call, Throwable t) {
                progressDialog.hideDialog();
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void activeClientSmsSend()
    {
        progressDialog.showDialog();
        String url = getString(R.string.base_url)+getString(R.string.active_client_sms_service);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.hideDialog();

                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("200")){
                        Toast.makeText(NoticeCreate.this, message,Toast.LENGTH_LONG).show();
                        finish();

                    }else if(status.equals("401")){

                        warningShow(message);

                    }else{
                        Toast.makeText(NoticeCreate.this, message,Toast.LENGTH_LONG).show();
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                Toast.makeText(NoticeCreate.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                map.put("jwt", jwt);
                map.put("message", activeClientMessage);
                return map;

            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance().addToRequestQueue(stringRequest);
    }

    //Internet connection check
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public void notice_create()
    {
        progressDialog.showDialog();
        String url = getString(R.string.base_url)+getString(R.string.notice_create);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Toast.makeText(NoticeEdit.this,response,Toast.LENGTH_SHORT).show();

                progressDialog.hideDialog();

                try{

                    JSONObject jsonObject = new JSONObject(response);

                    boolean m = jsonObject.has("message");
                    if (m)
                    {
                        String message = jsonObject.getString("message");
                        Toast.makeText(NoticeCreate.this,message,Toast.LENGTH_SHORT).show();
                        finish();

                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                Toast.makeText(NoticeCreate.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                map.put("notice", notice);
                return map;
            }
        };
        MySingleton.getInstance().addToRequestQueue(stringRequest);
    }


    public void warningBillExpire(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(true);
        alert.setTitle("সতর্কতা!!");
        alert.setMessage("যে সব ক্লায়েন্টদের বিলের মেয়াদ শেষ হতে ৩ দিন বাকি আছে তাদের ফোনে SMS যাবে।");
        alert.setIcon(R.drawable.ic_baseline_warning_24);

        alert.setPositiveButton("Ok, Sure", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                client.setJwt(jwt);
                billExpireWarningSend(client);
            }
        });

        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog dlg = alert.create();
        dlg.show();
    }

    public void warning_active_client_sms(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(true);
        alert.setTitle("সতর্কতা!!");
        alert.setMessage("সকল একটিভ ক্লায়েন্টদের ফোনে আপনার লিখা মেসেজ যাবে। আপনার মেসেজ সঠিক আছে কিনা চেক করে দেখুন অন্যথায় ভুল মেসেজে ক্লায়েন্ট বিভ্রান্তে পড়তে পারে।");
        alert.setIcon(R.drawable.ic_baseline_warning_24);

        alert.setPositiveButton("Ok, Sure", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                activeClientSmsSend();
            }
        });

        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog dlg = alert.create();
        dlg.show();
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

    public void loginWarningShow(String message){
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(this);
        alert.setCancelable(false);
        alert.setTitle("Warning!!");
        alert.setMessage(message);
        alert.setIcon(R.drawable.ic_baseline_warning_24);

        alert.setPositiveButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
                startActivity(new Intent(NoticeCreate.this, Login.class));
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        android.app.AlertDialog dlg = alert.create();
        dlg.show();
    }


    public void warningShow(String message){
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(this);
        alert.setCancelable(false);
        alert.setTitle("Warning!!");
        alert.setMessage(message);
        alert.setIcon(R.drawable.ic_baseline_warning_24);

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        android.app.AlertDialog dlg = alert.create();
        dlg.show();
    }
}

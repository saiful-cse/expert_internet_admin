package com.creativesaif.expert_internet_admin.Sms;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creativesaif.expert_internet_admin.Login;
import com.creativesaif.expert_internet_admin.Model.Client;
import com.creativesaif.expert_internet_admin.Model.DetailsWrapper;
import com.creativesaif.expert_internet_admin.MySingleton;
import com.creativesaif.expert_internet_admin.Network.ApiInterface;
import com.creativesaif.expert_internet_admin.Network.RetrofitApiClient;
import com.creativesaif.expert_internet_admin.ProgressDialog;
import com.creativesaif.expert_internet_admin.R;
import com.creativesaif.expert_internet_admin.URL_config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Url;

public class SmsCreate extends AppCompatActivity {

    private EditText editTextActiveClientMsg, editTextAreaMessage;
    private ProgressDialog progressDialog;
    private String jwt, activeClientMessage, areaMessage, selectedArea;
    private SharedPreferences preferences;
    private Spinner areaSpinner;
    private ApiInterface apiInterface;
    private Client client;
    private String admin_id;
    //Declaring Array List
    private ArrayList<String> areaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_create);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // ---------- notice ------------
        preferences = getApplicationContext().getSharedPreferences("users", MODE_PRIVATE);
        apiInterface = RetrofitApiClient.getClient().create(ApiInterface.class);
        client = new Client();
        jwt = preferences.getString("jwt", null);

        editTextAreaMessage = findViewById(R.id.edAreaSms);
        progressDialog = new ProgressDialog(this);
        Button areaSmsSend = findViewById(R.id.btnareasms);
        admin_id = preferences.getString("admin_id", null);

        areaSpinner = findViewById(R.id.areaListSpinner);
        areaList = new ArrayList<>();


        //Spinner item choice and click event
        areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // On selecting a spinner item
                selectedArea = parentView.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        area_load();
        areaSmsSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                areaMessage = editTextAreaMessage.getText().toString().trim();

                if (areaMessage.isEmpty()){
                    Snackbar.make(findViewById(android.R.id.content),"Write a SMS",Snackbar.LENGTH_LONG).show();

                }else if(selectedArea.equals("---") || selectedArea.isEmpty()){
                    Snackbar.make(findViewById(android.R.id.content),"Select an area",Snackbar.LENGTH_LONG).show();
                }
                else if(!isNetworkConnected()){
                    Snackbar.make(findViewById(android.R.id.content),"Please check internet connection.",Snackbar.LENGTH_LONG).show();
                }else {
                    areWiseMessage();
                }
            }
        });


        // ----- sms service for alert client -----

        Button buttonActiveSmsSend = findViewById(R.id.btnActiveSmsSend);
        Button buttonExpiredDisconnect = findViewById(R.id.btnbillexpirdisconnect);
        Button btnBillExpireWarning = findViewById(R.id.btnbillwarning);

        btnBillExpireWarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (jwt == null ){
                    finish();
                    startActivity(new Intent(SmsCreate.this, Login.class));

                } else if(!isNetworkConnected()){
                    Toast.makeText(SmsCreate.this,"Check Internet Connection.",Toast.LENGTH_SHORT).show();

                }else{
                    //confirm dialog
                    warningBillExpire();
                }
            }
        });

        buttonExpiredDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (jwt == null ){
                    finish();
                    startActivity(new Intent(SmsCreate.this, Login.class));

                } else if(!isNetworkConnected()){
                    Toast.makeText(SmsCreate.this,"Check Internet Connection.",Toast.LENGTH_SHORT).show();

                }else{
                    //confirm dialog
                    warningBillExpireDisconnect();
                }
            }
        });

        editTextActiveClientMsg = findViewById(R.id.edActiveClientSMs);
        buttonActiveSmsSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activeClientMessage = editTextActiveClientMsg.getText().toString().trim();
                jwt = preferences.getString("jwt", null);

                if(!admin_id.equals("9161")){
                    Toast.makeText(getApplicationContext(), "You don't have permission to send. In case you need to send, contact with Super Admin", Toast.LENGTH_LONG).show();

                } else if (jwt == null ){
                    finish();
                    startActivity(new Intent(SmsCreate.this, Login.class));

                } else if(activeClientMessage.isEmpty()){
                    Toast.makeText(SmsCreate.this,"Write SMS",Toast.LENGTH_SHORT).show();

                }else if(!isNetworkConnected()){
                    Toast.makeText(SmsCreate.this,"Check Internet Connection.",Toast.LENGTH_SHORT).show();

                }else{
                    //confirm dialog
                    warning_enabled_client_sms();
                }

            }
        });
    }

    public void billExpireWarningSend(Client mClient) {
        progressDialog.showDialog();
        Call<DetailsWrapper> call = apiInterface.bilExpiringWarningSms(mClient);
        call.enqueue(new Callback<DetailsWrapper>() {
            @SuppressLint("ResourceType")
            @Override
            public void onResponse(Call<DetailsWrapper> call, retrofit2.Response<DetailsWrapper> response) {

                progressDialog.hideDialog();

                DetailsWrapper detailsWrapper = response.body();
                assert detailsWrapper != null;

                if (detailsWrapper.getStatus() == 401) {
                    //Go to phone verification step
                    Intent intent = new Intent(SmsCreate.this, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

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

    public void billExpireDisconnect(Client mClient) {
        progressDialog.showDialog();
        Call<DetailsWrapper> call = apiInterface.expiredClientDisconnect(mClient);
        call.enqueue(new Callback<DetailsWrapper>() {
            @SuppressLint("ResourceType")
            @Override
            public void onResponse(Call<DetailsWrapper> call, retrofit2.Response<DetailsWrapper> response) {

                progressDialog.hideDialog();

                DetailsWrapper detailsWrapper = response.body();
                assert detailsWrapper != null;

                if (detailsWrapper.getStatus() == 401) {
                    //Go to phone verification step
                    Intent intent = new Intent(SmsCreate.this, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

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

    public void enabledClientSmsSend()
    {
        progressDialog.showDialog();
        String url = URL_config.BASE_URL+URL_config.ENABLE_CLIENT_SMS;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.hideDialog();

                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("200")){
                        Toast.makeText(SmsCreate.this, message,Toast.LENGTH_LONG).show();
                        finish();

                    }else if(status.equals("401")){

                        Toast.makeText(SmsCreate.this, message,Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SmsCreate.this, Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }else{
                        Toast.makeText(SmsCreate.this, message,Toast.LENGTH_LONG).show();
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                Toast.makeText(SmsCreate.this,error.toString(),Toast.LENGTH_LONG).show();
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

    public void areWiseMessage()
    {
        progressDialog.showDialog();
        String url = URL_config.BASE_URL+ URL_config.AREAWISE_SMS;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.hideDialog();

                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("200")){
                        Toast.makeText(SmsCreate.this, message,Toast.LENGTH_LONG).show();
                        finish();

                    }else if(status.equals("401")){

                        Toast.makeText(SmsCreate.this, message,Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SmsCreate.this, Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }else{
                        Toast.makeText(SmsCreate.this, message,Toast.LENGTH_LONG).show();
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                Toast.makeText(SmsCreate.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                map.put("jwt", jwt);
                map.put("message", areaMessage);
                map.put("area", selectedArea);
                return map;

            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance().addToRequestQueue(stringRequest);
    }


    public void warningBillExpire(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(true);
        alert.setTitle("সতর্কতা!!");
        alert.setMessage("যে সব ক্লায়েন্টদের বিলের মেয়াদ শেষ হতে ৩দিন বাকী তাদের ফোনে SMS যাবে।");
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

    public void warningBillExpireDisconnect(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(true);
        alert.setTitle("সতর্কতা!!");
        alert.setMessage("যে সব ক্লায়েন্টদের বিলের মেয়াদ শেষ তাদের লাইন বন্ধ হবে এবং ফোনে SMS যাবে।");
        alert.setIcon(R.drawable.ic_baseline_warning_24);

        alert.setPositiveButton("Ok, Sure", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                client.setJwt(jwt);
                billExpireDisconnect(client);
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


    public void warning_enabled_client_sms(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(true);
        alert.setTitle("সতর্কতা!!");
        alert.setMessage("সকল একটিভ ক্লায়েন্টদের ফোনে আপনার লিখা মেসেজ যাবে। আপনার মেসেজ সঠিক আছে কিনা চেক করে দেখুন অন্যথায় ভুল মেসেজে ক্লায়েন্ট বিভ্রান্তে পড়তে পারে।");
        alert.setIcon(R.drawable.ic_baseline_warning_24);

        alert.setPositiveButton("Ok, Sure", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                enabledClientSmsSend();
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

    public void area_load()
    {
        String url = URL_config.BASE_URL+URL_config.AREA_LOAD;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONArray jsonArray = new JSONArray(response);

                    for(int i=0; i<jsonArray.length(); i++) {
                        areaList.add(jsonArray.getString(i));
                    }

                    ArrayAdapter<String> AreaArrayAdapter = new ArrayAdapter<>(SmsCreate.this,
                            android.R.layout.simple_spinner_dropdown_item, areaList);
                    areaSpinner.setAdapter(AreaArrayAdapter);

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SmsCreate.this,error.toString(),Toast.LENGTH_LONG).show();
                finish();
            }
        });
        MySingleton.getInstance().addToRequestQueue(stringRequest);

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
                startActivity(new Intent(SmsCreate.this, Login.class));
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

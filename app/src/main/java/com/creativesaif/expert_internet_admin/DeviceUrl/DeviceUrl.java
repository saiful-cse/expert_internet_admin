package com.creativesaif.expert_internet_admin.DeviceUrl;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creativesaif.expert_internet_admin.Employees.Employee;
import com.creativesaif.expert_internet_admin.Login;
import com.creativesaif.expert_internet_admin.MainActivity;
import com.creativesaif.expert_internet_admin.MySingleton;
import com.creativesaif.expert_internet_admin.ProgressDialog;
import com.creativesaif.expert_internet_admin.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class DeviceUrl extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private SharedPreferences preferences;
    private EditText edBaseUrl, edloginIp, edUsername, edPassword;
    private String id, baseUrl, loginIp, username, password;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_url);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        preferences = this.getSharedPreferences("users", MODE_PRIVATE);
        edBaseUrl = findViewById(R.id.edBaseurl);
        edloginIp = findViewById(R.id.edLoginip);
        edUsername = findViewById(R.id.edUsername);
        edPassword = findViewById(R.id.edpassword);
        saveButton = findViewById(R.id.btnSave);

        if (!isNetworkConnected()){
            Toast.makeText(DeviceUrl.this,"Check Internet Connection!",Toast.LENGTH_LONG).show();

        }else{
            device_url_load();
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                baseUrl = edBaseUrl.getText().toString().trim();
                loginIp = edloginIp.getText().toString().trim();
                username = edUsername.getText().toString().trim();
                password = edPassword.getText().toString().trim();

                if (baseUrl.isEmpty()){
                    Toast.makeText(DeviceUrl.this,"Base URL not empty",Toast.LENGTH_LONG).show();

                }else if(loginIp.isEmpty()){
                    Toast.makeText(DeviceUrl.this,"Login not empty",Toast.LENGTH_LONG).show();

                }else if(username.isEmpty()){
                    Toast.makeText(DeviceUrl.this,"Username not empty",Toast.LENGTH_LONG).show();

                }else if(password.isEmpty()){
                    Toast.makeText(DeviceUrl.this,"Password not empty",Toast.LENGTH_LONG).show();

                }else if(!isNetworkConnected()){
                    Toast.makeText(DeviceUrl.this,"Check internet connection.",Toast.LENGTH_LONG).show();

                }else{

                    device_url_update();
                }
            }
        });

    }

    //Internet connection check
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
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

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void device_url_load()
    {
        String url = "http://192.168.1.8/api/expert_internet_api/exp-v5.0/device/device_url.php";

        progressDialog.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    for(int i=0; i<jsonObject.length(); i++) {
                        id = jsonObject.getString("id");
                        edBaseUrl.setText(jsonObject.getString("api_base"));
                        edloginIp.setText(jsonObject.getString("login_ip"));
                        edUsername.setText(jsonObject.getString("username"));
                        edPassword.setText(jsonObject.getString("password"));
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                Toast.makeText(DeviceUrl.this,error.toString(),Toast.LENGTH_LONG).show();
                finish();
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                map.put("jwt", Objects.requireNonNull(preferences.getString("jwt", null)));
                return map;

            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance().addToRequestQueue(stringRequest);;

    }

    public void device_url_update()
    {
        String url = "http://192.168.1.8/api/expert_internet_api/exp-v5.0/device/device_url_update.php";

        progressDialog.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("status").equals("200"))
                    {
                        Toast.makeText(DeviceUrl.this,jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(DeviceUrl.this, Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }else if(jsonObject.getString("status").equals("401")){
                        Toast.makeText(DeviceUrl.this,jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(DeviceUrl.this, Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                Toast.makeText(DeviceUrl.this,error.toString(),Toast.LENGTH_LONG).show();
                finish();
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                map.put("jwt", Objects.requireNonNull(preferences.getString("jwt", null)));
                map.put("id", id);
                map.put("api_base", baseUrl);
                map.put("login_ip", loginIp);
                map.put("username", username);
                map.put("password", password);
                return map;

            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance().addToRequestQueue(stringRequest);;

    }
}
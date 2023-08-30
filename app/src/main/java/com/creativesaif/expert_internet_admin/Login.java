package com.creativesaif.expert_internet_admin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    EditText editTextUserId, editTextUserPin;
    SharedPreferences sharedPreferences;
    Button btnLogin;
    ProgressBar progressBar;
    TextView viewversionname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        PackageManager manager = this.getPackageManager();

        /*
        Id's initialize
         */
        init();

        try{

            PackageInfo info = manager.getPackageInfo(this.getPackageName(), PackageManager.GET_ACTIVITIES);
            viewversionname.setText("App Version: "+info.versionName+"\n"+"Web API version: exp-v5.0\nRelease date: 12/05/2023");

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        editTextUserId.setText(sharedPreferences.getString("employee_id", null));
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (editTextUserId.getText().toString().trim().isEmpty()){

                    Toast.makeText(Login.this,"Enter user ID",Toast.LENGTH_LONG).show();

                }else if(editTextUserPin.getText().toString().trim().isEmpty()){

                    Toast.makeText(Login.this,"Enter pin",Toast.LENGTH_LONG).show();

                }else if(!isNetworkConnected()){

                    Toast.makeText(Login.this,"Check internet Connection",Toast.LENGTH_LONG).show();

                }
                else {

                    login();
                }
            }
        });
    }

    public void init(){
        editTextUserId = findViewById(R.id.edUserId);
        editTextUserPin = findViewById(R.id.edUserPin);
        sharedPreferences = getApplicationContext().getSharedPreferences("users", MODE_PRIVATE);
        progressBar = findViewById(R.id.progressbar);
        btnLogin = findViewById(R.id.login);
        viewversionname = findViewById(R.id.version);
    }

    //Internet connection check
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public void login()
    {
        progressBar.setVisibility(View.VISIBLE);
        String url = URL_config.BASE_URL+URL_config.LOGIN;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressBar.setVisibility(View.GONE);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("200")) {

                        //store jwt and userid
                        sharedPreferences.edit().putString("employee_id", jsonObject.getString("employee_id")).apply();
                        sharedPreferences.edit().putString("jwt", jsonObject.getString("jwt")).apply();
                        sharedPreferences.edit().putString("super_admin", jsonObject.getString("super_admin")).apply();
                        sharedPreferences.edit().putString("dashboard", jsonObject.getString("dashboard")).apply();
                        sharedPreferences.edit().putString("client_add", jsonObject.getString("client_add")).apply();
                        sharedPreferences.edit().putString("client_details_update", jsonObject.getString("client_details_update")).apply();
                        sharedPreferences.edit().putString("sms", jsonObject.getString("sms")).apply();
                        sharedPreferences.edit().putString("txn_summary", jsonObject.getString("txn_summary")).apply();
                        sharedPreferences.edit().putString("txn_edit", jsonObject.getString("txn_edit")).apply();
                        sharedPreferences.edit().putString("upstream_bill", jsonObject.getString("upstream_bill")).apply();
                        sharedPreferences.edit().putString("salary_add", jsonObject.getString("salary_add")).apply();
                        sharedPreferences.edit().putString("device", jsonObject.getString("device")).apply();
                        sharedPreferences.edit().putString("note", jsonObject.getString("note")).apply();

                        sharedPreferences.edit().putString("api_base", jsonObject.getString("api_base")).apply();
                        sharedPreferences.edit().putString("login_ip", jsonObject.getString("login_ip")).apply();
                        sharedPreferences.edit().putString("username", jsonObject.getString("username")).apply();
                        sharedPreferences.edit().putString("password", jsonObject.getString("password")).apply();

                        Intent intent = new Intent(Login.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }else{

                        Toast.makeText(Login.this,message,Toast.LENGTH_LONG).show();

                    }


                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                //Toast.makeText(Login.this,"Connection Error!! Try again",Toast.LENGTH_LONG).show();
                Toast.makeText(Login.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                map.put("employee_id", editTextUserId.getText().toString().trim());
                map.put("pin", editTextUserPin.getText().toString().trim());
                return map;

            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 8, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance().addToRequestQueue(stringRequest);
    }

}
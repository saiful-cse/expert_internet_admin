package com.creativesaif.expert_internet_admin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashScreen extends AppCompatActivity {
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        progressBar = findViewById(R.id.progressBarSplash);

        if (isNetworkConnected() ){
            //calling a function for Load post
            alert_set();

        }else{

            Snackbar.make(findViewById(android.R.id.content),"Please!! Check internet connection.",Snackbar.LENGTH_LONG).show();

        }

    }

    //get profile info
    public void alert_set(){

        progressBar.setVisibility(View.VISIBLE);
        String url = getString(R.string.base_url)+getString(R.string.check_alert_client);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String message = jsonObject.getString("message");

                    if (message.equals("200")) {
                        finish();
                        startActivity(new Intent(SplashScreen.this, MainActivity.class));
                        Toast.makeText(SplashScreen.this, message,Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(SplashScreen.this, message,Toast.LENGTH_LONG).show();
                        finish();
                    }

                }catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SplashScreen.this, e.getMessage(),Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                progressBar.setVisibility(View.GONE);
                Toast.makeText(SplashScreen.this, volleyError.toString(),Toast.LENGTH_SHORT).show();

            }
        });
        MySingleton.getInstance().addToRequestQueue(request);
    }

    //Internet connection check
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}

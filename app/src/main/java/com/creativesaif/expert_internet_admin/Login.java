package com.creativesaif.expert_internet_admin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
    ImageView imageViewLogin;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*
        Id's initialize
         */
        init();

        imageViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isNetworkConnected()){
                    Snackbar.make(findViewById(android.R.id.content),"Check internet Connection",Snackbar.LENGTH_LONG).show();
                }
                else if (editTextUserId.getText().toString().trim().isEmpty()){
                    Snackbar.make(findViewById(android.R.id.content),"Enter user id",Snackbar.LENGTH_LONG).show();

                }else if(editTextUserPin.getText().toString().trim().isEmpty()){
                    Snackbar.make(findViewById(android.R.id.content),"Enter pin",Snackbar.LENGTH_LONG).show();
                }else {
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
        imageViewLogin = findViewById(R.id.login);
    }

    //Internet connection check
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public void login()
    {
        progressBar.setVisibility(View.VISIBLE);
        String url = getString(R.string.base_url)+getString(R.string.login);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Toast.makeText(MainActivity.this,response,Toast.LENGTH_SHORT).show();

                progressBar.setVisibility(View.GONE);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");

                    if (message.equals("200")) {

                        //session, store id
                        sharedPreferences.edit().putString("userid", editTextUserId.getText().toString().trim()).apply();

                        Intent intent = new Intent(Login.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }else{

                        Snackbar.make(findViewById(android.R.id.content),message,Snackbar.LENGTH_LONG).show();
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

                map.put("userid", editTextUserId.getText().toString().trim());
                map.put("pin", editTextUserPin.getText().toString().trim());
                return map;

            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 8, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance().addToRequestQueue(stringRequest);
    }

}
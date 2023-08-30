package com.creativesaif.expert_internet_admin.Note;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.creativesaif.expert_internet_admin.MainActivity;
import com.creativesaif.expert_internet_admin.Model.Salary;
import com.creativesaif.expert_internet_admin.Model.SalaryWrapper;
import com.creativesaif.expert_internet_admin.MySingleton;
import com.creativesaif.expert_internet_admin.Network.ApiInterface;
import com.creativesaif.expert_internet_admin.Network.RetrofitApiClient;
import com.creativesaif.expert_internet_admin.ProgressDialog;
import com.creativesaif.expert_internet_admin.R;
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

public class NoteEdit extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private EditText edNote;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        edNote = findViewById(R.id.ednote);
        sharedPreferences = getApplicationContext().getSharedPreferences("users", MODE_PRIVATE);
        progressDialog = new ProgressDialog(this);

        if (isConnected()){
            loadNote();
        }else{
            finish();
            Toast.makeText(NoteEdit.this, "No Internet Connection",Toast.LENGTH_LONG).show();
        }

        FloatingActionButton fab = findViewById(R.id.fabnoteudate);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isConnected()){
                    warning_note_update();
                }else{
                    finish();
                    Toast.makeText(NoteEdit.this, "No Internet Connection",Toast.LENGTH_LONG).show();
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
                        edNote.setText(jsonObject.getString("note"));
                    }else{
                        Toast.makeText(NoteEdit.this,message,Toast.LENGTH_LONG).show();
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
                Toast.makeText(NoteEdit.this,error.toString(),Toast.LENGTH_LONG).show();
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

    public void updateNote(String note) {

        progressDialog.showDialog();
        String url = URL_config.BASE_URL+URL_config.NOTE_EDIT;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.hideDialog();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("200")) {
                        Intent intent = new Intent(NoteEdit.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        Toast.makeText(NoteEdit.this,message,Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(NoteEdit.this,message,Toast.LENGTH_LONG).show();
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
                Toast.makeText(NoteEdit.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                map.put("jwt", sharedPreferences.getString("jwt", null));
                map.put("note", note);

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

    public void warning_note_update(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(true);
        alert.setTitle("Warning!!");
        alert.setMessage("Are you sure want to update note?");
        alert.setIcon(R.drawable.ic_baseline_warning_24);

        alert.setPositiveButton("Ok, Sure", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                updateNote(edNote.getText().toString().trim());
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
}
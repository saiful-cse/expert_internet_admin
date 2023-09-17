package com.creativesaif.expert_internet_admin.ClientList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creativesaif.expert_internet_admin.Employees.Employee;
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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class ClientReg extends AppCompatActivity {

    //Declaring EditText
    private EditText edclientname, edclientphone;
    private JSONArray jsonArrayArea;

    //Declaring String
    private String jwt,name, phone, selectedAreaId;

    //Declaring progress dialog
    private ProgressDialog progressDialog;

    //Declaring spinner
    private Spinner areaSpinner;

    //Declaring Array List

    private ApiInterface apiInterface;
    private Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_reg);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        SharedPreferences preferences = this.getSharedPreferences("users", MODE_PRIVATE);
        jwt = preferences.getString("jwt", null);

        apiInterface = RetrofitApiClient.getClient().create(ApiInterface.class);
        client = new Client();

        if (isNetworkConnected()){
            area_load();
        }else{
            Toast.makeText(getApplicationContext(), "Please!! Check internet connection.", Toast.LENGTH_SHORT).show();
            finish();
        }
        /*
        Id initialize
         */
        areaSpinner = findViewById(R.id.areaListSpinner);
        edclientname = findViewById(R.id.edclientname);
        edclientphone = findViewById(R.id.edclientphone);
        //Declaring Button
        Button submitbtn = findViewById(R.id.submit_button);

        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = edclientname.getText().toString().trim();
                phone = edclientphone.getText().toString().trim();

                if (name.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Enter client name", Toast.LENGTH_SHORT).show();

                }else if(phone.isEmpty() || phone.length() < 11){
                    Toast.makeText(getApplicationContext(), "Enter correct phone number", Toast.LENGTH_SHORT).show();

                }else if(phone.length() > 11){
                    Toast.makeText(getApplicationContext(), "Enter correct phone number", Toast.LENGTH_SHORT).show();

                }else if(selectedAreaId.equals("1")){
                    Toast.makeText(getApplicationContext(), "Select area name", Toast.LENGTH_SHORT).show();

                } else if (!isNetworkConnected()){
                    Toast.makeText(getApplicationContext(), "Please!! Check internet connection.", Toast.LENGTH_SHORT).show();

                }else {

                    client.setJwt(jwt);
                    client.setName(name);
                    client.setPhone(phone);
                    client.setArea_id(selectedAreaId);

                    clientRegistration(client);
                }
            }
        });

        //Spinner item choice and click event
        areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // On selecting a spinner item
                try {
                    JSONObject json = jsonArrayArea.getJSONObject(position);
                    selectedAreaId = json.getString("id");

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

    }




    //Internet connection check
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void area_load()
    {
        String url = URL_config.BASE_URL+URL_config.AREA_LOAD;

        progressDialog.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.hideDialog();
                ArrayList<String> areaList = new ArrayList<>();
                try {

                    jsonArrayArea = new JSONArray(response);

                    for(int i=0; i<jsonArrayArea.length(); i++) {
                        JSONObject jsonObjectItem = jsonArrayArea.getJSONObject(i);
                        areaList.add(jsonObjectItem.getString("area_name"));
                    }

                    ArrayAdapter<String> AreaArrayAdapter = new ArrayAdapter<>(ClientReg.this,
                            android.R.layout.simple_spinner_dropdown_item, areaList);
                    areaSpinner.setAdapter(AreaArrayAdapter);

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                Toast.makeText(ClientReg.this,error.toString(),Toast.LENGTH_LONG).show();
                finish();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance().addToRequestQueue(stringRequest);;
    }

    public void clientRegistration(Client client) {

        progressDialog.showDialog();
        Call<DetailsWrapper> call = apiInterface.clientRegistration(client);
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

                } else if (detailsWrapper.getStatus() == 200) {
                    Toast.makeText(getApplicationContext(), detailsWrapper.getMessage(), Toast.LENGTH_LONG).show();
                    finish();

                }else{
                    warningShow(detailsWrapper.getMessage());
                }

            }

            @Override
            public void onFailure(Call<DetailsWrapper> call, Throwable t) {
                progressDialog.hideDialog();
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();

            }
        });
    }

    public void updateRegistration(Client client) {

        progressDialog.showDialog();
        Call<DetailsWrapper> call = apiInterface.updateRegistration(client);
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

                } else if (detailsWrapper.getStatus() == 200) {
                    Toast.makeText(getApplicationContext(), detailsWrapper.getMessage(), Toast.LENGTH_LONG).show();
                    finish();

                }else{
                    warningShow(detailsWrapper.getMessage());
                }

            }

            @Override
            public void onFailure(Call<DetailsWrapper> call, Throwable t) {
                progressDialog.hideDialog();
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();

            }
        });
    }

    public void loginWarningShow(String message){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(false);
        alert.setTitle("Warning!!");
        alert.setMessage(message);
        alert.setIcon(R.drawable.ic_baseline_warning_24);

        alert.setPositiveButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
                startActivity(new Intent(ClientReg.this, Login.class));
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog dlg = alert.create();
        dlg.show();
    }

    public void warningShow(String message){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
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
        AlertDialog dlg = alert.create();
        dlg.show();
    }
}
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creativesaif.expert_internet_admin.Login;
import com.creativesaif.expert_internet_admin.Model.Client;
import com.creativesaif.expert_internet_admin.Model.DetailsWrapper;
import com.creativesaif.expert_internet_admin.Model.Package;
import com.creativesaif.expert_internet_admin.MySingleton;
import com.creativesaif.expert_internet_admin.Network.ApiInterface;
import com.creativesaif.expert_internet_admin.Network.RetrofitApiClient;
import com.creativesaif.expert_internet_admin.ProgressDialog;
import com.creativesaif.expert_internet_admin.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class ClientDetailsEdit extends AppCompatActivity{

    //Declaring EditText
    private EditText edclientname, edclientphone, edpppusername, edpppassword;

    //Declaring RadioButton
    private RadioGroup radioGroupPaymentMethod, radioGroupClientMode;

    //Declaring String
    private String jwt;
    private String id;
    private String name;
    private String phone;
    private String existArea;
    private String selectedArea;
    private String pppname;
    private String pppassword;
    private String selectedPackage;

    //Declaring progress dialog
    private ProgressDialog progressDialog;

    //Declaring spinner
    private Spinner areaSpinner, packageSpinner;

    //Declaring Array List
    private ArrayList<String> areaList;

    private ApiInterface apiInterface;
    private Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_details_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        id = getIntent().getStringExtra("id");
        SharedPreferences preferences = this.getSharedPreferences("users", MODE_PRIVATE);
        jwt = preferences.getString("jwt", null);

        apiInterface = RetrofitApiClient.getClient().create(ApiInterface.class);
        client = new Client();

        /*
        Id initialize
         */
        areaSpinner = findViewById(R.id.areaListSpinner);
        packageSpinner = findViewById(R.id.spinnerpkgs);
        areaList = new ArrayList<>();

        radioGroupPaymentMethod = findViewById(R.id.radioGroupPaymentMethod);
        radioGroupClientMode = findViewById(R.id.radioGroupClientMode);
        edclientname = findViewById(R.id.edclientname);
        edclientphone = findViewById(R.id.edclientphone);
        edpppusername = findViewById(R.id.edpppusername);
        edpppassword = findViewById(R.id.edppppassword);
        //Declaring Button
        Button buttonUpdate = findViewById(R.id.update_button);

        if (id == null ){
            loginWarningShow("Session expired!!");

        }else if (!isNetworkConnected()) {
            Toast.makeText(getApplicationContext(), "Please!! Check internet connection.", Toast.LENGTH_SHORT).show();

        }else{
            client.setJwt(jwt);
            client.setId(id);
            load_details(client);
        }

        //Submit data to server
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name = edclientname.getText().toString().trim();
                phone = edclientphone.getText().toString().trim();
                pppname = edpppusername.getText().toString().trim();
                pppassword = edpppassword.getText().toString().trim();

                if (name.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Enter client name", Toast.LENGTH_SHORT).show();

                }else if(phone.isEmpty() || phone.length() < 11){
                    Toast.makeText(getApplicationContext(), "Enter correct phone number", Toast.LENGTH_SHORT).show();

                }else if(phone.length() > 11){
                    Toast.makeText(getApplicationContext(), "Enter correct phone number", Toast.LENGTH_SHORT).show();

                }else if(pppname.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Enter PPP name", Toast.LENGTH_SHORT).show();

                }else if(pppassword.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Enter PPP password", Toast.LENGTH_SHORT).show();

                } else if (!isNetworkConnected()){
                    Toast.makeText(getApplicationContext(), "Please!! Check internet connection.", Toast.LENGTH_SHORT).show();

                }else if(id == null || jwt == null){
                    Toast.makeText(getApplicationContext(), "Session expired!!", Toast.LENGTH_LONG).show();
                    finish();
                    Intent intent = new Intent(ClientDetailsEdit.this, Login.class);
                    startActivity(intent);

                }else {

                    int selectedPaymentMethod = radioGroupPaymentMethod.getCheckedRadioButtonId();
                    RadioButton radioButtonPaymentMethod = findViewById(selectedPaymentMethod);
                    String payment_method = radioButtonPaymentMethod.getText().toString().trim();

                    int selectedClientMode = radioGroupClientMode.getCheckedRadioButtonId();
                    RadioButton radioButtonClientMode = findViewById(selectedClientMode);
                    String client_mode = radioButtonClientMode.getText().toString().trim();

                    client.setJwt(jwt);
                    client.setId(id);
                    client.setMode(client_mode);
                    client.setPaymentMethod(payment_method);
                    client.setName(name);
                    client.setPhone(phone);
                    client.setArea(selectedArea);
                    client.setPppName(pppname);
                    client.setPppPass(pppassword);
                    client.setPkgId(selectedPackage);

                    updateDetails(client);
                }
            }
        });

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

        //Spinner item choice and click event
        packageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // On selecting a spinner item
                selectedPackage = parentView.getItemAtPosition(position).toString();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

    }


    public void load_details(Client mClient) {
        progressDialog.showDialog();
        Call<DetailsWrapper> call = apiInterface.getClientDetailsId(mClient);
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
                }

                if (detailsWrapper.getStatus() == 200) {
                    edclientname.setText(detailsWrapper.getName());
                    edclientphone.setText(detailsWrapper.getPhone());
                    existArea = detailsWrapper.getArea();
                    if (detailsWrapper.getPaymentMethod().equals("Cash")) {
                        radioGroupPaymentMethod.check(R.id.payment_cash);

                    } else if (detailsWrapper.getPaymentMethod().equals("Mobile")) {
                        radioGroupPaymentMethod.check(R.id.payment_mobile);
                    }

                    if (detailsWrapper.getMode().equals("Enable")){
                        radioGroupClientMode.check(R.id.client_enable);

                    }else if(detailsWrapper.getMode().equals("Disable")){
                        radioGroupClientMode.check(R.id.client_disable);
                    }

                    edpppusername.setText(detailsWrapper.getPppName());
                    edpppassword.setText(detailsWrapper.getPppPass());

                    //Load packages
                    List<String> packageList = new ArrayList<>();

                    for (int i = 0; i < detailsWrapper.getPackages().size(); i++){
                        packageList.add(detailsWrapper.getPackages().get(i).getPkgId());
                    }

                    ArrayAdapter<String> packageArrayAdapter = new ArrayAdapter<>(ClientDetailsEdit.this,
                         android.R.layout.simple_spinner_dropdown_item, packageList);
                    packageSpinner.setAdapter(packageArrayAdapter);
                    int spinnerPosition = packageArrayAdapter.getPosition(detailsWrapper.getPkgId());
                    packageSpinner.setSelection(spinnerPosition);

                }else{
                    Toast.makeText(getApplicationContext(), detailsWrapper.getMessage(), Toast.LENGTH_LONG).show();
                }

                area_load();
            }

            @Override
            public void onFailure(Call<DetailsWrapper> call, Throwable t) {
                progressDialog.hideDialog();
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
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

    //Area load
    public void area_load()
    {
        String url = getString(R.string.base_url)+getString(R.string.area_load);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONArray jsonArray = new JSONArray(response);

                    for(int i=0; i<jsonArray.length(); i++) {
                        areaList.add(jsonArray.getString(i));
                    }

                    ArrayAdapter<String> AreaArrayAdapter = new ArrayAdapter<>(ClientDetailsEdit.this,
                            android.R.layout.simple_spinner_dropdown_item, areaList);
                    areaSpinner.setAdapter(AreaArrayAdapter);
                    int spinnerPosition2 = AreaArrayAdapter.getPosition(existArea);
                    areaSpinner.setSelection(spinnerPosition2);


                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ClientDetailsEdit.this,error.toString(),Toast.LENGTH_LONG).show();
                finish();
            }
        });
        MySingleton.getInstance().addToRequestQueue(stringRequest);

    }

    public void updateDetails(Client client) {

        progressDialog.showDialog();
        Call<DetailsWrapper> call = apiInterface.updateDetails(client);
        call.enqueue(new Callback<DetailsWrapper>() {
            @SuppressLint("ResourceType")
            @Override
            public void onResponse(Call<DetailsWrapper> call, retrofit2.Response<DetailsWrapper> response) {

                progressDialog.hideDialog();

                DetailsWrapper detailsWrapper = response.body();
                assert detailsWrapper != null;

                //Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();

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
                Toast.makeText(getApplicationContext(), "Failure: "+t.toString(), Toast.LENGTH_LONG).show();

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
                startActivity(new Intent(ClientDetailsEdit.this, Login.class));
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

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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class ClientRegUpdate extends AppCompatActivity {

    //Declaring EditText
    private EditText edclientname, edclientphone, edpppusername, edpppassword;

    //Declaring RadioButton
    private RadioGroup radioGroupPaymentMethod, radioGroupClientMode;

    //Declaring String
    private String jwt, id, name, phone, existArea, selectedArea, selectedZone,
            pppname, pppassword, selectedPackage;

    //Declaring progress dialog
    private ProgressDialog progressDialog;

    private ProgressBar areaLoader;

    //Declaring spinner
    private Spinner areaSpinner, zoneSpinner, packageSpinner;

    //Declaring Array List
    private ArrayList<String> areaList;
    private ApiInterface apiInterface;
    private Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_reg_update);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        areaLoader = findViewById(R.id.areaLoadProgressBar);

        id = getIntent().getStringExtra("id");
        SharedPreferences preferences = this.getSharedPreferences("users", MODE_PRIVATE);
        jwt = preferences.getString("jwt", null);

        apiInterface = RetrofitApiClient.getClient().create(ApiInterface.class);
        client = new Client();

        /*
        Id initialize
         */
        areaSpinner = findViewById(R.id.areaListSpinner);
        zoneSpinner = findViewById(R.id.zoneListSpinner);
        packageSpinner = findViewById(R.id.spinnerpkgs);
        areaList = new ArrayList<>();

        radioGroupPaymentMethod = findViewById(R.id.radioGroupPaymentMethod);
        radioGroupClientMode = findViewById(R.id.radioGroupClientMode);
        edclientname = findViewById(R.id.edclientname);
        edclientphone = findViewById(R.id.edclientphone);
        edpppusername = findViewById(R.id.edpppusername);
        edpppassword = findViewById(R.id.edppppassword);
        //Declaring Button
        Button btnUpdate = findViewById(R.id.update_button);

        if (!isNetworkConnected()) {
            Toast.makeText(getApplicationContext(), "Please!! Check internet connection.", Toast.LENGTH_SHORT).show();

        }else{
            client.setJwt(jwt);
            client.setId(id);
            load_details(client);
        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
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

                }else if(selectedArea.equals("---")){
                    Toast.makeText(getApplicationContext(), "Select Area", Toast.LENGTH_SHORT).show();

                } else if(pppname.isEmpty() || pppname.equals("ss-expnet-")){
                    Toast.makeText(getApplicationContext(), "Enter Correct PPP Serial", Toast.LENGTH_SHORT).show();

                }else if(pppassword.isEmpty() || pppassword.length() < 8){
                    Toast.makeText(getApplicationContext(), "Enter Correct PPP Password", Toast.LENGTH_SHORT).show();

                }else if (!isNetworkConnected()){
                    Toast.makeText(getApplicationContext(), "Please!! Check internet connection.", Toast.LENGTH_SHORT).show();

                }else {

                    int selectedPaymentMethod = radioGroupPaymentMethod.getCheckedRadioButtonId();
                    RadioButton radioButtonPaymentMethod = findViewById(selectedPaymentMethod);
                    String payment_method = radioButtonPaymentMethod.getText().toString().trim();

                    int selectedClientMode = radioGroupClientMode.getCheckedRadioButtonId();
                    RadioButton radioButtonClientMode = findViewById(selectedClientMode);
                    String client_mode = radioButtonClientMode.getText().toString().trim();

                    client.setJwt(jwt);
                    client.setId(id);
                    client.setPaymentMethod(payment_method);
                    client.setMode(client_mode);
                    client.setName(name);
                    client.setPhone(phone);
                    client.setArea(selectedArea);
                    client.setZone(selectedZone);
                    client.setPppName(pppname);
                    client.setPppPass(pppassword);
                    client.setPkgId(selectedPackage);

                    updateRegistration(client);
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
        zoneSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // On selecting a spinner item
                selectedZone = parentView.getItemAtPosition(position).toString();

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
                    Toast.makeText(getApplicationContext(), detailsWrapper.getMessage(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ClientRegUpdate.this, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

                if (detailsWrapper.getStatus() == 200) {
                    edclientname.setText(detailsWrapper.getName());
                    edclientphone.setText(detailsWrapper.getPhone());
                    existArea = detailsWrapper.getArea();

                    List<String> zoneList = new ArrayList<>();
                    zoneList.add("Main");
                    zoneList.add("Osman");

                    ArrayAdapter<String> zoneArrayAdapter = new ArrayAdapter<>(ClientRegUpdate.this,
                            android.R.layout.simple_spinner_dropdown_item, zoneList);
                    zoneSpinner.setAdapter(zoneArrayAdapter);

                    int zonespinnerPosition = zoneArrayAdapter.getPosition(detailsWrapper.getZone());
                    zoneSpinner.setSelection(zonespinnerPosition);


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

                    ArrayAdapter<String> packageArrayAdapter = new ArrayAdapter<>(ClientRegUpdate.this,
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
        areaLoader.setVisibility(View.VISIBLE);
        String url = URL_config.BASE_URL+URL_config.AREA_LOAD;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                areaLoader.setVisibility(View.GONE);
                try {

                    JSONArray jsonArray = new JSONArray(response);

                    for(int i=0; i<jsonArray.length(); i++) {
                        areaList.add(jsonArray.getString(i));
                    }

                    ArrayAdapter<String> AreaArrayAdapter = new ArrayAdapter<>(ClientRegUpdate.this,
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
                areaLoader.setVisibility(View.GONE);
                Toast.makeText(ClientRegUpdate.this,error.toString(),Toast.LENGTH_LONG).show();
                finish();
            }
        });
        MySingleton.getInstance().addToRequestQueue(stringRequest);

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
                    Toast.makeText(getApplicationContext(), detailsWrapper.getMessage(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ClientRegUpdate.this, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

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
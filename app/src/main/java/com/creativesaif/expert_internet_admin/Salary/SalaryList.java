package com.creativesaif.expert_internet_admin.Salary;


import android.content.Context;
import android.content.Intent;
import android.content.ReceiverCallNotAllowedException;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;


import android.widget.Spinner;
import android.widget.Toast;

import com.creativesaif.expert_internet_admin.Adapter.SalaryAdapter;
import com.creativesaif.expert_internet_admin.Login;
import com.creativesaif.expert_internet_admin.MainActivity;
import com.creativesaif.expert_internet_admin.Model.Client;
import com.creativesaif.expert_internet_admin.Model.ClientWrapper;
import com.creativesaif.expert_internet_admin.Model.Salary;
import com.creativesaif.expert_internet_admin.Model.SalaryWrapper;
import com.creativesaif.expert_internet_admin.Network.ApiInterface;
import com.creativesaif.expert_internet_admin.Network.RetrofitApiClient;
import com.creativesaif.expert_internet_admin.ProgressDialog;
import com.creativesaif.expert_internet_admin.R;
import com.creativesaif.expert_internet_admin.Sms.SmsCreate;

import java.util.ArrayList;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;


public class SalaryList extends AppCompatActivity {
    public SharedPreferences sharedPreferences;
    public String jwt;
    public Spinner adminSpinner;
    public String adminId;
    public ProgressDialog progressDialog;

    RecyclerView recyclerView;
    SalaryAdapter salaryAdapter;
    ArrayList<Salary> salaryArrayList;
    ApiInterface apiInterface;
    Salary salary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salary_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        sharedPreferences = getApplicationContext().getSharedPreferences("users", MODE_PRIVATE);
        progressDialog = new ProgressDialog(this);


        adminSpinner = findViewById(R.id.adminidListSpinner);
        jwt = sharedPreferences.getString("jwt", null);
        adminId = sharedPreferences.getString("admin_id", null);

        salaryArrayList = new ArrayList<>();
        salaryAdapter = new SalaryAdapter(this, salaryArrayList);
        recyclerView = findViewById(R.id.recyclerViewSalaryRow);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(salaryAdapter);
        apiInterface = RetrofitApiClient.getClient().create(ApiInterface.class);
        salary = new Salary();

        List<String> employeeList = new ArrayList<>();
        if (adminId.equals("9161") || adminId.equals("8991")){
            employeeList.add("9588");
            employeeList.add("0713");
        }else{
            employeeList.add(adminId);
        }

        ArrayAdapter<String> employeeArrayAdapter = new ArrayAdapter<>(SalaryList.this,
                android.R.layout.simple_spinner_dropdown_item, employeeList);
        adminSpinner.setAdapter(employeeArrayAdapter);

        //Spinner item choice and click event
        adminSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // On selecting a spinner item
                adminId = parentView.getItemAtPosition(position).toString();
                if (!isConnected()) {
                    Toast.makeText(getApplicationContext(), "Please!! Check internet connection.", Toast.LENGTH_SHORT).show();

                } else {
                    //sent value on client model class
                    salary.setJwt(jwt);
                    salary.setEmployee_id(adminId);
                    loadSalary(salary);
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        FloatingActionButton fab2 = findViewById(R.id.fabaddslaray);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Objects.equals(sharedPreferences.getString("admin_id", null), "9161") || Objects.equals(sharedPreferences.getString("admin_id", null), "8991")){
                    startActivity(new Intent(SalaryList.this, AddSalary.class));
                }else{
                    Toast.makeText(getApplicationContext(), "You are not permitted to access", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void loadSalary(Salary mSalary) {

        progressDialog.showDialog();
        Call<SalaryWrapper> call = apiInterface.getSalary(mSalary);
        call.enqueue(new Callback<SalaryWrapper>() {
            @Override
            public void onResponse(Call<SalaryWrapper> call, retrofit2.Response<SalaryWrapper> response) {

                progressDialog.hideDialog();
                salaryArrayList.clear();

                SalaryWrapper salaryWrapper = response.body();
                assert salaryWrapper != null;

                if (salaryWrapper.getStatus() == 401) {

                    Intent intent = new Intent(SalaryList.this, Login.class);
                    startActivity(intent);

                }else if (salaryWrapper.getStatus() == 404) {
                    //client not found then visible error
                    Toast.makeText(getApplication(), salaryWrapper.getMessage(), Toast.LENGTH_LONG).show();

                }else if (salaryWrapper.getStatus() == 200) {

                    salaryAdapter.setSalaryList(salaryWrapper.getSalaries());
                }

            }

            @Override
            public void onFailure(Call<SalaryWrapper> call, Throwable t) {
              progressDialog.hideDialog();
              Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();

            }
        });
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

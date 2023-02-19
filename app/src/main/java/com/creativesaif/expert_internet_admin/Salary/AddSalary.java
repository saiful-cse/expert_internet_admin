package com.creativesaif.expert_internet_admin.Salary;

import android.content.Context;
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
import android.widget.Spinner;
import android.widget.Toast;

import com.creativesaif.expert_internet_admin.Login;
import com.creativesaif.expert_internet_admin.Model.Salary;
import com.creativesaif.expert_internet_admin.Model.SalaryWrapper;
import com.creativesaif.expert_internet_admin.Network.ApiInterface;
import com.creativesaif.expert_internet_admin.Network.RetrofitApiClient;
import com.creativesaif.expert_internet_admin.ProgressDialog;
import com.creativesaif.expert_internet_admin.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;


public class AddSalary extends AppCompatActivity {

    private EditText edAmount;
    private Spinner spinnerMonth, spinnerEmployee;
    private Button btnSubmit;

    private String amount, selectedMonth, selectedEmployee;

    //Declaring progress dialog
    private ProgressDialog progressDialog;
    private ApiInterface apiInterface;
    private Salary salary;
    private String admin_id;
    private SharedPreferences preferences;
    private String jwt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_salary);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        edAmount = findViewById(R.id.salaryamount);
        preferences = this.getSharedPreferences("users", MODE_PRIVATE);
        jwt = preferences.getString("jwt", null);
        admin_id = preferences.getString("admin_id", null);
        spinnerMonth = findViewById(R.id.monthListSpinner);
        spinnerEmployee = findViewById(R.id.employeeListSpinner);
        salary = new Salary();
        apiInterface = RetrofitApiClient.getClient().create(ApiInterface.class);

        //Set month list
        List<String> monthList = new ArrayList<>();
        monthList.add("---");
        monthList.add("January, 2023");
        monthList.add("February, 2023");
        monthList.add("March, 2023");
        monthList.add("April, 2023");
        monthList.add("May, 2023");
        monthList.add("June, 2023");
        monthList.add("July, 2023");
        monthList.add("August, 2023");
        monthList.add("September, 2023");
        monthList.add("October, 2023");
        monthList.add("November, 2023");
        monthList.add("December, 2023");

        ArrayAdapter<String> monthArrayAdapter = new ArrayAdapter<>(AddSalary.this,
                android.R.layout.simple_spinner_dropdown_item, monthList);
        spinnerMonth.setAdapter(monthArrayAdapter);

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // On selecting a spinner item
                selectedMonth = parentView.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        List<String> employeeList = new ArrayList<>();
        employeeList.add("---");
        employeeList.add("9588");
        employeeList.add("0713");

        ArrayAdapter<String> employeeArrayAdapter = new ArrayAdapter<>(AddSalary.this,
                android.R.layout.simple_spinner_dropdown_item, employeeList);
        spinnerEmployee.setAdapter(employeeArrayAdapter);

        spinnerEmployee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // On selecting a spinner item
                selectedEmployee = parentView.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        btnSubmit = findViewById(R.id.btnSalarysubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount = edAmount.getText().toString();
                if (amount.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Enter salary amount", Toast.LENGTH_SHORT).show();
                }else if(selectedMonth.equals("---")){
                    Toast.makeText(getApplicationContext(), "Select month of salary", Toast.LENGTH_SHORT).show();

                }else if(selectedEmployee.equals("---")){
                    Toast.makeText(getApplicationContext(), "Select employee", Toast.LENGTH_SHORT).show();
                }
                else if(!isNetworkConnected()){
                    Toast.makeText(getApplicationContext(), "Check internet connection", Toast.LENGTH_SHORT).show();

                }else{
                    salary.setJwt(jwt);
                    salary.setAdmin_id(admin_id);
                    salary.setAmount(amount);
                    salary.setMonth(selectedMonth);
                    salary.setEmployee_id(selectedEmployee);
                    addSalary(salary);

                }
            }
        });

    }

    public void addSalary(Salary mSalary) {
        progressDialog.showDialog();
        Call<SalaryWrapper> call = apiInterface.addSalary(mSalary);
        call.enqueue(new Callback<SalaryWrapper>() {
            @Override
            public void onResponse(Call<SalaryWrapper> call, retrofit2.Response<SalaryWrapper> response) {

                progressDialog.hideDialog();

                SalaryWrapper salaryWrapper = response.body();
                assert salaryWrapper != null;

                if (salaryWrapper.getStatus() == 401) {
                    //Go to phone verification step
                    Toast.makeText(getApplicationContext(), salaryWrapper.getMessage(), Toast.LENGTH_LONG).show();

                    finish();
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);

                } else if (salaryWrapper.getStatus() == 200) {
                    Toast.makeText(getApplicationContext(), salaryWrapper.getMessage(), Toast.LENGTH_LONG).show();
                    finish();

                }else {
                    Toast.makeText(getApplicationContext(), salaryWrapper.getMessage(), Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onFailure(Call<SalaryWrapper> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
                finish();

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
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
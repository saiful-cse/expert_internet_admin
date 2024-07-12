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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creativesaif.expert_internet_admin.Login;
import com.creativesaif.expert_internet_admin.Model.Salary;
import com.creativesaif.expert_internet_admin.Model.SalaryWrapper;
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
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;


public class AddSalary extends AppCompatActivity {

    private EditText edAmount;

    public Spinner employee_spinner, spinnerMonth;
    private Button btnSubmit;

    private String amount, selectedMonth, selectedEmployee;

    //Declaring progress dialog
    private ProgressDialog progressDialog;
    private ApiInterface apiInterface;
    private Salary salary;
    private String employee_id;
    private SharedPreferences preferences;
    private String jwt;
    private JSONArray jsonArrayEmployee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_salary);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        edAmount = findViewById(R.id.salaryamount);
        preferences = this.getSharedPreferences("users", MODE_PRIVATE);
        jwt = preferences.getString("jwt", null);
        employee_id = preferences.getString("employee_id", null);
        spinnerMonth = findViewById(R.id.monthListSpinner);
        employee_spinner = findViewById(R.id.employee_spinner);
        salary = new Salary();
        apiInterface = RetrofitApiClient.getClient().create(ApiInterface.class);

        //Set month list
        List<String> monthList = new ArrayList<>();
        monthList.add("---");
        monthList.add("March, 2024");
        monthList.add("April, 2024");
        monthList.add("May, 2024");
        monthList.add("June, 2024");
        monthList.add("July, 2024");
        monthList.add("August, 2024");
        monthList.add("September, 2024");
        monthList.add("October, 2024");
        monthList.add("November, 2024");
        monthList.add("December, 2024");

        ArrayAdapter<String> monthArrayAdapter = new ArrayAdapter<>(AddSalary.this,
                android.R.layout.simple_spinner_dropdown_item, monthList);
        spinnerMonth.setAdapter(monthArrayAdapter);

        employee_load();

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

        employee_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // On selecting a spinner item
                selectedEmployee = "---";
                try {
                    JSONObject json = jsonArrayEmployee.getJSONObject(position);
                    selectedEmployee = json.getString("employee_id");

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

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

                }else if(selectedEmployee.equals("9161") || selectedEmployee.equals("8991")){
                    Toast.makeText(getApplicationContext(), "Select employee", Toast.LENGTH_SHORT).show();
                }
                else if(!isNetworkConnected()){
                    Toast.makeText(getApplicationContext(), "Check internet connection", Toast.LENGTH_SHORT).show();

                } else{
                    salary.setJwt(jwt);
                    salary.setSuperAdminId(employee_id);
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

    public void employee_load()
    {
        String url = URL_config.BASE_URL+URL_config.EMPLOYEE_LIST;

        progressDialog.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.hideDialog();
                ArrayList<String> employee_list = new ArrayList<>();
                try {

                    jsonArrayEmployee = new JSONArray(response);

                    for(int i=0; i<jsonArrayEmployee.length(); i++) {
                        JSONObject jsonObjectItem = jsonArrayEmployee.getJSONObject(i);
                        employee_list.add(jsonObjectItem.getString("name"));
                    }

                    ArrayAdapter<String> EmployeeArrayAdapter = new ArrayAdapter<>(AddSalary.this,
                            android.R.layout.simple_spinner_dropdown_item, employee_list);
                    employee_spinner.setAdapter(EmployeeArrayAdapter);


                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                Toast.makeText(AddSalary.this,error.toString(),Toast.LENGTH_LONG).show();
                finish();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance().addToRequestQueue(stringRequest);;
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
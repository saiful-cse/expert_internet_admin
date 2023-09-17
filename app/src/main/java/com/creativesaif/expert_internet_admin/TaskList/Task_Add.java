package com.creativesaif.expert_internet_admin.TaskList;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.creativesaif.expert_internet_admin.MainActivity;
import com.creativesaif.expert_internet_admin.MySingleton;
import com.creativesaif.expert_internet_admin.ProgressDialog;
import com.creativesaif.expert_internet_admin.R;
import com.creativesaif.expert_internet_admin.URL_config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Task_Add extends AppCompatActivity {

    private SharedPreferences preferences;
    private Button btnAdd;
    private String description, selectedEmployee;
    private EditText edDescription;
    private Spinner employee_spinner;
    private ProgressDialog progressDialog;
    private JSONArray jsonArrayEmployee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_add);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferences = getSharedPreferences("users", MODE_PRIVATE);
        btnAdd = findViewById(R.id.btnTaskAdd);
        edDescription = findViewById(R.id.edDescription);
        employee_spinner = findViewById(R.id.employeeSpinner);
        progressDialog = new ProgressDialog(this);

        edDescription.setText(description);
        if (!isNetworkConnected()) {
            Toast.makeText(getApplicationContext(), "Please!! Check internet connection.", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            employee_load();
        }

        employee_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // On selecting a spinner item
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

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                description = edDescription.getText().toString().trim();
                if(description.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Write a task", Toast.LENGTH_SHORT).show();
                }
                if (!isNetworkConnected()) {
                    Toast.makeText(getApplicationContext(), "Please!! Check internet connection.", Toast.LENGTH_SHORT).show();
                }else{
                    task_add();
                }
            }
        });
    }

    //Internet connection check
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
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

                    ArrayAdapter<String> AreaArrayAdapter = new ArrayAdapter<>(Task_Add.this,
                            android.R.layout.simple_spinner_dropdown_item, employee_list);
                    employee_spinner.setAdapter(AreaArrayAdapter);

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                Toast.makeText(Task_Add.this,error.toString(),Toast.LENGTH_LONG).show();
                finish();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance().addToRequestQueue(stringRequest);;
    }


    public void task_add()
    {
        String url = URL_config.BASE_URL+URL_config.TASK_ADD;
        progressDialog.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("status").equals("200"))
                    {
                        Toast.makeText(Task_Add.this,jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                        finish();

                    }else{
                        Toast.makeText(Task_Add.this,jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                Toast.makeText(Task_Add.this,error.toString(),Toast.LENGTH_LONG).show();
                finish();
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                map.put("description", description);
                map.put("assignBy", Objects.requireNonNull(preferences.getString("employee_id", null)));
                map.put("assignOn", selectedEmployee);
                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance().addToRequestQueue(stringRequest);;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
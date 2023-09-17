package com.creativesaif.expert_internet_admin.TaskList;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creativesaif.expert_internet_admin.ClientList.ClientDetails;
import com.creativesaif.expert_internet_admin.DeviceUrl.DeviceUrl;
import com.creativesaif.expert_internet_admin.Employees.Employee;
import com.creativesaif.expert_internet_admin.Login;
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

public class Task_Edit_Delete extends AppCompatActivity {

    private SharedPreferences preferences;
    private Button btnDelete, btnDone, btnUpdate;
    private String taskId, description, assignBy, assignOn, selectedEmployee, assignOnAsName;
    private EditText edDescription;
    private Spinner employee_spinner;
    private ProgressDialog progressDialog;
    private JSONArray jsonArrayEmployee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit_delete);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferences = getSharedPreferences("users", MODE_PRIVATE);
        btnDelete = findViewById(R.id.btnDelte);
        btnDone = findViewById(R.id.btnDone);
        btnUpdate = findViewById(R.id.btnUpdate);
        edDescription = findViewById(R.id.edDescription);
        employee_spinner = findViewById(R.id.employeeSpinner);
        progressDialog = new ProgressDialog(this);

        taskId = getIntent().getStringExtra("taskId");
        description = getIntent().getStringExtra("description");
        assignBy = getIntent().getStringExtra("assignBy");
        assignOn = getIntent().getStringExtra("assignOn");

        edDescription.setText(description);
        if (!isNetworkConnected()) {
            Toast.makeText(getApplicationContext(), "Please!! Check internet connection.", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            employee_load();
        }

        if (Objects.equals(preferences.getString("super_admin", null), "1")){
            btnDelete.setVisibility(View.VISIBLE);
            btnDone.setVisibility(View.VISIBLE);
            btnUpdate.setVisibility(View.VISIBLE);

        }else if(Objects.equals(preferences.getString("employee_id", null), assignBy)){
            btnDelete.setVisibility(View.VISIBLE);
            btnDone.setVisibility(View.VISIBLE);
            btnUpdate.setVisibility(View.VISIBLE);

        }else if(Objects.equals(preferences.getString("employee_id", null), assignOn)){
            btnDelete.setVisibility(View.GONE);
            btnDone.setVisibility(View.VISIBLE);
            btnUpdate.setVisibility(View.GONE);
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

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNetworkConnected()) {
                    Toast.makeText(getApplicationContext(), "Please!! Check internet connection.", Toast.LENGTH_SHORT).show();
                }else{
                    warningTaskDone();
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNetworkConnected()) {
                    Toast.makeText(getApplicationContext(), "Please!! Check internet connection.", Toast.LENGTH_SHORT).show();
                }else{
                    warningTaskDelete();
                }
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                description = edDescription.getText().toString().trim();
                if (description.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Write task description", Toast.LENGTH_SHORT).show();
                }else if(!isNetworkConnected()){
                    Toast.makeText(getApplicationContext(), "Please!! Check internet connection.", Toast.LENGTH_SHORT).show();
                }
                else{
                    task_update();
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

                    ArrayAdapter<String> AreaArrayAdapter = new ArrayAdapter<>(Task_Edit_Delete.this,
                            android.R.layout.simple_spinner_dropdown_item, employee_list);
                    employee_spinner.setAdapter(AreaArrayAdapter);

                    //Assigning database value into Spinner
                    for(int i=0; i<jsonArrayEmployee.length(); i++) {
                        JSONObject jsonObjectItem = jsonArrayEmployee.getJSONObject(i);
                        if (jsonObjectItem.getString("employee_id").equals(assignOn)){
                            assignOnAsName = jsonObjectItem.getString("name");
                        }
                    }
                    int spinnerPosition = AreaArrayAdapter.getPosition(assignOnAsName);
                    employee_spinner.setSelection(spinnerPosition);

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                Toast.makeText(Task_Edit_Delete.this,error.toString(),Toast.LENGTH_LONG).show();
                finish();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance().addToRequestQueue(stringRequest);;
    }


    public void task_update()
    {
        String url = URL_config.BASE_URL+URL_config.TASK_UPDATE;
        progressDialog.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("status").equals("200"))
                    {
                        Toast.makeText(Task_Edit_Delete.this,jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Task_Edit_Delete.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }else{
                        Toast.makeText(Task_Edit_Delete.this,jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                Toast.makeText(Task_Edit_Delete.this,error.toString(),Toast.LENGTH_LONG).show();
                finish();
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                map.put("taskId", taskId);
                map.put("description", description);
                map.put("assignBy", assignBy);
                map.put("assignOn", selectedEmployee);
                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance().addToRequestQueue(stringRequest);;
    }

    public void task_delete()
    {
        String url = URL_config.BASE_URL+URL_config.TASK_DELETE+taskId;
        progressDialog.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("status").equals("200"))
                    {
                        Toast.makeText(Task_Edit_Delete.this,jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Task_Edit_Delete.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }else{
                        Toast.makeText(Task_Edit_Delete.this,jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                Toast.makeText(Task_Edit_Delete.this,error.toString(),Toast.LENGTH_LONG).show();
                finish();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance().addToRequestQueue(stringRequest);;
    }

    public void task_done_update()
    {
        String url = URL_config.BASE_URL+URL_config.TASK_DONE_UPDATE+taskId;

        progressDialog.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("status").equals("200"))
                    {
                        Toast.makeText(Task_Edit_Delete.this,jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Task_Edit_Delete.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }else{
                        Toast.makeText(Task_Edit_Delete.this,jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                Toast.makeText(Task_Edit_Delete.this,error.toString(),Toast.LENGTH_LONG).show();
                finish();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance().addToRequestQueue(stringRequest);;
    }

    public void warningTaskDelete(){
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(this);
        alert.setCancelable(true);
        alert.setTitle("Warning!!");
        alert.setMessage("আপনি কাজটি ডিলিট করতে চান?");
        alert.setIcon(R.drawable.warning_icon);

        alert.setPositiveButton("হ্যা", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                task_delete();
            }
        });

        alert.setNegativeButton("এখনো করিনাই", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        android.app.AlertDialog dlg = alert.create();
        dlg.show();
    }

    public void warningTaskDone(){
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(this);
        alert.setCancelable(true);
        alert.setTitle("Warning!!");
        alert.setMessage("আপনি কাজটি যথাযতভাবে সম্পর্ন করেছেন?");
        alert.setIcon(R.drawable.warning_icon);

        alert.setPositiveButton("হ্যা", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                task_done_update();
            }
        });

        alert.setNegativeButton("এখনো করিনাই", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        android.app.AlertDialog dlg = alert.create();
        dlg.show();
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
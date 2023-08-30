package com.creativesaif.expert_internet_admin.Employees;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creativesaif.expert_internet_admin.Login;
import com.creativesaif.expert_internet_admin.MainActivity;
import com.creativesaif.expert_internet_admin.MySingleton;
import com.creativesaif.expert_internet_admin.ProgressDialog;
import com.creativesaif.expert_internet_admin.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Employee_add extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private String employeeId, name, address, mobile, pin, about;

    private EditText edName, edEmpId, edAddress, edMobile, edPin, edAbout;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_add);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(this);

        edName = findViewById(R.id.edEmpName);
        edEmpId = findViewById(R.id.edEmpid);
        edAddress = findViewById(R.id.edEmpAddress);
        edMobile = findViewById(R.id.edEmpMobile);
        edPin = findViewById(R.id.edEmpPin);
        edAbout = findViewById(R.id.edEmpAbout);
        addButton = findViewById(R.id.btnAdd);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                employeeId = edEmpId.getText().toString().trim();
                name = edName.getText().toString().trim();
                address = edAddress.getText().toString().trim();
                mobile = edMobile.getText().toString().trim();
                pin = edPin.getText().toString().trim();
                about = edAbout.getText().toString().trim();


                if (employeeId.isEmpty() || employeeId.length() < 4){
                    Toast.makeText(Employee_add.this,"Employee ID must be 4 digit",Toast.LENGTH_LONG).show();

                }else if(name.isEmpty()){
                    Toast.makeText(Employee_add.this,"Enter name",Toast.LENGTH_LONG).show();

                }else if(address.isEmpty()){
                    Toast.makeText(Employee_add.this,"Enter correct address",Toast.LENGTH_LONG).show();

                }else if(mobile.isEmpty() || mobile.length() < 11){
                    Toast.makeText(Employee_add.this,"Mobile number must be 11 digit",Toast.LENGTH_LONG).show();

                }else if(pin.isEmpty() || pin.length() < 4){
                    Toast.makeText(Employee_add.this,"Pin must be 4 digit",Toast.LENGTH_LONG).show();

                }else if(about.isEmpty()){
                    Toast.makeText(Employee_add.this,"Enter about of employee",Toast.LENGTH_LONG).show();

                }else if(!isNetworkConnected()){
                    Toast.makeText(Employee_add.this,"Check Internet Connection.",Toast.LENGTH_LONG).show();
                }else {
                    employee_add();
                }
            }
        });
    }

    //Internet connection check
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
    public void employee_add()
    {
        String url = "http://192.168.1.8/api/expert_internet_api/exp-v5.0/employee/employee_add.php";

        progressDialog.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("status").equals("200"))
                    {
                        Toast.makeText(Employee_add.this,jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Employee_add.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }else{
                        Toast.makeText(Employee_add.this,jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                Toast.makeText(Employee_add.this,error.toString(),Toast.LENGTH_LONG).show();
                finish();
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                map.put("employee_id", employeeId);
                map.put("name", name);
                map.put("address", address);
                map.put("mobile", mobile);
                map.put("about", about);
                map.put("pin", pin);
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
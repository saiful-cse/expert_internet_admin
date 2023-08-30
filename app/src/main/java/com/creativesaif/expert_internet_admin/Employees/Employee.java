package com.creativesaif.expert_internet_admin.Employees;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.creativesaif.expert_internet_admin.ClientList.ClientDetailsEdit;
import com.creativesaif.expert_internet_admin.Login;
import com.creativesaif.expert_internet_admin.MainActivity;
import com.creativesaif.expert_internet_admin.MySingleton;
import com.creativesaif.expert_internet_admin.ProgressDialog;
import com.creativesaif.expert_internet_admin.R;
import com.creativesaif.expert_internet_admin.Salary.SalaryList;
import com.creativesaif.expert_internet_admin.TransactionList.TransactionList;
import com.creativesaif.expert_internet_admin.URL_config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Employee extends AppCompatActivity {

    private Spinner employee_spinner;
    private ProgressDialog progressDialog;
    private String indexId, employeeId, name, address, mobile, pin, about, super_admin;
    private JSONArray jsonArrayEmployee;
    private SharedPreferences sharedPreferences;
    private EditText edName, edEmpId, edAddress, edMobile, edPin, edAbout;

    private Switch switchDash, switchClientAdd, switchClDeUpdate, switchSms, switchTxnSumm,
    switchTxnEdit, switchUpstreBill, switchSalaraAdd, switchDevice, switchNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        employee_spinner = findViewById(R.id.employeeSpinner);
        progressDialog = new ProgressDialog(this);

        edName = findViewById(R.id.edEmpName);
        edEmpId = findViewById(R.id.edEmpid);
        edAddress = findViewById(R.id.edEmpAddress);
        edMobile = findViewById(R.id.edEmpMobile);
        edPin = findViewById(R.id.edEmpPin);
        edAbout = findViewById(R.id.edEmpAbout);

        switchDash = findViewById(R.id.swdashboard);
        switchClientAdd = findViewById(R.id.swclientadd);
        switchClDeUpdate = findViewById(R.id.swcldetaupda);
        switchSms = findViewById(R.id.swsms);
        switchTxnSumm = findViewById(R.id.swtxnsumm);
        switchTxnEdit = findViewById(R.id.swtxnedit);
        switchUpstreBill = findViewById(R.id.swupstrbill);
        switchSalaraAdd = findViewById(R.id.swsaladd);
        switchDevice = findViewById(R.id.swdevice);
        switchNote = findViewById(R.id.swnote);

        sharedPreferences = getApplicationContext().getSharedPreferences("users", MODE_PRIVATE);

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
                //selectedEmployeeid = parentView.getItemAtPosition(position).toString();
                try {
                    //Getting object of given index
                    JSONObject json = jsonArrayEmployee.getJSONObject(position);

                    //Fetching name from that object
                    //selectedEmployeeid = json.getString("id");
                    employee_details_load(json.getString("id"));
                    //Toast.makeText(Employee.this,selectedEmployeeid,Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
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

                    ArrayAdapter<String> AreaArrayAdapter = new ArrayAdapter<>(Employee.this,
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
                Toast.makeText(Employee.this,error.toString(),Toast.LENGTH_LONG).show();
                finish();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance().addToRequestQueue(stringRequest);;
    }

    public void employee_details_load(String id)
    {
        String url = "http://192.168.1.8/api/expert_internet_api/exp-v5.0/employee/employee_details.php";

        progressDialog.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    for(int i=0; i<jsonObject.length(); i++) {

                        //Store in String variable
                        indexId = jsonObject.getString("id");
                        super_admin = jsonObject.getString("super_admin");

                        //Set the EditText field
                        edEmpId.setText(jsonObject.getString("employee_id"));
                        edName.setText(jsonObject.getString("name"));
                        edAddress.setText(jsonObject.getString("address"));
                        edMobile.setText(jsonObject.getString("mobile"));
                        edPin.setText(jsonObject.getString("pin"));
                        edAbout.setText(jsonObject.getString("about"));

                        switchDash.setChecked(jsonObject.getString("dashboard").equals("1"));
                        switchClientAdd.setChecked(jsonObject.getString("client_add").equals("1"));
                        switchClDeUpdate.setChecked(jsonObject.getString("client_details_update").equals("1"));
                        switchSms.setChecked(jsonObject.getString("sms").equals("1"));
                        switchTxnSumm.setChecked(jsonObject.getString("txn_summary").equals("1"));
                        switchTxnEdit.setChecked(jsonObject.getString("txn_edit").equals("1"));
                        switchUpstreBill.setChecked(jsonObject.getString("upstream_bill").equals("1"));
                        switchSalaraAdd.setChecked(jsonObject.getString("salary_add").equals("1"));
                        switchDevice.setChecked(jsonObject.getString("device").equals("1"));
                        switchNote.setChecked(jsonObject.getString("note").equals("1"));

                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                Toast.makeText(Employee.this,error.toString(),Toast.LENGTH_LONG).show();
                finish();
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                map.put("id", id);
                return map;

            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance().addToRequestQueue(stringRequest);;

    }


    public void employee_details_update()
    {
        String url = "http://192.168.1.8/api/expert_internet_api/exp-v5.0/employee/employee_details_update.php";

        progressDialog.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("status").equals("200"))
                    {
                        Toast.makeText(Employee.this,jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                        employee_load();
                    }
                    else{
                        Toast.makeText(Employee.this,jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                Toast.makeText(Employee.this,error.toString(),Toast.LENGTH_LONG).show();
                finish();
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                map.put("id", indexId);
                map.put("employee_id", employeeId);
                map.put("name", name);
                map.put("address", address);
                map.put("mobile", mobile);
                map.put("about", about);
                map.put("pin", pin);
                map.put("super_admin", super_admin);
                map.put("dashboard", (switchDash.isChecked()) ? "1" : "0");
                map.put("client_add", (switchClientAdd.isChecked()) ? "1" : "0");
                map.put("client_details_update", (switchClDeUpdate.isChecked()) ? "1" : "0");
                map.put("sms", (switchSms.isChecked()) ? "1" : "0");
                map.put("txn_summary", (switchTxnSumm.isChecked()) ? "1" : "0");
                map.put("txn_edit", (switchTxnEdit.isChecked()) ? "1" : "0");
                map.put("upstream_bill", (switchUpstreBill.isChecked()) ? "1" : "0");
                map.put("salary_add", (switchSalaraAdd.isChecked()) ? "1" : "0");
                map.put("device", (switchDevice.isChecked()) ? "1" : "0");
                map.put("note", (switchNote.isChecked()) ? "1" : "0");
                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance().addToRequestQueue(stringRequest);;

    }

    public void employee_delete()
    {
        String url = "http://192.168.1.8/api/expert_internet_api/exp-v5.0/employee/employee_delete.php";

        progressDialog.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("status").equals("200"))
                    {
                        Toast.makeText(Employee.this,jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                        employee_load();
                    }else{
                        Toast.makeText(Employee.this,jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                Toast.makeText(Employee.this,error.toString(),Toast.LENGTH_LONG).show();
                finish();
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                map.put("id", indexId);
                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance().addToRequestQueue(stringRequest);;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.employee, menu);
        return true;
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
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_item_delete) {

            name = edName.getText().toString().trim();
            deltewarningshow();
            return true;
        }
        if (id == R.id.action_item_add) {

            startActivity(new Intent(Employee.this, Employee_add.class));
            return true;
        }

        if (id == R.id.action_item_update) {

            employeeId = edEmpId.getText().toString().trim();
            name = edName.getText().toString().trim();
            address = edAddress.getText().toString().trim();
            mobile = edMobile.getText().toString().trim();
            pin = edPin.getText().toString().trim();
            about = edAbout.getText().toString().trim();
            if(super_admin.equals("1")){
                super_admin = "1";
            }else{
                super_admin = "0";
            }

            if (employeeId.isEmpty() || employeeId.length() < 4){
                Toast.makeText(Employee.this,"Employee ID must be 4 digit",Toast.LENGTH_LONG).show();

            }else if(name.isEmpty()){
                Toast.makeText(Employee.this,"Enter name",Toast.LENGTH_LONG).show();

            }else if(address.isEmpty()){
                Toast.makeText(Employee.this,"Enter correct address",Toast.LENGTH_LONG).show();

            }else if(mobile.isEmpty() || mobile.length() < 11){
                Toast.makeText(Employee.this,"Mobile number must be 11 digit",Toast.LENGTH_LONG).show();

            }else if(pin.isEmpty() || pin.length() < 4){
                Toast.makeText(Employee.this,"Pin must be 4 digit",Toast.LENGTH_LONG).show();

            }else if(about.isEmpty()){
                Toast.makeText(Employee.this,"Enter about of employee",Toast.LENGTH_LONG).show();

            }
            else if(!isNetworkConnected()){
                Toast.makeText(Employee.this,"Check Internet Connection.",Toast.LENGTH_LONG).show();
            }else {

                updatewarningshow();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void updatewarningshow(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(true);
        alert.setTitle("Warning!!");
        alert.setMessage("আপনি কি কর্মচারী "+name+" তথ্য ও পারমিশন আপডেট করতে চান?");
        alert.setIcon(R.drawable.ic_baseline_warning_24);

        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                employee_details_update();
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

    public void deltewarningshow(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(true);
        alert.setTitle("Warning!!");
        alert.setMessage("আপনি কি কর্মচারী "+name+" তথ্য ও পারমিশন ডিলেট করতে চান? যা পুনরায় ফিরিয়ে আনা যাবেনা।");
        alert.setIcon(R.drawable.ic_baseline_warning_24);

        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                employee_delete();
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


}
package com.creativesaif.expert_internet_admin.TransactionList;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creativesaif.expert_internet_admin.ClientList.ClientDetails;
import com.creativesaif.expert_internet_admin.Login;
import com.creativesaif.expert_internet_admin.MySingleton;
import com.creativesaif.expert_internet_admin.ProgressDialog;
import com.creativesaif.expert_internet_admin.R;
import com.creativesaif.expert_internet_admin.Sms.SmsCreate;
import com.creativesaif.expert_internet_admin.URL_config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TransactionEdit extends AppCompatActivity {

    EditText edTxnId, edDate, edEmpId, edDetails, edCredit, edDebit;
    String jwt, txnId, date, empId, details, credit, debit;
    Button btnSearch, btnDelete, btnUpdate;
    TextView tvClientId, tvClientName, tvtype;
    private SharedPreferences sharedPreferences;
    ProgressDialog progressDialog;

    CardView cardViewDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);

        //txn details invisible
        cardViewDetails = findViewById(R.id.details_card);
        cardViewDetails.setVisibility(View.GONE);

        sharedPreferences = getApplicationContext().getSharedPreferences("users", MODE_PRIVATE);

        /*
        ID's initialize
         */
        //edit text
        edTxnId = findViewById(R.id.input_txn_id);
        edDate = findViewById(R.id.edDate);
        edEmpId = findViewById(R.id.edEmpid);
        edDetails = findViewById(R.id.editDetails);
        edCredit = findViewById(R.id.editAmountofCredit);
        edDebit = findViewById(R.id.editAmountofDebit);


        //button
        btnSearch = findViewById(R.id.btn_txn_search);
        btnDelete = findViewById(R.id.btn_delete);
        btnUpdate = findViewById(R.id.btn_update);

        //text view
        tvClientId = findViewById(R.id.tv_client_id);
        tvClientName = findViewById(R.id.tv_client_name);
        tvtype = findViewById(R.id.tv_payment_type);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txnId = edTxnId.getText().toString().trim();
                String admin_id = sharedPreferences.getString("employee_id", null);

                if(!isNetworkConnected())
                {
                    Snackbar.make(findViewById(android.R.id.content),"Please!! Check Internet Connection or Try again later.",Snackbar.LENGTH_LONG).show();

                }else if(txnId.isEmpty())
                {
                    Snackbar.make(findViewById(android.R.id.content),"Write a txn id",Snackbar.LENGTH_LONG).show();

                }else {
                    assert admin_id != null;
                    if(admin_id.equals("9161")) {

                        txn_load(txnId);

                    }else {
                        Toast.makeText(getApplicationContext(), "You are not permitted to edit", Toast.LENGTH_LONG).show();

                    }
                }

            }
        });


        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                date = edDate.getText().toString().trim();
                jwt = sharedPreferences.getString("jwt", null);

                if (jwt == null){
                    finish();
                    startActivity(new Intent(TransactionEdit.this, Login.class));

                }else if(!isNetworkConnected())
                {
                    Snackbar.make(findViewById(android.R.id.content),"Please!! Check Internet Connection or Try again later.",Snackbar.LENGTH_LONG).show();

                }else {

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar cal = Calendar.getInstance();
                    //5 day decrease from current date
                    cal.add(Calendar.DAY_OF_MONTH, -5);
                    Date currentDate = cal.getTime();

                    try {
                        Date txnDate = sdf.parse(date);
                        int result = currentDate.compareTo(txnDate);

                        //if current date is before txndate
                        if (result <= 0) {

                            deleteDialog();
                        } else {
                            //System.out.println("no editable");
                            Toast.makeText(getApplicationContext(), "Time expired, 5 day over.", Toast.LENGTH_LONG).show();
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            }
        });


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                txnId = edTxnId.getText().toString().trim();
                date = edDate.getText().toString().trim();

                details = edDetails.getText().toString().trim();
                empId = edEmpId.getText().toString().trim();
                credit = edCredit.getText().toString().trim();
                debit = edDebit.getText().toString().trim();
                jwt = sharedPreferences.getString("jwt", null);

                if (jwt == null){
                    finish();
                    startActivity(new Intent(TransactionEdit.this, Login.class));

                } else if(txnId.isEmpty()){
                    Snackbar.make(findViewById(android.R.id.content),"Txn ID cannot empty",Snackbar.LENGTH_LONG).show();

                }else if(date.isEmpty()){
                    Snackbar.make(findViewById(android.R.id.content),"Date cannot empty",Snackbar.LENGTH_LONG).show();

                }else if(empId.isEmpty()){
                    Snackbar.make(findViewById(android.R.id.content),"Admin ID cannot empty",Snackbar.LENGTH_LONG).show();

                }else if(details.isEmpty()){
                    Snackbar.make(findViewById(android.R.id.content),"Details cannot empty",Snackbar.LENGTH_LONG).show();

                }else if(credit.isEmpty()){
                    Snackbar.make(findViewById(android.R.id.content),"Credit cannot empty",Snackbar.LENGTH_LONG).show();

                }else if(debit.isEmpty()){
                    Snackbar.make(findViewById(android.R.id.content),"Debit cannot empty",Snackbar.LENGTH_LONG).show();

                }else if(!isNetworkConnected())
                {
                    Snackbar.make(findViewById(android.R.id.content),"Please!! Check Internet Connection or Try again later.",Snackbar.LENGTH_LONG).show();

                }
                else {

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar cal = Calendar.getInstance();
                    //5 day decrease from current date
                    cal.add(Calendar.DAY_OF_MONTH, -5);
                    Date currentDate = cal.getTime();
                    try {

                        Date txnDate = sdf.parse(date);
                        int result = currentDate.compareTo(txnDate);

                        //if current date is before txndate
                        if (result <= 0) {

                            txn_update();

                        } else {
                            //System.out.println("no editable");
                            Toast.makeText(getApplicationContext(), "Time expired, 5 day over.", Toast.LENGTH_LONG).show();
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }

    public void txn_load(String txnId)
    {
        progressDialog.showDialog();
        String url = URL_config.BASE_URL+URL_config.TXN_DETAILS +"?txn_id="+txnId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.hideDialog();
                try{

                    JSONObject jsonObject = new JSONObject(response);

                    boolean m = jsonObject.has("message");
                    if (m)
                    {
                        cardViewDetails.setVisibility(View.GONE);
                        String message = jsonObject.getString("message");
                        Toast.makeText(TransactionEdit.this,message,Toast.LENGTH_LONG).show();

                    }else
                    {
                        cardViewDetails.setVisibility(View.VISIBLE);
                        JSONArray jsonArray = jsonObject.getJSONArray("txn_details");

                        for (int i=0; i<=jsonArray.length(); i++)
                        {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            tvClientId.setText(jsonObject1.getString("client_id"));
                            tvClientName.setText(jsonObject1.getString("name"));
                            tvtype.setText(jsonObject1.getString("type"));

                            edDate.setText(jsonObject1.getString("date"));
                            edEmpId.setText(jsonObject1.getString("emp_id"));
                            edDetails.setText(jsonObject1.getString("details"));
                            edCredit.setText(jsonObject1.getString("credit"));
                            edDebit.setText(jsonObject1.getString("debit"));

                        }

                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                Toast.makeText(TransactionEdit.this,error.toString(),Toast.LENGTH_LONG).show();
                finish();
            }
        });
        MySingleton.getInstance().addToRequestQueue(stringRequest);

    }


    public void txn_delete(final String txnId)
    {
        progressDialog.showDialog();
        String url = URL_config.BASE_URL+URL_config.TXN_DELETE;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.hideDialog();

                try{

                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("200")){
                        Toast.makeText(TransactionEdit.this, message,Toast.LENGTH_LONG).show();
                        finish();

                    }else if(status.equals("401")){

                        warningShow(message);

                    }else{
                        Toast.makeText(TransactionEdit.this, message,Toast.LENGTH_LONG).show();
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                Toast.makeText(TransactionEdit.this,error.toString(),Toast.LENGTH_LONG).show();
                finish();
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                map.put("jwt", jwt);
                map.put("txn_id", txnId);
                return map;

            }
        };
        MySingleton.getInstance().addToRequestQueue(stringRequest);

    }

    public void txn_update()
    {
        progressDialog.showDialog();
        String url = URL_config.BASE_URL+URL_config.TXN_UPDATE;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                progressDialog.hideDialog();

                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("200")){
                        Toast.makeText(TransactionEdit.this, message,Toast.LENGTH_LONG).show();
                        finish();

                    }else if(status.equals("401")){

                        Toast.makeText(TransactionEdit.this, message,Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(TransactionEdit.this, Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }else{
                        Toast.makeText(TransactionEdit.this, message,Toast.LENGTH_LONG).show();
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                Toast.makeText(TransactionEdit.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                map.put("jwt", jwt);
                map.put("txn_id", txnId);
                map.put("date", date);
                map.put("emp_id", empId);
                map.put("details", details);
                map.put("credit", credit);
                map.put("debit", debit);
                return map;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance().addToRequestQueue(stringRequest);
    }

    public void deleteDialog(){
        AlertDialog.Builder aleart1 = new AlertDialog.Builder(this);
        aleart1.setCancelable(true);
        aleart1.setTitle("Warning!!!");
        aleart1.setMessage("Are you sure want to permanently delete this transaction from database?");
        aleart1.setIcon(R.drawable.warning_icon);

        aleart1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                txn_delete(txnId);
            }
        });

        aleart1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog dlg = aleart1.create();
        dlg.show();
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

    public void warningShow(String message){
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(this);
        alert.setCancelable(false);
        alert.setTitle("Warning!!");
        alert.setMessage(message);
        alert.setIcon(R.drawable.warning_icon);

        alert.setPositiveButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
                startActivity(new Intent(TransactionEdit.this, Login.class));

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        android.app.AlertDialog dlg = alert.create();
        dlg.show();
    }
}

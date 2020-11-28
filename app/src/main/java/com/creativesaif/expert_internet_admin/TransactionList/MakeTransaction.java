package com.creativesaif.expert_internet_admin.TransactionList;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creativesaif.expert_internet_admin.ClientList.ClientDetails;
import com.creativesaif.expert_internet_admin.MySingleton;
import com.creativesaif.expert_internet_admin.ProgressDialog;
import com.creativesaif.expert_internet_admin.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MakeTransaction extends AppCompatActivity {

    ProgressDialog progressDialog;
    RadioGroup radioGroup, radioGroup2;
    EditText editTextAmount, editTextDetails;

    String txn_type, txn_method, amount, details;
    Button buttonSubmmit;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_transaction);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        radioGroup = findViewById(R.id.radioGroup);
        radioGroup2 = findViewById(R.id.radioGroup2);
        editTextAmount = findViewById(R.id.txnamount);
        editTextDetails = findViewById(R.id.txndetails);
        progressDialog = new ProgressDialog(this);
        buttonSubmmit = findViewById(R.id.btnsubmit);
        sharedPreferences = getApplicationContext().getSharedPreferences("users", MODE_PRIVATE);

        buttonSubmmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                amount = editTextAmount.getText().toString().trim();
                details = editTextDetails.getText().toString().trim();

                if(!isNetworkConnected())
                {
                    Snackbar.make(findViewById(android.R.id.content),"Please!! Check Internet Connection or Try again later.",Snackbar.LENGTH_LONG).show();

                }else if(radioGroup.getCheckedRadioButtonId() == -1)
                {
                    Snackbar.make(findViewById(android.R.id.content),"Select txn type",Snackbar.LENGTH_LONG).show();

                }else if(radioGroup2.getCheckedRadioButtonId() == -1){
                    Snackbar.make(findViewById(android.R.id.content),"Select txn method",Snackbar.LENGTH_LONG).show();

                } else if(amount.isEmpty())
                {
                    Snackbar.make(findViewById(android.R.id.content),"Write an amount",Snackbar.LENGTH_LONG).show();
                }else if(details.isEmpty())
                {
                    Snackbar.make(findViewById(android.R.id.content),"Write a details",Snackbar.LENGTH_LONG).show();
                }else {

                    int selectedTxnType = radioGroup.getCheckedRadioButtonId();
                    int selectedMethod = radioGroup2.getCheckedRadioButtonId();
                    RadioButton radioButton = findViewById(selectedTxnType);
                    RadioButton radioButton2 = findViewById(selectedMethod);
                    txn_type = radioButton.getText().toString().trim();
                    txn_method = radioButton2.getText().toString().trim();

                    //Snackbar.make(findViewById(android.R.id.content),txn_type,Snackbar.LENGTH_LONG).show();

                    make_txn();
                }


            }
        });


    }

    //Internet connection check
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public void make_txn()
    {
        progressDialog.showDialog();

        String url = getString(R.string.base_url)+getString(R.string.admin_txn);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                //Toast.makeText(MainActivity.this,response,Toast.LENGTH_SHORT).show();

                progressDialog.hideDialog();

                try{

                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");

                    if (message.equals("201"))
                    {
                        Toast.makeText(MakeTransaction.this,"Your transaction has been successfully.",Toast.LENGTH_LONG).show();
                        finish();

                    }else{
                        Toast.makeText(MakeTransaction.this,message,Toast.LENGTH_LONG).show();
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                Toast.makeText(MakeTransaction.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                map.put("type", txn_type);
                map.put("method", txn_method);
                map.put("amount", amount);
                map.put("details", details);
                map.put("userid", sharedPreferences.getString("userid", null));
                return map;

            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance().addToRequestQueue(stringRequest);
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

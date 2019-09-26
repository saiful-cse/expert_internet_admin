package com.creativesaif.expert_internet_admin.TransactionList;

import android.content.Context;
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
    RadioGroup radioGroup;
    EditText editTextAmount, editTextDetails;

    String txn_type, amount, details;
    Button buttonSubmmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_transaction);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        radioGroup = findViewById(R.id.radioGroup);
        editTextAmount = findViewById(R.id.txnamount);
        editTextDetails = findViewById(R.id.txndetails);
        progressDialog = new ProgressDialog(this);
        buttonSubmmit = findViewById(R.id.btnsubmit);

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

                }
                else if(amount.isEmpty())
                {
                    Snackbar.make(findViewById(android.R.id.content),"Write an amount",Snackbar.LENGTH_LONG).show();
                }else if(details.isEmpty())
                {
                    Snackbar.make(findViewById(android.R.id.content),"Write a details",Snackbar.LENGTH_LONG).show();
                }else {

                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    RadioButton radioButton = findViewById(selectedId);
                    txn_type = radioButton.getText().toString();

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
        //String url = getString(R.string.admin_txn_laravel);
        String url = getString(R.string.base_url)+getString(R.string.admin_txn);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                //Toast.makeText(MainActivity.this,response,Toast.LENGTH_SHORT).show();

                progressDialog.hideDialog();

                try{

                    JSONObject jsonObject = new JSONObject(response);

                    boolean m = jsonObject.has("message");
                    if (m)
                    {
                        String message = jsonObject.getString("message");
                        Toast.makeText(MakeTransaction.this,message,Toast.LENGTH_SHORT).show();
                        finish();

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
                map.put("amount", amount);
                map.put("details", details);
                return map;

            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 8, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
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

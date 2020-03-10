package com.creativesaif.expert_internet_admin.TransactionList;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creativesaif.expert_internet_admin.ClientList.ClientDetails;
import com.creativesaif.expert_internet_admin.Dashboard.Dashboard;
import com.creativesaif.expert_internet_admin.MainActivity;
import com.creativesaif.expert_internet_admin.MySingleton;
import com.creativesaif.expert_internet_admin.NewsFeed.NewsAdd;
import com.creativesaif.expert_internet_admin.ProgressDialog;
import com.creativesaif.expert_internet_admin.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class TransactionList extends AppCompatActivity implements View.OnClickListener{

    private TransactionMonthlyAdapter transactionMonthlyAdapter;
    private ArrayList<Transaction> transactionArrayList;
    RecyclerView recyclerView;

    ProgressDialog progressDialog;

    Button buttonDatePicker1,buttonDatePicker2, buttonTxnView;

    String first_date, last_date, total_credit, total_debit;
    private int mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        /*
        Initialize here
         */

        buttonDatePicker1 = findViewById(R.id.btn_datepicker1);
        buttonDatePicker2 = findViewById(R.id.btn_datepicker2);
        buttonTxnView = findViewById(R.id.btn_txn_view);

        buttonDatePicker1.setOnClickListener(this);
        buttonDatePicker2.setOnClickListener(this);
        buttonTxnView.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        transactionArrayList = new ArrayList<>();
        transactionMonthlyAdapter = new TransactionMonthlyAdapter(this,transactionArrayList);

        recyclerView = findViewById(R.id.recyclerViewTnxRow);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        //now set adapter to recyclerView
        recyclerView.setAdapter(transactionMonthlyAdapter);

        FloatingActionButton fab1 = findViewById(R.id.total);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Total Credit: "+total_credit+"\nTotal Debit: "+total_debit, Snackbar.LENGTH_LONG).show();

            }
        });

        FloatingActionButton fab3 = findViewById(R.id.txn_edit);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TransactionList.this, TransactionEdit.class));
            }
        });

        FloatingActionButton fab2 = findViewById(R.id.fab);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TransactionList.this, MakeTransaction.class));
            }
        });
    }


    @Override
    public void onClick(View v) {

        if (v == buttonDatePicker1){

            // Get first Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            buttonDatePicker1.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            first_date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();

        }else if(v == buttonDatePicker2){

            // Get first Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            buttonDatePicker2.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            last_date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();

        }else if(v == buttonTxnView){

            if (!isNetworkConnected()){
                Snackbar.make(findViewById(android.R.id.content),"Please!! Check Internet Connection or Try again later.",Snackbar.LENGTH_LONG).show();

            }else if(first_date == null){
                Snackbar.make(findViewById(android.R.id.content),"Select first date",Snackbar.LENGTH_LONG).show();

            }else if(last_date == null){
                Snackbar.make(findViewById(android.R.id.content),"Select last date",Snackbar.LENGTH_LONG).show();

            }
            else {

                load_txn();
            }

        }


    }

    //Internet connection check
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    private void load_txn() {

        progressDialog.showDialog();
        String url = getString(R.string.base_url)+getString(R.string.all_txn)+"?first_date="+first_date+"&last_date="+last_date;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Toast.makeText(ClientDetails.this,response,Toast.LENGTH_SHORT).show();
                progressDialog.hideDialog();

                try{

                    transactionArrayList.clear();
                    JSONObject jsonObject = new JSONObject(response);

                    boolean m = jsonObject.has("message");
                    if (m)
                    {
                        String message = jsonObject.getString("message");
                        Toast.makeText(TransactionList.this,message,Toast.LENGTH_LONG).show();

                    }else
                    {
                        /*
                        Load collection of limited transaction total
                         */
                        total_credit_debit_load();

                        JSONArray jsonArray = jsonObject.getJSONArray("all_txn");

                        for (int i=0; i<=jsonArray.length(); i++)
                        {
                            Transaction transaction = new Transaction();

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            transaction.setDate(jsonObject1.getString("date"));
                            transaction.setTxn_id(jsonObject1.getString("txn_id"));
                            transaction.setDetails(jsonObject1.getString("details"));
                            transaction.setCredit(jsonObject1.getString("credit"));
                            transaction.setDebit(jsonObject1.getString("debit"));

                            transactionArrayList.add(transaction);

                            transactionMonthlyAdapter.notifyDataSetChanged();
                        }

                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(TransactionList.this,error.toString(),Toast.LENGTH_LONG).show();
                progressDialog.hideDialog();
            }
        });
        MySingleton.getInstance().addToRequestQueue(stringRequest);
    }

    public void total_credit_debit_load()
    {
        String url = getString(R.string.base_url)+getString(R.string.total_credit_debit)+"?first_date="+first_date+"&last_date="+last_date;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Toast.makeText(Dashboard.this,response,Toast.LENGTH_SHORT).show();

                try{
                    JSONObject jsonObject = new JSONObject(response);
                    total_credit = jsonObject.getString("total_credit");
                    total_debit = jsonObject.getString("total_debit");


                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(TransactionList.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        });
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

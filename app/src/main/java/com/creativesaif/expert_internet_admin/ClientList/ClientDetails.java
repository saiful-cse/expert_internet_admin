package com.creativesaif.expert_internet_admin.ClientList;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creativesaif.expert_internet_admin.Dashboard.Dashboard;
import com.creativesaif.expert_internet_admin.Login;
import com.creativesaif.expert_internet_admin.MainActivity;
import com.creativesaif.expert_internet_admin.MySingleton;
import com.creativesaif.expert_internet_admin.NewsFeed.News;
import com.creativesaif.expert_internet_admin.NewsFeed.NewsAdapter;
import com.creativesaif.expert_internet_admin.NewsFeed.NewsAdd;
import com.creativesaif.expert_internet_admin.NewsFeed.NewsDetails;
import com.creativesaif.expert_internet_admin.Notice.NoticeCreate;
import com.creativesaif.expert_internet_admin.ProgressDialog;
import com.creativesaif.expert_internet_admin.R;
import com.creativesaif.expert_internet_admin.TransactionList.MakeTransaction;
import com.creativesaif.expert_internet_admin.TransactionList.Transaction;
import com.creativesaif.expert_internet_admin.TransactionList.TransactionAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClientDetails extends AppCompatActivity {

    Button btnDetailsEdit, btnSmsSend, btnSmsHistory;

    private TextView tvId, tvMode, tvName, tvPhone, tvAddress, tvEmail, tvArea,
            tvUsername, tvPassword,
            tvSpeed, tvFee, tvPaymentMethod, tvRegDate, tvActiveDate, tvInactiveDate;

    ImageView user_call;
    private String jwt, client_id, name, phone, user_id;

    private SharedPreferences sharedPreferences;

    private String informMessage;

    private CardView cardViewAlert;

    /*
    Payment Details
     */
// initialize adapter and data structure here
    private TransactionAdapter transactionAdapter;
    private ArrayList<Transaction> transactionArrayList;
    RecyclerView recyclerView;

    /*
    Make txn
     */
    private EditText editTextAmount, editTextInformSms;
    Button buttonTxnSubmit;
    String payment_type, payment_method, amount;
    RadioGroup radioGroup, radioGroup2;

    SwipeRefreshLayout swipeRefreshLayout;

    /*
   Progress dialog
    */
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*
        Txn ID initialize
         */
        swipeRefreshLayout = findViewById(R.id.details_refresh);

        editTextAmount = findViewById(R.id.edAmount);
        buttonTxnSubmit = findViewById(R.id.txn_submit);
        radioGroup = findViewById(R.id.radioGroup);
        /*
        ID initialize
         */
        cardViewAlert = findViewById(R.id.cardViewAlert);

        sharedPreferences = getApplicationContext().getSharedPreferences("users", MODE_PRIVATE);

        user_call = findViewById(R.id.user_direct_call);

        tvId = findViewById(R.id.id);
        tvMode = findViewById(R.id.mode);
        tvName = findViewById(R.id.name);
        tvPhone = findViewById(R.id.phone);
        tvAddress = findViewById(R.id.address);
        tvEmail = findViewById(R.id.email);
        tvArea = findViewById(R.id.area);

        tvUsername = findViewById(R.id.username);
        tvPassword = findViewById(R.id.password);

        tvSpeed = findViewById(R.id.speed);
        tvFee = findViewById(R.id.fee);
        tvPaymentMethod = findViewById(R.id.payment_method);

        radioGroup2 = findViewById(R.id.radioGroup2);
        tvRegDate = findViewById(R.id.reg_date);
        tvActiveDate = findViewById(R.id.active_date);
        tvInactiveDate = findViewById(R.id.inactive_date);

        progressDialog = new ProgressDialog(this);

         /*
        Instance create for client
         */
        client_id = getIntent().getStringExtra("id");

        if (isNetworkConnected())
        {
            details_load(client_id );
            load_payment_details(client_id);

        }else{

            swipeRefreshLayout.setRefreshing(false);
        }

        user_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_VIEW);
                callIntent.setData(Uri.parse("tel:+88"+phone));
                startActivity(callIntent);
            }
        });

        //reload or refresh posts
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isNetworkConnected()){

                    Toast.makeText(ClientDetails.this,"Please!! Check internet connection.",Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);

                }
                else{
                    details_load(client_id);
                    load_payment_details(client_id);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });


        /*
        id initialize here
         */
        //assign all objects to avoid nullPointerException
        transactionArrayList = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(this,transactionArrayList);

        recyclerView = findViewById(R.id.recyclerViewTnxRow);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        //now set adapter to recyclerView
        recyclerView.setAdapter(transactionAdapter);


        btnDetailsEdit = findViewById(R.id.btnEdit);
        btnDetailsEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(ClientDetails.this,ClientDetailsEdit.class);
                i.putExtra("id", client_id);
                startActivity(i);
            }
        });


        /*
        Transaction submit
         */
        buttonTxnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount = editTextAmount.getText().toString().trim();
                jwt = sharedPreferences.getString("jwt", null);
                user_id = sharedPreferences.getString("userid", null);

                if (jwt == null || user_id == null){
                    finish();
                    startActivity(new Intent(ClientDetails.this, Login.class));

                } else if(radioGroup.getCheckedRadioButtonId() == -1)
                {
                    Snackbar.make(findViewById(android.R.id.content),"Select payment type",Snackbar.LENGTH_LONG).show();

                } else if(radioGroup2.getCheckedRadioButtonId() == -1){
                     Snackbar.make(findViewById(android.R.id.content),"Select payment method",Snackbar.LENGTH_LONG).show();

                 } else if(amount.isEmpty())
                {
                    Snackbar.make(findViewById(android.R.id.content),"Write an amount",Snackbar.LENGTH_LONG).show();

                }else if(!isNetworkConnected()){
                    Snackbar.make(findViewById(android.R.id.content),"Please!! Check Internet Connection or Try again later.",Snackbar.LENGTH_LONG).show();

                } else {

                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    int selectedMethod = radioGroup2.getCheckedRadioButtonId();
                    RadioButton radioButton = findViewById(selectedId);
                    RadioButton radioButton2 = findViewById(selectedMethod);
                    payment_type = radioButton.getText().toString();
                    payment_method = radioButton2.getText().toString().trim();

                    txn_confirm_diaglog();

                }
            }
        });

        //Make inform
        btnSmsSend = findViewById(R.id.btnInformSend);
        editTextInformSms = findViewById(R.id.edInformSms);

        btnSmsSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                informMessage = editTextInformSms.getText().toString().trim();
                jwt = sharedPreferences.getString("jwt", null);

                if (jwt == null){
                    finish();
                    startActivity(new Intent(ClientDetails.this, Login.class));

                } else if(informMessage.isEmpty())
                {
                    Snackbar.make(findViewById(android.R.id.content),"Write a message.",Snackbar.LENGTH_LONG).show();

                }else if(!isNetworkConnected()) {
                    Snackbar.make(findViewById(android.R.id.content),"Please!! Check Internet Connection or Try again later.",Snackbar.LENGTH_LONG).show();

                } else{
                    inform_confirm_dialog();
                }
            }
        });

        //Sms history
        btnSmsHistory = findViewById(R.id.btnSmsHistory);
        btnSmsHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
            }
        });

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

    //Internet connection check
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }


    public void details_load(String got_id)
    {
        swipeRefreshLayout.setRefreshing(true);
        progressDialog.showDialog();
        String url = getString(R.string.base_url)+getString(R.string.client_details)+"?id="+got_id;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                swipeRefreshLayout.setRefreshing(false);
                progressDialog.hideDialog();

                try{

                    JSONObject jsonObject = new JSONObject(response);

                    boolean m = jsonObject.has("message");
                    if (m)
                    {
                        String message = jsonObject.getString("message");
                        Toast.makeText(ClientDetails.this,message,Toast.LENGTH_LONG).show();

                    }else
                    {
                        JSONArray jsonArray = jsonObject.getJSONArray("client_details");

                        for (int i=0; i<=jsonArray.length(); i++)
                        {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            //Set data on string variable
                            phone = jsonObject1.getString("phone");
                            name = jsonObject1.getString("name");

                            /*
                            Set text on Text View
                             */
                            tvId.setText(jsonObject1.getString("id"));

                            if (jsonObject1.getString("mode").equals("Active")){
                                tvMode.setText(jsonObject1.getString("mode"));
                                tvMode.setTextColor(Color.GREEN);

                            }else{
                                tvMode.setText(jsonObject1.getString("mode"));
                                tvMode.setTextColor(Color.RED);
                            }


                            tvName.setText(name);
                            tvPhone.setText(phone);
                            tvAddress.setText(jsonObject1.getString("address"));
                            tvEmail.setText(jsonObject1.getString("email"));
                            tvArea.setText(jsonObject1.getString("area"));

                            tvUsername.setText(jsonObject1.getString("username"));
                            tvPassword.setText(jsonObject1.getString("password"));

                            tvSpeed.setText(jsonObject1.getString("speed"));
                            tvFee.setText(jsonObject1.getString("fee"));
                            tvPaymentMethod.setText(jsonObject1.getString("payment_method"));

                            tvRegDate.setText(jsonObject1.getString("reg_date"));
                            tvActiveDate.setText(jsonObject1.getString("active_date"));
                            tvInactiveDate.setText(jsonObject1.getString("inactive_date"));

                        }

                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
                progressDialog.hideDialog();
                Toast.makeText(ClientDetails.this,error.toString(),Toast.LENGTH_LONG).show();
                finish();
            }
        });
        MySingleton.getInstance().addToRequestQueue(stringRequest);

    }

    private void load_payment_details(String got_id) {

        String url = getString(R.string.base_url)+getString(R.string.payment_details)+"?id="+got_id;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{

                    transactionArrayList.clear();
                    JSONObject jsonObject = new JSONObject(response);

                    boolean m = jsonObject.has("message");
                    if (m)
                    {
                        String message = jsonObject.getString("message");
                        Toast.makeText(ClientDetails.this,message,Toast.LENGTH_LONG).show();

                    }else
                    {
                        JSONArray jsonArray = jsonObject.getJSONArray("txn_details");

                        for (int i=0; i<=jsonArray.length(); i++)
                        {
                            Transaction transaction = new Transaction();

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            transaction.setDate(jsonObject1.getString("date"));
                            transaction.setTxn_id(jsonObject1.getString("txn_id"));
                            transaction.setCredit(jsonObject1.getString("amount"));
                            transaction.setDetails(jsonObject1.getString("details"));

                            transactionArrayList.add(transaction);

                            transactionAdapter.notifyDataSetChanged();

                        }

                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(ClientDetails.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance().addToRequestQueue(stringRequest);
    }

    public void make_txn()
    {
        progressDialog.showDialog();

        String url = getString(R.string.base_url)+getString(R.string.client_txn);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.hideDialog();

                try{

                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("200")){
                        Toast.makeText(ClientDetails.this, message,Toast.LENGTH_LONG).show();
                        finish();

                    }else if(status.equals("401")){

                        warningShow(message);

                    }else{
                        Toast.makeText(ClientDetails.this, message,Toast.LENGTH_LONG).show();
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.hideDialog();
                Toast.makeText(ClientDetails.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                map.put("jwt", jwt);
                map.put("id", client_id);
                map.put("name", name);
                map.put("type", payment_type);
                map.put("method", payment_method);
                map.put("userid", user_id);
                map.put("amount", amount);
                map.put("details", name+", "+client_id+", "+payment_type);
                return map;

            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance().addToRequestQueue(stringRequest);
    }


    public void informSmsSend()
    {
        progressDialog.showDialog();

        String url = getString(R.string.base_url)+getString(R.string.idwise_sms_service);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.hideDialog();

                try{

                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("200")){
                        Toast.makeText(ClientDetails.this, message,Toast.LENGTH_LONG).show();
                        finish();

                    }else if(status.equals("401")){

                        warningShow(message);

                    }else{
                        Toast.makeText(ClientDetails.this, message,Toast.LENGTH_LONG).show();
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                Toast.makeText(ClientDetails.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                map.put("jwt", jwt);
                map.put("message", informMessage);
                map.put("phone", phone);
                map.put("client_id", client_id);

                return map;

            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance().addToRequestQueue(stringRequest);
    }

    public void txn_confirm_diaglog(){
        AlertDialog.Builder aleart1 = new AlertDialog.Builder(this);
        aleart1.setCancelable(false);
        aleart1.setTitle("Please Confirm your transaction!!");
        aleart1.setMessage("Your payment submit to \n"+"ID: "+client_id+"\n"+"Name: "+name+"\n"+"Type: "+payment_type+"\n"+"Amount: "+amount);
        aleart1.setIcon(R.drawable.warning_icon);

        aleart1.setPositiveButton("Ok, Sure", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                make_txn();
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

    public void inform_confirm_dialog(){
        AlertDialog.Builder aleart1 = new AlertDialog.Builder(this);
        aleart1.setCancelable(false);
        aleart1.setTitle("Please Confirm your message!!");
        aleart1.setMessage("This message will be send to "+name);
        aleart1.setIcon(R.drawable.warning_icon);

        aleart1.setPositiveButton("Ok, Sure", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                informSmsSend();
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
                startActivity(new Intent(ClientDetails.this, Login.class));

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

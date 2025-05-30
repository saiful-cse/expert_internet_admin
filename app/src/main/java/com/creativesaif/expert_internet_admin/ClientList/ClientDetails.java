package com.creativesaif.expert_internet_admin.ClientList;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creativesaif.expert_internet_admin.Dashboard.Dashboard;
import com.creativesaif.expert_internet_admin.MainActivity;
import com.creativesaif.expert_internet_admin.MySingleton;
import com.creativesaif.expert_internet_admin.NewsFeed.News;
import com.creativesaif.expert_internet_admin.NewsFeed.NewsAdapter;
import com.creativesaif.expert_internet_admin.NewsFeed.NewsAdd;
import com.creativesaif.expert_internet_admin.NewsFeed.NewsDetails;
import com.creativesaif.expert_internet_admin.ProgressDialog;
import com.creativesaif.expert_internet_admin.R;
import com.creativesaif.expert_internet_admin.TransactionList.Transaction;
import com.creativesaif.expert_internet_admin.TransactionList.TransactionAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClientDetails extends AppCompatActivity {

    Button btnDetailsEdit;
    Client client;
    private TextView tvWarning, tvId, tvName, tvPhone, tvAddress, tvEmail,tvIntConnType, tvWanIp, tvSubnet, tvGateway, tvdns1, tvdns2, tvOnuMac,
    tvSpeed, tvFee, tvBillType, tvRegDate, tvActiveDate, tvInactiveDate;

    CardView cardViewAlert;

    String got_id;
    LinearLayout linearLayout, linearLayoutMain;

    /*
    Get text from server. store on string;
     */
    String mode, id, name, phone, address, email, int_type, wan_ip, subnet, gateway, dns1, dns2, onu_mac,
    speed, fee, bill_type;

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
    private boolean isLoading = true;
    private EditText editTextAmount, editTextDetails;
    Button buttonTxnSubmit;
    String payment_type, amount, details, client_name;
    RadioGroup radioGroup;

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
        editTextDetails = findViewById(R.id.edDetails);
        buttonTxnSubmit = findViewById(R.id.txn_submit);
        radioGroup = findViewById(R.id.radioGroup);
        /*
        ID initialize
         */

        linearLayout  = findViewById(R.id.progress_layout);
        linearLayoutMain = findViewById(R.id.mainLayout);
        cardViewAlert = findViewById(R.id.cardViewAlert);
        tvWarning = findViewById(R.id.warning_viw);
        tvId = findViewById(R.id.id);
        tvName = findViewById(R.id.name);
        tvPhone = findViewById(R.id.phone);
        tvAddress = findViewById(R.id.address);
        tvEmail = findViewById(R.id.email);

        tvIntConnType = findViewById(R.id.int_type);
        tvWanIp = findViewById(R.id.wan_ip);
        tvSubnet = findViewById(R.id.subnet);
        tvGateway = findViewById(R.id.gateway);
        tvdns1 = findViewById(R.id.dns1);
        tvdns2 = findViewById(R.id.dns2);
        tvOnuMac = findViewById(R.id.onu_mac);

        tvSpeed = findViewById(R.id.speed);
        tvFee = findViewById(R.id.fee);
        tvBillType = findViewById(R.id.bill_type);
        tvRegDate = findViewById(R.id.reg_date);
        tvActiveDate = findViewById(R.id.active_date);
        tvInactiveDate = findViewById(R.id.inactive_date);

        progressDialog = new ProgressDialog(this);

         /*
        Instance create for client
         */
        client = getIntent().getExtras().getParcelable("client");
        got_id = client.getId();

        if (isNetworkConnected())
        {
            details_load(got_id );
            load_payment_details(got_id);

        }else{

            tvWarning.setVisibility(View.VISIBLE);
            linearLayoutMain.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }


        //reload or refresh posts
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isNetworkConnected()){

                    Toast.makeText(ClientDetails.this,"Please!! Check internet connection.",Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);

                }else if(!isLoading){
                    Toast.makeText(ClientDetails.this,"One request is being process, Try again later.",Toast.LENGTH_SHORT).show();
                }
                else{
                    details_load(got_id);
                    load_payment_details(got_id);
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
                i.putExtra("mode",mode);
                i.putExtra("id",id);
                i.putExtra("name",name);
                i.putExtra("phone",phone);
                i.putExtra("address",address);
                i.putExtra("email",email);

                i.putExtra("int_type",int_type);
                i.putExtra("wan_ip",wan_ip);
                i.putExtra("subnet",subnet);
                i.putExtra("gateway",gateway);
                i.putExtra("dns1",dns1);
                i.putExtra("dns2",dns2);
                i.putExtra("onu_mac",onu_mac);

                i.putExtra("speed",speed);
                i.putExtra("fee",fee);
                i.putExtra("bill_type",bill_type);

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
                details = editTextDetails.getText().toString().trim();

                if (!isLoading)
                {
                    Snackbar.make(findViewById(android.R.id.content),"One request is being process, Try again later.",Snackbar.LENGTH_LONG).show();

                }else if(!isNetworkConnected())
                {
                    Snackbar.make(findViewById(android.R.id.content),"Please!! Check Internet Connection or Try again later.",Snackbar.LENGTH_LONG).show();

                }else if(radioGroup.getCheckedRadioButtonId() == -1)
                {
                    Snackbar.make(findViewById(android.R.id.content),"Select payment type",Snackbar.LENGTH_LONG).show();

                }
                else if(amount.isEmpty())
                {
                    Snackbar.make(findViewById(android.R.id.content),"Write an amount",Snackbar.LENGTH_LONG).show();
                }else if(details.isEmpty())
                {
                    Snackbar.make(findViewById(android.R.id.content),"Write a details",Snackbar.LENGTH_LONG).show();
                }else
                {
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    RadioButton radioButton = findViewById(selectedId);
                    payment_type = radioButton.getText().toString();

                    txn_confirm_diaglog();
                    //Toast.makeText(ClientDetails.this,payment_type,Toast.LENGTH_LONG).show();
                }
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

                //Toast.makeText(ClientDetails.this,response,Toast.LENGTH_SHORT).show();

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

                            String isAlert = jsonObject1.getString("alert");

                            if (isAlert.equals("1"))
                            {
                                cardViewAlert.setVisibility(View.VISIBLE);
                            }

                            /*
                            getting text from json array and store to string
                             */

                            mode = jsonObject1.getString("mode");
                            id = jsonObject1.getString("id");
                            name = jsonObject1.getString("name");
                            client_name = jsonObject1.getString("name");
                            phone = jsonObject1.getString("phone");
                            address = jsonObject1.getString("address");
                            email = jsonObject1.getString("email");

                            int_type = jsonObject1.getString("int_conn_type");
                            wan_ip = jsonObject1.getString("wan_ip");
                            subnet = jsonObject1.getString("subnet");
                            gateway = jsonObject1.getString("default_gateway");
                            dns1 = jsonObject1.getString("dns1");
                            dns2 = jsonObject1.getString("dns2");
                            onu_mac = jsonObject1.getString("onu_mac");

                            speed = jsonObject1.getString("speed");
                            fee = jsonObject1.getString("fee");
                            bill_type = jsonObject1.getString("bill_type");

                            /*
                            Set text on Text View
                             */
                            tvId.setText(id);
                            tvName.setText(name);
                            tvPhone.setText(phone);
                            tvAddress.setText(address);
                            tvEmail.setText(email);

                            tvIntConnType.setText(int_type);
                            tvWanIp.setText(wan_ip);
                            tvSubnet.setText(subnet);
                            tvGateway.setText(gateway);
                            tvdns1.setText(dns1);
                            tvdns2.setText(dns2);
                            tvOnuMac.setText(onu_mac);

                            tvSpeed.setText(speed);
                            tvFee.setText(fee);
                            tvBillType.setText(bill_type);
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

                //Toast.makeText(ClientDetails.this,response,Toast.LENGTH_SHORT).show();

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
        isLoading = false;
        String url = getString(R.string.base_url)+getString(R.string.client_txn);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Toast.makeText(MainActivity.this,response,Toast.LENGTH_SHORT).show();

                isLoading = true;
                progressDialog.hideDialog();

                try{

                    JSONObject jsonObject = new JSONObject(response);

                    boolean m = jsonObject.has("message");
                    if (m)
                    {
                        String message = jsonObject.getString("message");
                        editTextAmount.setText(null);
                        editTextDetails.setText(null);
                       // Toast.makeText(ClientDetails.this,message,Toast.LENGTH_SHORT).show();
                        activity_refresh(message);
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isLoading = true;
                progressDialog.hideDialog();
                Toast.makeText(ClientDetails.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();


                map.put("id", got_id);
                map.put("name", client_name);
                map.put("type", payment_type);
                map.put("amount", amount);
                map.put("details", details);
                return map;

            }
        };
        MySingleton.getInstance().addToRequestQueue(stringRequest);
    }

    public void txn_confirm_diaglog(){
        AlertDialog.Builder aleart1 = new AlertDialog.Builder(this);
        aleart1.setCancelable(false);
        aleart1.setTitle("Please Confirm your transaction!!");
        aleart1.setMessage("Your payment submit to \n"+"ID: "+got_id+"\n"+"Name: "+client_name+"\n"+"Type: "+payment_type+"\n"+"Amount: "+amount+"\n"+"Details: "+details);
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

    public void activity_refresh(String message){
        AlertDialog.Builder aleart1 = new AlertDialog.Builder(this);
        aleart1.setCancelable(false);
        aleart1.setMessage(message);
        aleart1.setIcon(R.drawable.done);

        aleart1.setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                details_load(got_id);
                load_payment_details(got_id);
            }
        });
        AlertDialog dlg = aleart1.create();
        dlg.show();
    }

}

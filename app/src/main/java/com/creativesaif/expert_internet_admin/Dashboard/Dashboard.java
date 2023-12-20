package com.creativesaif.expert_internet_admin.Dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creativesaif.expert_internet_admin.ClientList.ClientDetails;
import com.creativesaif.expert_internet_admin.DeviceUrl.DeviceUrl;
import com.creativesaif.expert_internet_admin.Login;
import com.creativesaif.expert_internet_admin.MySingleton;
import com.creativesaif.expert_internet_admin.ProgressDialog;
import com.creativesaif.expert_internet_admin.R;
import com.creativesaif.expert_internet_admin.URL_config;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Dashboard extends AppCompatActivity {

    TextView textViewTotalExpiredClient, textViewEnableClient, textViewDisableClient,
            textViewMonthCredit, textViewMonthDebit, tvMonthServiceFee, tvMonthBill, textViewMonthProfit;
    String jwt, totalExpiredCLient, activeClient, inactiveClient, monthCredit, monthDebit;
    ProgressDialog progressDialog;

    ArrayList noOfClient;
    ArrayList month;

    BarChart chart;

    BarDataSet bardataset;
    SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textViewTotalExpiredClient = findViewById(R.id.dashboard_total_expired_client);
        textViewEnableClient = findViewById(R.id.dashboard_active);
        textViewDisableClient = findViewById(R.id.dashboard_inactive);
        textViewMonthCredit = findViewById(R.id.dashboard_mon_credit);
        textViewMonthDebit = findViewById(R.id.dashboard_mon_debit);
        tvMonthServiceFee = findViewById(R.id.dashboard_mon_service);
        tvMonthBill = findViewById(R.id.dashboard_mon_bill);
        textViewMonthProfit = findViewById(R.id.dashboard_profit);
        progressDialog = new ProgressDialog(this);

        chart = findViewById(R.id.barchart);
        noOfClient = new ArrayList();
        month = new ArrayList();
        preferences = getApplicationContext().getSharedPreferences("users", MODE_PRIVATE);
        jwt = preferences.getString("jwt", null);

        if(jwt == null){
            finish();
            startActivity(new Intent(Dashboard.this, Login.class));
        }
        else if (isNetworkConnected()){

            data_load();

        }else{
            Snackbar.make(findViewById(android.R.id.content),"Please!! Check internet connection.",Snackbar.LENGTH_LONG).show();
        }

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

    public void data_load()
    {
        progressDialog.showDialog();
        String url = URL_config.BASE_URL+URL_config.DASHBOARD_READ;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                progressDialog.hideDialog();

                try{

                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equals("401")){
                        Toast.makeText(Dashboard.this,jsonObject.getString("status"),Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Dashboard.this, Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }else{

                        totalExpiredCLient = jsonObject.getString("total_expired_client");
                    activeClient = jsonObject.getString("total_enabled_client");
                    inactiveClient = jsonObject.getString("total_disabled_client");
                    monthCredit = jsonObject.getString("current_month_total_credit");
                    monthDebit = jsonObject.getString("current_month_total_debit");

                    double monthlyProfit = Double.parseDouble(monthCredit) - Double.parseDouble(monthDebit);

                    textViewTotalExpiredClient.setText("Total Expired Client\n"+totalExpiredCLient);
                    textViewEnableClient.setText("Enabled Client\n"+activeClient);
                    textViewDisableClient.setText("Disabled Client\n"+inactiveClient);

                    textViewMonthCredit.setText("This Month Credit\n"+monthCredit);
                    textViewMonthDebit.setText("This Month Debit\n"+monthDebit);

                    tvMonthServiceFee.setText("This Month Service fee\n"+jsonObject.getString("current_month_total_service"));
                    tvMonthBill.setText("This Month Bill\n"+jsonObject.getString("current_month_total_bill"));

                    textViewMonthProfit.setText("Monthly Profit\n"+monthlyProfit);

                    //Chart data
                    JSONArray jsonArray = jsonObject.getJSONArray("monthly_client_count");
                    for (int i = 0; i < jsonArray.length(); i++){

                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                        noOfClient.add(new BarEntry(Float.parseFloat(jsonObject1.getString("total")),i));
                        month.add(jsonObject1.getString("month"));
                    }

                    bardataset = new BarDataSet(noOfClient, "No Of Client");
                    chart.animateY(2000);
                    BarData data = new BarData(month, bardataset);
                    bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
                    chart.setData(data);

                    }


                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                Toast.makeText(Dashboard.this,error.toString(),Toast.LENGTH_LONG).show();
                finish();
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                map.put("jwt", jwt);
                return map;

            }
        };
        MySingleton.getInstance().addToRequestQueue(stringRequest);

    }

}


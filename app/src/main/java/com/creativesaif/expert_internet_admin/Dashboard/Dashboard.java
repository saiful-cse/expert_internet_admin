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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creativesaif.expert_internet_admin.ClientList.ClientDetails;
import com.creativesaif.expert_internet_admin.Login;
import com.creativesaif.expert_internet_admin.MySingleton;
import com.creativesaif.expert_internet_admin.ProgressDialog;
import com.creativesaif.expert_internet_admin.R;
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

    TextView textViewActive, textViewInactive, textViewMonthCredit, textViewMonthDebit,
            textViewOverCredit, textViewOverDebit, textViewTotalInvest,
            textViewSaifuPercent, textViewMisbaPercent, textViewSaifulProfit, textViewMisbaProfit;
    String jwt, activeClient, inactiveClient, monthCredit, monthDebit, overCredit, overDebit;
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

        textViewActive = findViewById(R.id.dashboard_active);
        textViewInactive = findViewById(R.id.dashboard_inactive);
        textViewMonthCredit = findViewById(R.id.dashboard_mon_credit);
        textViewMonthDebit = findViewById(R.id.dashboard_mon_debit);
        textViewOverCredit = findViewById(R.id.dashboard_over_credit);
        textViewOverDebit = findViewById(R.id.dashboard_over_debit);
        textViewTotalInvest = findViewById(R.id.dashboard_total_invest);
        textViewSaifuPercent = findViewById(R.id.dashboard_saifulpercent);
        textViewMisbaPercent = findViewById(R.id.dashboard_misbapercent);
        textViewSaifulProfit = findViewById(R.id.dashboard_saifulprofit);
        textViewMisbaProfit = findViewById(R.id.dashboard_misbaprofit);
        progressDialog = new ProgressDialog(this);

        chart = findViewById(R.id.barchart);

        noOfClient = new ArrayList();
        month = new ArrayList();
        preferences = getApplicationContext().getSharedPreferences("users", MODE_PRIVATE);

        jwt = preferences.getString("jwt", null);

        if (jwt == null){
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
        String url = getString(R.string.base_url)+getString(R.string.dashboard_data);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                progressDialog.hideDialog();

                try{

                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equals("401")){
                        finish();
                        startActivity(new Intent(Dashboard.this, Login.class));

                    }else{

                    activeClient = jsonObject.getString("total_enabled_client");
                    inactiveClient = jsonObject.getString("total_disabled_client");
                    monthCredit = jsonObject.getString("current_month_total_credit");
                    monthDebit = jsonObject.getString("current_month_total_debit");
                    overCredit = jsonObject.getString("overall_credit");
                    overDebit = jsonObject.getString("overall_debit");

                    double saifulInvest = Double.parseDouble(jsonObject.getString("saifulinvest"));
                    double misbaInvest = Double.parseDouble(jsonObject.getString("misbainvest"));
                    double totalInvest = Double.parseDouble(jsonObject.getString("saifulinvest")) + Double.parseDouble(jsonObject.getString("misbainvest"));
                    double saifulPercent = (saifulInvest/totalInvest)*100;
                    double misbaPercent = (misbaInvest/totalInvest)*100;

                    double monthlyProfit = Double.parseDouble(monthCredit) - Double.parseDouble(monthDebit);
//                    double saifulProfit = (saifulPercent/100)*monthlyProfit;
//                    double misbaProfit = (misbaPercent/100)*monthlyProfit;
//
                    double saifulProfit = monthlyProfit/2;
                    double misbaProfit = monthlyProfit/2;


                    textViewActive.setText("Enabled Client\n"+activeClient);
                    textViewInactive.setText("Disabled Client\n"+inactiveClient);

                    textViewMonthCredit.setText("This Month Credit\n"+monthCredit);
                    textViewMonthDebit.setText("This Month Debit\n"+monthDebit);

                    textViewOverCredit.setText("Overall Credit\n"+overCredit);
                    textViewOverDebit.setText("Overall Debit\n"+overDebit);

                    textViewTotalInvest.setText("Total Invest\n"+String.format("%.2f", new BigDecimal(totalInvest)));

                    textViewSaifuPercent.setText("Saiful Invest\n"+saifulInvest+" TK, "+String.format("%.2f", new BigDecimal(saifulPercent))+"%");
                    textViewMisbaPercent.setText("Shamim Invest\n"+misbaInvest+" TK, "+String.format("%.2f", new BigDecimal(misbaPercent))+"%");

                    textViewSaifulProfit.setText("Saiful Profit\n"+String.format("%.2f", new BigDecimal(saifulProfit)) +" TK");
                    textViewMisbaProfit.setText("Shamim Profit\n"+String.format("%.2f", new BigDecimal(misbaProfit)) +" TK");

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


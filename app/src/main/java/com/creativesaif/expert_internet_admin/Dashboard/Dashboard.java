package com.creativesaif.expert_internet_admin.Dashboard;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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

import java.util.ArrayList;

public class Dashboard extends AppCompatActivity {

    TextView textViewActive, textViewInactive, textViewMonthCredit, textViewMonthDebit, textViewOverCredit, textViewOverDebit;
    String activeClient, inactiveClient, monthCredit, monthDebit, overCredit, overDebit;
    private boolean isLoading = true;
    ProgressDialog progressDialog;

    ArrayList noOfClient;
    ArrayList month;

    BarChart chart;

    BarDataSet bardataset;

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
        progressDialog = new ProgressDialog(this);

        chart = findViewById(R.id.barchart);

        noOfClient = new ArrayList();
        month = new ArrayList();

        if (isLoading && isNetworkConnected()){

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
        isLoading = false;
        String url = getString(R.string.base_url)+getString(R.string.dashboard_data);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.hideDialog();

                //Toast.makeText(Dashboard.this,response,Toast.LENGTH_SHORT).show();

                try{

                    JSONObject jsonObject = new JSONObject(response);
                    activeClient = jsonObject.getString("total_active_client");
                    inactiveClient = jsonObject.getString("total_inactive_client");
                    monthCredit = jsonObject.getString("current_month_total_credit");
                    monthDebit = jsonObject.getString("current_month_total_debit");
                    overCredit = jsonObject.getString("overall_credit");
                    overDebit = jsonObject.getString("overall_debit");

                    textViewActive.setText(activeClient);
                    textViewInactive.setText(inactiveClient);

                    textViewMonthCredit.setText(monthCredit);
                    textViewMonthDebit.setText(monthDebit);

                    textViewOverCredit.setText(overCredit);
                    textViewOverDebit.setText(overDebit);

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
        });
        MySingleton.getInstance().addToRequestQueue(stringRequest);

    }

}


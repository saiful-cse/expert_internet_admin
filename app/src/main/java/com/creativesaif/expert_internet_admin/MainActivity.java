package com.creativesaif.expert_internet_admin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.creativesaif.expert_internet_admin.ClientList.ClientList;
import com.creativesaif.expert_internet_admin.ClientList.ClientReg;
import com.creativesaif.expert_internet_admin.Dashboard.Dashboard;

import com.creativesaif.expert_internet_admin.Salary.SalaryList;
import com.creativesaif.expert_internet_admin.Sms.SmsCreate;

import com.creativesaif.expert_internet_admin.Search.Search_Page;
import com.creativesaif.expert_internet_admin.Sms.SmsHistory;
import com.creativesaif.expert_internet_admin.TransactionList.TransactionList;
import com.creativesaif.expert_internet_admin.Webview.Webviewpage;

import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //refresh posts
    SwipeRefreshLayout swipeRefreshLayout;
    SharedPreferences sharedPreferences;

    private WebView webview;
    private static ProgressBar progressBar;
    private String url;
    private LinearLayout linearLayoutError;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        linearLayoutError = findViewById(R.id.connection_error_layout);
        progressBar = findViewById(R.id.progressbar);
        swipeRefreshLayout = findViewById(R.id.viewRefresh);
        url = getString(R.string.base_url)+"dashboard/view.php";

        webview = findViewById(R.id.webView);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new myWebClient());

        if (!isNetworkConnected()){
            progressBar.setVisibility(View.GONE);
            linearLayoutError.setVisibility(View.VISIBLE);
        }else{

            webview.loadUrl(url);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webview.loadUrl(url);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        /*
        id initialize here
         */
        sharedPreferences = getApplicationContext().getSharedPreferences("users", MODE_PRIVATE);

    }


    public static class myWebClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            progressBar.setVisibility(View.VISIBLE);
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }
    }

    //Internet connection check
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            MainActivity.super.onBackPressed();
                            //sharedPreferences.edit().clear().apply();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        String admin_id = sharedPreferences.getString("admin_id", null);

        if (id == R.id.nav_dashboard) {

            if(Objects.equals(sharedPreferences.getString("dashboard", null), "1")){
                startActivity(new Intent(MainActivity.this, Dashboard.class));
            }

        }else if (id == R.id.nav_client_reg) {

            if(Objects.equals(sharedPreferences.getString("client_add", null), "1")){
                startActivity(new Intent(MainActivity.this, ClientReg.class));
            }

        } else if (id == R.id.nav_clientlist) {

            startActivity(new Intent(MainActivity.this, ClientList.class));

        } else if (id == R.id.nav_search) {

            startActivity(new Intent(MainActivity.this, Search_Page.class));

        } else if (id == R.id.nav_notice) {

            if (Objects.equals(sharedPreferences.getString("sms", null), "1")){
                startActivity(new Intent(MainActivity.this, SmsCreate.class));
            }

        } else if (id == R.id.nav_txnlist) {

            startActivity(new Intent(MainActivity.this, TransactionList.class));
        }else if (id == R.id.nav_smsHistory) {

            startActivity(new Intent(MainActivity.this, SmsHistory.class));

        }else if (id == R.id.nav_salarylist) {

            startActivity(new Intent(MainActivity.this, SalaryList.class));

        }
        else if (id == R.id.nav_upstrimtxn) {

            if (Objects.equals(sharedPreferences.getString("upstream_bill", null), "1")){
                Intent intent = new Intent(MainActivity.this, Webviewpage.class);
                intent.putExtra("url", getString(R.string.base_url)+"txn/upstream_bill_list.php?admin_id="+sharedPreferences.getString("admin_id", null)+"&jwt="+sharedPreferences.getString("jwt", null));
                startActivity(intent);
            }
        }
        else if (id == R.id.nav_txn_search) {

            Intent intent = new Intent(MainActivity.this, Webviewpage.class);
            intent.putExtra("url", "https://expert-internet.net/paybill/bkash-payment/search_txn/");
            startActivity(intent);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void selectLoginUrlDialog(int port){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(true);
        alert.setTitle("Warning");
        alert.setMessage("Select your network connection");
        alert.setIcon(R.drawable.ic_baseline_warning_24);

        alert.setPositiveButton("Mobile Data/Other WiFi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse("https://103.132.248.128/:"+port+"/action/login.html"));
                startActivity(in);
            }
        });

        alert.setNegativeButton("Self WiFi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (port == 7777){

                    Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse("https://77.77.77.2/action/login.html"));
                    startActivity(in);

                }else if(port == 8888){
                    Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse("https://88.88.88.2/action/login.html"));
                    startActivity(in);

                }else{
                    Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse("https://99.99.99.2/action/login.html"));
                    startActivity(in);
                }

            }
        });
        AlertDialog dlg = alert.create();
        dlg.show();
    }
}

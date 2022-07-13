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
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creativesaif.expert_internet_admin.ClientList.ClientDetails;
import com.creativesaif.expert_internet_admin.ClientList.ClientDetailsEdit;
import com.creativesaif.expert_internet_admin.ClientList.ClientList;
import com.creativesaif.expert_internet_admin.ClientList.ClientReg;
import com.creativesaif.expert_internet_admin.ClientList.ClientRegUpdate;
import com.creativesaif.expert_internet_admin.Dashboard.Dashboard;
import com.creativesaif.expert_internet_admin.Feedback.FeedbackList;

import com.creativesaif.expert_internet_admin.Notice.NoticeCreate;
import com.creativesaif.expert_internet_admin.Notice.NoticeRead;

import com.creativesaif.expert_internet_admin.Search.Search_Page;
import com.creativesaif.expert_internet_admin.Sms.SmsHistory;
import com.creativesaif.expert_internet_admin.TransactionList.TransactionList;
import com.creativesaif.expert_internet_admin.Webview.Webviewpage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
                            sharedPreferences.edit().clear().apply();
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

            assert admin_id != null;
            if(admin_id.equals("9161") || admin_id.equals("8991")) {

                startActivity(new Intent(MainActivity.this, Dashboard.class));

            }else {
                Toast.makeText(getApplicationContext(), "You are not permitted to view", Toast.LENGTH_LONG).show();
            }

        }else if (id == R.id.nav_client_reg) {

            startActivity(new Intent(MainActivity.this, ClientReg.class));

        } else if (id == R.id.nav_clientlist) {

            startActivity(new Intent(MainActivity.this, ClientList.class));

        } else if (id == R.id.nav_search) {

            startActivity(new Intent(MainActivity.this, Search_Page.class));

        } else if (id == R.id.nav_notice) {
            startActivity(new Intent(MainActivity.this, NoticeCreate.class));

        } else if (id == R.id.nav_feedback) {

            startActivity(new Intent(MainActivity.this, FeedbackList.class));

        } else if (id == R.id.nav_txnlist) {

            startActivity(new Intent(MainActivity.this, TransactionList.class));
        }else if (id == R.id.nav_smsHistory) {

            startActivity(new Intent(MainActivity.this, SmsHistory.class));

        }else if (id == R.id.nav_baysoft) {

            Intent intent = new Intent(MainActivity.this, Webviewpage.class);
            intent.putExtra("url", "http://bay.robotispsoft.net/include/login.php");
            startActivity(intent);

        }
        else if (id == R.id.nav_olt7) {

            selectLoginUrlDialog(7777);

        }else if (id == R.id.nav_olt8) {

            selectLoginUrlDialog(8888);

        }else if (id == R.id.nav_olt9) {

            selectLoginUrlDialog(9999);
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

                Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse("https://103.134.39.146:"+port+"/action/login.html"));
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

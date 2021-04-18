package com.creativesaif.expert_internet_admin.Notice;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creativesaif.expert_internet_admin.ClientList.ClientDetailsEdit;
import com.creativesaif.expert_internet_admin.MySingleton;
import com.creativesaif.expert_internet_admin.ProgressDialog;
import com.creativesaif.expert_internet_admin.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoticeCreate extends AppCompatActivity {

    EditText editTextAlertSms , editTextActiveClientMsg, editTextNotice;
    ProgressDialog progressDialog;
    String message, activeClientMessage, notice;
    Button buttonAlertSmsSend, buttonActiveSmsSend, notice_Post;
    TextView textViewTotlaAlertClient, textViewSent, textViewUnSent;
    int countUnsentSms, totalActiveClient;


    /*
    Area load from server
     */
    private RecyclerView mRecyclerView;

    private Button btnSelection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_create);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // ---------- notice ------------
        editTextNotice = findViewById(R.id.edNotice);
        progressDialog = new ProgressDialog(this);
        notice_Post = findViewById(R.id.btnNoticePost);

        notice_Post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notice = editTextNotice.getText().toString().trim();

                if (notice.isEmpty()){
                    Snackbar.make(findViewById(android.R.id.content),"Write a notice",Snackbar.LENGTH_LONG).show();

                }else if(!isNetworkConnected()){
                    Snackbar.make(findViewById(android.R.id.content),"Please check internet connection.",Snackbar.LENGTH_LONG).show();
                }else {
                    notice_create();
                }
            }
        });


        // ----- sms service for alert client -----

        textViewTotlaAlertClient = findViewById(R.id.tvTotalClient);
        textViewSent = findViewById(R.id.tvSent);
        textViewUnSent = findViewById(R.id.tvUnSent);

        editTextAlertSms = findViewById(R.id.edAlertSms);
        buttonAlertSmsSend = findViewById(R.id.btnAlertSmsSend);
        buttonActiveSmsSend = findViewById(R.id.btnActiveSmsSend);

        //Fetching alert client sms status
        getSmsStatus();

        buttonAlertSmsSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (countUnsentSms > 0 ){

                    message = editTextAlertSms.getText().toString().trim();
                    if(message.isEmpty()){
                        Toast.makeText(NoticeCreate.this,"Write SMS",Toast.LENGTH_SHORT).show();

                    }else if(!isNetworkConnected()){
                        Toast.makeText(NoticeCreate.this,"Check Internet Connection.",Toast.LENGTH_SHORT).show();

                    }else{
                        //confirm dialog
                        warning_alert_client_sms();
                    }

                }else{
                    Toast.makeText(NoticeCreate.this,"Nothing unsent alert client.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        editTextActiveClientMsg = findViewById(R.id.edActiveClientSMs);
        buttonActiveSmsSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activeClientMessage = editTextActiveClientMsg.getText().toString().trim();
                if(activeClientMessage.isEmpty()){
                    Toast.makeText(NoticeCreate.this,"Write SMS",Toast.LENGTH_SHORT).show();

                }else if(!isNetworkConnected()){
                    Toast.makeText(NoticeCreate.this,"Check Internet Connection.",Toast.LENGTH_SHORT).show();

                }else{
                    //confirm dialog
                    warning_active_client_sms();
                }

            }
        });


    }

    public void alertSmsSend()
    {
        progressDialog.showDialog();
        String url = getString(R.string.base_url)+getString(R.string.alert_client_sms_service);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.hideDialog();

                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");

                    if (message.equals("200")) {

                        Toast.makeText(NoticeCreate.this, "Message has been delivered.",Toast.LENGTH_LONG).show();
                        finish();

                    }else{
                        Toast.makeText(NoticeCreate.this, message,Toast.LENGTH_LONG).show();
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                Toast.makeText(NoticeCreate.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                map.put("message", message);
                return map;

            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance().addToRequestQueue(stringRequest);
    }

    public void activeClientSmsSend()
    {
        progressDialog.showDialog();
        String url = getString(R.string.base_url)+getString(R.string.active_client_sms_service);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.hideDialog();

                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");

                    if (message.equals("200")) {

                        Toast.makeText(NoticeCreate.this, "Message has been delivered.",Toast.LENGTH_LONG).show();
                        finish();

                    }else{
                        Toast.makeText(NoticeCreate.this, message,Toast.LENGTH_LONG).show();
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                Toast.makeText(NoticeCreate.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                map.put("message", activeClientMessage);
                return map;

            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance().addToRequestQueue(stringRequest);
    }

    //Internet connection check
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public void notice_create()
    {
        progressDialog.showDialog();
        String url = getString(R.string.base_url)+getString(R.string.notice_create);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Toast.makeText(NoticeEdit.this,response,Toast.LENGTH_SHORT).show();

                progressDialog.hideDialog();

                try{

                    JSONObject jsonObject = new JSONObject(response);

                    boolean m = jsonObject.has("message");
                    if (m)
                    {
                        String message = jsonObject.getString("message");
                        Toast.makeText(NoticeCreate.this,message,Toast.LENGTH_SHORT).show();
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
                Toast.makeText(NoticeCreate.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                map.put("notice", notice);
                return map;

            }
        };
        MySingleton.getInstance().addToRequestQueue(stringRequest);
    }


    private void getSmsStatus() {

        progressDialog.showDialog();
        String url = getString(R.string.base_url)+getString(R.string.get_sms_status);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.hideDialog();

                try{

                    JSONObject jsonObject = new JSONObject(response);

                    textViewTotlaAlertClient.setText(jsonObject.getString("total_alert"));
                    textViewSent.setText(jsonObject.getString("sent"));
                    countUnsentSms = Integer.parseInt(jsonObject.getString("unsent"));
                    textViewUnSent.setText(jsonObject.getString("unsent"));
                    totalActiveClient = Integer.parseInt(jsonObject.getString("total_active"));

                    //Toast.makeText(NoticeCreate.this,response,Toast.LENGTH_LONG).show();


                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                Toast.makeText(NoticeCreate.this,error.toString(),Toast.LENGTH_LONG).show();
                finish();
            }
        });
        MySingleton.getInstance().addToRequestQueue(stringRequest);
    }


    public void warning_alert_client_sms(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(false);
        alert.setTitle("Warning!!");
        alert.setMessage("Message will be send to "+countUnsentSms+" alert clients");
        alert.setIcon(R.drawable.warning_icon);

        alert.setPositiveButton("Ok, Sure", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertSmsSend();
            }
        });

        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog dlg = alert.create();
        dlg.show();
    }


    public void warning_active_client_sms(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(false);
        alert.setTitle("Warning!!");
        alert.setMessage("Message will be send to "+totalActiveClient+" active clients");
        alert.setIcon(R.drawable.warning_icon);

        alert.setPositiveButton("Ok, Sure", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                activeClientSmsSend();
            }
        });

        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog dlg = alert.create();
        dlg.show();
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

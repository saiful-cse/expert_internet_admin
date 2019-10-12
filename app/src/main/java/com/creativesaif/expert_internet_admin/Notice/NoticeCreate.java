package com.creativesaif.expert_internet_admin.Notice;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creativesaif.expert_internet_admin.ClientList.Client;
import com.creativesaif.expert_internet_admin.MySingleton;
import com.creativesaif.expert_internet_admin.ProgressDialog;
import com.creativesaif.expert_internet_admin.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NoticeCreate extends AppCompatActivity {

    EditText editTextNotice;
    ProgressDialog progressDialog;
    String notice;
    Button notice_Post;
    TextView textViewTotlaAlertClient, textViewSent, textViewNotSent;

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
        textViewNotSent = findViewById(R.id.tvNotSent);

        getting_alert_client();
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


    private void getting_alert_client() {

        progressDialog.showDialog();
        String url = getString(R.string.base_url)+getString(R.string.getting_alert_client);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Toast.makeText(getContext(),response,Toast.LENGTH_SHORT).show();

                try{

                    progressDialog.hideDialog();
                    JSONObject jsonObject = new JSONObject(response);

                    boolean m = jsonObject.has("message");
                    if (m)
                    {
                        String message = jsonObject.getString("message");
                        Toast.makeText(NoticeCreate.this,message,Toast.LENGTH_LONG).show();

                    }else
                    {

                        textViewTotlaAlertClient.setText(jsonObject.getString("total"));
                        textViewSent.setText(jsonObject.getString("sent"));
                        textViewNotSent.setText(jsonObject.getString("not_sent"));
//                        JSONArray jsonArray = jsonObject.getJSONArray("alert_client");
//
//                        for (int i=0; i<=jsonArray.length(); i++)
//                        {
//                            Client client = new Client();
//
//                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
//
//                            client.setId(jsonObject1.getString("id"));
//                            client.setName(jsonObject1.getString("name"));
//                            client.setPhone(jsonObject1.getString("phone"));
//
//                        }

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

package com.creativesaif.expert_internet_admin.Notice;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creativesaif.expert_internet_admin.MySingleton;
import com.creativesaif.expert_internet_admin.NewsFeed.NewsDetails;
import com.creativesaif.expert_internet_admin.NewsFeed.NewsEdit;
import com.creativesaif.expert_internet_admin.ProgressDialog;
import com.creativesaif.expert_internet_admin.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NoticeEdit extends AppCompatActivity {
    String got_id, notice;
    ProgressDialog progressDialog;

    Button delete, update;

    EditText editTextNotice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       got_id = getIntent().getStringExtra("id");
       progressDialog = new ProgressDialog(this);
       delete = findViewById(R.id.btn_delete);
       update = findViewById(R.id.btn_update);
       editTextNotice = findViewById(R.id.edNotice);

       editTextNotice.setText(getIntent().getStringExtra("notice"));


       delete.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               deleteDialog();
           }
       });

       update.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               notice = editTextNotice.getText().toString().trim();

               if (notice.isEmpty()){
                   Snackbar.make(findViewById(android.R.id.content),"Write a notice",Snackbar.LENGTH_LONG).show();

               }else{

                   notice_update();
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

    public void notice_update()
    {
        progressDialog.showDialog();
        String url = getString(R.string.base_url)+getString(R.string.notice_update);

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
                        Toast.makeText(NoticeEdit.this,message,Toast.LENGTH_SHORT).show();
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
                Toast.makeText(NoticeEdit.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                map.put("id", got_id);
                map.put("notice", notice);
                return map;

            }
        };
        MySingleton.getInstance().addToRequestQueue(stringRequest);
    }


    public void notice_delete(String got_id)
    {
        progressDialog.showDialog();
        String url = getString(R.string.base_url)+getString(R.string.notice_delete)+"?id="+got_id;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(NoticeEdit.this,response,Toast.LENGTH_SHORT).show();

                progressDialog.hideDialog();

                try{

                    JSONObject jsonObject = new JSONObject(response);

                    boolean m = jsonObject.has("message");
                    if (m)
                    {
                        String message = jsonObject.getString("message");
                        Toast.makeText(NoticeEdit.this,message,Toast.LENGTH_SHORT).show();
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
                Toast.makeText(NoticeEdit.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance().addToRequestQueue(stringRequest);
    }

    public void deleteDialog(){
        AlertDialog.Builder aleart1 = new AlertDialog.Builder(this);
        aleart1.setCancelable(true);
        aleart1.setTitle("Warning!!!");
        aleart1.setMessage("Are you sure want to permanently delete this notice?");
        aleart1.setIcon(R.drawable.warning_icon);

        aleart1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                notice_delete(got_id);
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

}

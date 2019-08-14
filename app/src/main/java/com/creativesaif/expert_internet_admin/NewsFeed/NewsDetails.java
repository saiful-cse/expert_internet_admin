package com.creativesaif.expert_internet_admin.NewsFeed;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.creativesaif.expert_internet_admin.MySingleton;
import com.creativesaif.expert_internet_admin.ProgressDialog;
import com.creativesaif.expert_internet_admin.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NewsDetails extends AppCompatActivity {

    ImageView imageView;
    TextView created_at, title, description;
    News news;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        progressDialog = new ProgressDialog(this);

        FloatingActionButton fab = findViewById(R.id.news_edit);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(NewsDetails.this, NewsEdit.class);
                i.putExtra("news",news);
                startActivity(i);
            }
        });

        FloatingActionButton fab2 = findViewById(R.id.news_delete);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isNetworkConnected()){
                    deleteDialog();

                }else{
                    Snackbar.make(findViewById(android.R.id.content),"Please!! Check Internet Connection or Try again later.",Snackbar.LENGTH_LONG).show();
                }
            }
        });


        news = getIntent().getExtras().getParcelable("news");

        imageView = findViewById(R.id.newsImageDetailView);
        created_at = findViewById(R.id.news_detail_created_at);
        title = findViewById(R.id.news_detail_title);
        description = findViewById(R.id.news_detail_descript);

        Glide.with(this)
                .load(getString(R.string.base_url)+getString(R.string.news_image_path)+news.getImage_path())
                .error(R.drawable.ic_menu_gallery)
                .into(imageView);

        created_at.setText(news.getCreated_at());
        title.setText(news.getTitle());
        description.setText(news.getDescription());

    }


    //Internet connection check
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public void deleteDialog(){
        AlertDialog.Builder aleart1 = new AlertDialog.Builder(this);
        aleart1.setCancelable(true);
        aleart1.setTitle("Warning!!!");
        aleart1.setMessage("Are you sure want to permanently delete this news?");
        aleart1.setIcon(R.drawable.warning_icon);

        aleart1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                news_delete();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void news_delete()
    {
        progressDialog.showDialog();
        String url = getString(R.string.base_url)+getString(R.string.news_delete);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Toast.makeText(NewsEdit.this,response,Toast.LENGTH_SHORT).show();

                progressDialog.hideDialog();

                try{

                    JSONObject jsonObject = new JSONObject(response);

                    boolean m = jsonObject.has("message");
                    if (m)
                    {
                        String message = jsonObject.getString("message");
                        Toast.makeText(NewsDetails.this,message,Toast.LENGTH_SHORT).show();
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
                Toast.makeText(NewsDetails.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                map.put("id", news.getId());
                map.put("image", news.getImage_path());
                return map;

            }
        };
        MySingleton.getInstance().addToRequestQueue(stringRequest);
    }

}

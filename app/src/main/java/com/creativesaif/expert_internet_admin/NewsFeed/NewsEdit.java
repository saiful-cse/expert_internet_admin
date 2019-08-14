package com.creativesaif.expert_internet_admin.NewsFeed;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class NewsEdit extends AppCompatActivity {

    ImageView imageView;

    EditText editTextTitle, editTextDescr;
    Button buttonPost;

    Bitmap bitmapOrginalImage;
    Bitmap bitmapImage;

    private boolean isLoading = true;

    ProgressDialog progressDialog;

    String title, description, new_image;

    News news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageView = findViewById(R.id.postPhoto);
        editTextTitle = findViewById(R.id.news_create_title);
        editTextDescr = findViewById(R.id.news_create_descr);
        buttonPost = findViewById(R.id.news_update_btn);
        progressDialog = new ProgressDialog(this);

        news = getIntent().getExtras().getParcelable("news");

        //set content on editext field
        Glide.with(this)
                .load(getString(R.string.base_url)+getString(R.string.news_image_path)+news.getImage_path())
                .placeholder(R.drawable.ic_menu_gallery)
                .into(imageView);

        editTextTitle.setText(news.getTitle());
        editTextDescr.setText(news.getDescription());


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //call to Runtime permission confirmation method
                permissionconfirmation();

                //call to shoImageChoser method
                showImageChooser();
            }
        });

        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = editTextTitle.getText().toString().trim();
                description = editTextDescr.getText().toString().trim();

                if(title.isEmpty()){
                    Snackbar.make(findViewById(android.R.id.content),"Please!! write a title",Snackbar.LENGTH_LONG).show();

                }else if(description.isEmpty()){
                    Snackbar.make(findViewById(android.R.id.content),"Please!! write a description",Snackbar.LENGTH_LONG).show();

                }else if(!isLoading){
                    Snackbar.make(findViewById(android.R.id.content),"One request is being process, Try again later.",Snackbar.LENGTH_LONG).show();

                }else if(isNetworkConnected()){

                    news_update();
                }
                else{
                    Snackbar.make(findViewById(android.R.id.content),"Please!! Check Internet Connection or Try again later.",Snackbar.LENGTH_LONG).show();
                    isLoading = true;
                }
            }
        });

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

    //Runtime permission confirmation
    public void permissionconfirmation(){
        Permissions.check(this/*context*/, Manifest.permission.READ_EXTERNAL_STORAGE, null, new PermissionHandler() {
            @Override
            public void onGranted() {
            }
        });
    }

    //Chosser photo
    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 99);
    }


    //Getting image from gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 99 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();

            try {

                //Getting the Bitmap from Gallery
                bitmapOrginalImage = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                bitmapImage = getResizedBitmap(bitmapOrginalImage, 500);

                //Setting the Bitmap to ImageView
                imageView.setImageBitmap(bitmapImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public Bitmap getResizedBitmap(Bitmap bitmapImage, int maxSize) {
        int width = bitmapImage.getWidth();
        int height = bitmapImage.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(bitmapImage, width, height, true);
    }


    //Bitmap to string convert
    public String getStringImage(Bitmap bitmap){
        ByteArrayOutputStream baos = new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b = baos.toByteArray();

        return Base64.encodeToString(b, Base64.DEFAULT);
    }


    public void news_update()
    {
        progressDialog.showDialog();
        isLoading = false;
        String url = getString(R.string.base_url)+getString(R.string.news_update);

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
                        Toast.makeText(NewsEdit.this,message,Toast.LENGTH_SHORT).show();
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
                Toast.makeText(NewsEdit.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                 //= getStringImage(bitmapImage);

                if (bitmapImage == null){
                    new_image = news.getImage_path();

                }else{
                    new_image = getStringImage(bitmapImage);
                }

                map.put("id", news.getId());
                map.put("title", title);
                map.put("description", description);
                map.put("image", news.getImage_path());
                map.put("new_image", new_image);
                return map;

            }
        };
        MySingleton.getInstance().addToRequestQueue(stringRequest);
    }
}

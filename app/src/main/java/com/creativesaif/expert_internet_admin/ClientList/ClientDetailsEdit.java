package com.creativesaif.expert_internet_admin.ClientList;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creativesaif.expert_internet_admin.MySingleton;
import com.creativesaif.expert_internet_admin.ProgressDialog;
import com.creativesaif.expert_internet_admin.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ClientDetailsEdit extends AppCompatActivity {

    EditText edname, edphone, edaddress, edemail, edint_type, edUsername,
            edPassword, edonu_mac, edspeed, edfee, edbill_type;

    RadioGroup radioGroup;

    String id, name, phone, address, email, int_type, username, password, onu_mac,
            speed, fee, bill_type;

    Button buttonUpdate;

    /*
    Progress dialog
     */
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_details_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*
        Id initialize
         */

        radioGroup = findViewById(R.id.radioGroup);

        edname = findViewById(R.id.edName);
        edphone = findViewById(R.id.edPhone);
        edaddress = findViewById(R.id.edAdress);
        edemail = findViewById(R.id.edEmail);

        edint_type = findViewById(R.id.edInt_type);
        edUsername = findViewById(R.id.edUsername);
        edPassword = findViewById(R.id.edPassword);
        edonu_mac = findViewById(R.id.edOnu_mac);

        edspeed = findViewById(R.id.edSpeed);
        edfee = findViewById(R.id.edFee);
        edbill_type = findViewById(R.id.edBill_type);

        buttonUpdate = findViewById(R.id.update_button);


        progressDialog = new ProgressDialog(this);

        /*
        Set text on edit text field
         */

        String mode = getIntent().getStringExtra("mode");
        //setting radio button status
        if (mode.equals("Active"))
        {
            radioGroup.check(R.id.mode_active);

        }else{

            radioGroup.check(R.id.mode_inactive);
        }

        edname.setText(getIntent().getStringExtra("name"));
        edphone.setText(getIntent().getStringExtra("phone"));
        edaddress.setText(getIntent().getStringExtra("address"));
        edemail.setText(getIntent().getStringExtra("email"));

        edint_type.setText(getIntent().getStringExtra("int_type"));
        edUsername.setText(getIntent().getStringExtra("username"));
        edPassword.setText(getIntent().getStringExtra("password"));
        edonu_mac.setText(getIntent().getStringExtra("onu_mac"));

        edspeed.setText(getIntent().getStringExtra("speed"));
        edfee.setText(getIntent().getStringExtra("fee"));
        edbill_type.setText(getIntent().getStringExtra("bill_type"));

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
        Getting content from edi text field
         */
                id = getIntent().getStringExtra("id");
                name = edname.getText().toString().trim();
                phone = edphone.getText().toString().trim();
                address = edaddress.getText().toString().trim();
                email = edemail.getText().toString().trim();

                int_type = edint_type.getText().toString().trim();
                username = edUsername.getText().toString().trim();
                password = edPassword.getText().toString().trim();
                onu_mac = edonu_mac.getText().toString();

                speed = edspeed.getText().toString().trim();
                fee = edfee.getText().toString().trim();
                bill_type = edbill_type.getText().toString().trim();

                /*
        Data validation
         */
                if(!isNetworkConnected()){
                    Snackbar.make(findViewById(android.R.id.content),"Please check Internet connection",Snackbar.LENGTH_LONG).show();

                } else if (name.isEmpty()){
                    Snackbar.make(findViewById(android.R.id.content),"Write a client name",Snackbar.LENGTH_LONG).show();
                }else if(phone.isEmpty() || phone.length() < 11){
                    Snackbar.make(findViewById(android.R.id.content),"Write a valid phone number",Snackbar.LENGTH_LONG).show();
                }else if(address.isEmpty()){
                    Snackbar.make(findViewById(android.R.id.content),"Write a valid address",Snackbar.LENGTH_LONG).show();
                }else if(email.isEmpty()){
                    Snackbar.make(findViewById(android.R.id.content),"Write a valid email address",Snackbar.LENGTH_LONG).show();

                }else if(int_type.isEmpty()){
                    Snackbar.make(findViewById(android.R.id.content),"Write a Internet connection type",Snackbar.LENGTH_LONG).show();

                }else if(username.isEmpty()){
                    Snackbar.make(findViewById(android.R.id.content),"Write a Username",Snackbar.LENGTH_LONG).show();

                }else if(password.isEmpty()){
                    Snackbar.make(findViewById(android.R.id.content),"Write a Password",Snackbar.LENGTH_LONG).show();

                }else if(onu_mac.isEmpty()){
                    Snackbar.make(findViewById(android.R.id.content),"Write a valid ONU MAC address",Snackbar.LENGTH_LONG).show();

                }else if(speed.isEmpty()){
                    Snackbar.make(findViewById(android.R.id.content),"Write a speed",Snackbar.LENGTH_LONG).show();

                }else if(fee.isEmpty()){
                    Snackbar.make(findViewById(android.R.id.content),"Write a fee",Snackbar.LENGTH_LONG).show();

                }else if(bill_type.isEmpty()){
                    Snackbar.make(findViewById(android.R.id.content),"Write a bill type",Snackbar.LENGTH_LONG).show();

                }else {

                    client_details_upadate();
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
//        switch (item.getItemId()) {
//            case android.R.id.home:
//
//        }

        if (item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void client_details_upadate()
    {
        progressDialog.showDialog();
        String url = getString(R.string.base_url)+getString(R.string.update_details);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Toast.makeText(MainActivity.this,response,Toast.LENGTH_SHORT).show();

                progressDialog.hideDialog();

                try{

                    JSONObject jsonObject = new JSONObject(response);

                    boolean m = jsonObject.has("message");
                    if (m)
                    {
                        String message = jsonObject.getString("message");
                        Toast.makeText(ClientDetailsEdit.this,message,Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ClientDetailsEdit.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                int selectedId = radioGroup.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                RadioButton radioButton = findViewById(selectedId);
                String client_mode = radioButton.getText().toString();

                map.put("id", id);
                map.put("mode", client_mode);
                map.put("name", name);
                map.put("phone", phone);
                map.put("address", address);
                map.put("email", email);

                map.put("int_conn_type", int_type);
                map.put("username", username);
                map.put("password", password);
                map.put("onu_mac", onu_mac);

                map.put("speed", speed);
                map.put("fee", fee);
                map.put("bill_type", bill_type);

                return map;

            }
        };
        MySingleton.getInstance().addToRequestQueue(stringRequest);
    }

}

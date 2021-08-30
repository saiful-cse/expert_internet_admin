package com.creativesaif.expert_internet_admin.ClientList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creativesaif.expert_internet_admin.MySingleton;
import com.creativesaif.expert_internet_admin.ProgressDialog;
import com.creativesaif.expert_internet_admin.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClientDetailsEdit extends AppCompatActivity{

    //Declaring EditText
    private EditText edname, edphone, edaddress, edemail,
            edUsername, edPassword,
            edspeed, edfee;

    private TextView tvArea;

    //Declaring RadioButton
    private RadioGroup radioGroupClientMode, radioGroupPaymentMethod;

    //Declaring String
    private String jwt, client_id, name, phone, address, email, selectedArea, existArea,
            username, password,
            speed, fee;

    //Declaring Button
    Button buttonUpdate;

    //Declaring progress dialog
    private ProgressDialog progressDialog;

    //Declaring spinner
    private Spinner areaSpinner;

    //Declaring area Array List
    private ArrayList<String> areaList;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_details_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        client_id = getIntent().getStringExtra("id");

        preferences = this.getSharedPreferences("data", MODE_PRIVATE);

        /*
        Id initialize
         */
        areaSpinner = findViewById(R.id.areaListSpinner);
        areaList = new ArrayList<>();

        radioGroupClientMode = findViewById(R.id.radioGroupClientMode);

        edname = findViewById(R.id.edName);
        edphone = findViewById(R.id.edPhone);
        edaddress = findViewById(R.id.edAdress);
        edemail = findViewById(R.id.edEmail);
        tvArea = findViewById(R.id.edArea);

        edUsername = findViewById(R.id.edUsername);
        edPassword = findViewById(R.id.edPassword);

        edspeed = findViewById(R.id.edSpeed);
        edfee = findViewById(R.id.edFee);
        radioGroupPaymentMethod = findViewById(R.id.radioGroupPaymentMethod);

        buttonUpdate = findViewById(R.id.update_button);

        details_load(client_id);

        //Spinner item choice and click event
        areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // On selecting a spinner item
                selectedArea = parentView.getItemAtPosition(position).toString();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
        Getting content from edi text field
         */
                jwt = preferences.getString("jwt", null);

                name = edname.getText().toString().trim();
                phone = edphone.getText().toString().trim();
                address = edaddress.getText().toString().trim();
                email = edemail.getText().toString().trim();

                username = edUsername.getText().toString().trim();
                password = edPassword.getText().toString().trim();

                speed = edspeed.getText().toString().trim();
                fee = edfee.getText().toString().trim();

                        /*
                Data validation
                 */
                if (name.isEmpty()){
                    Snackbar.make(findViewById(android.R.id.content),"Write a client name",Snackbar.LENGTH_LONG).show();

                }else if(phone.isEmpty() || phone.length() < 11){
                    Snackbar.make(findViewById(android.R.id.content),"Write a valid phone number",Snackbar.LENGTH_LONG).show();

                }else if(address.isEmpty()){
                    Snackbar.make(findViewById(android.R.id.content),"Write a valid address",Snackbar.LENGTH_LONG).show();

                }else if(email.isEmpty()){
                    Snackbar.make(findViewById(android.R.id.content),"Write a valid email address",Snackbar.LENGTH_LONG).show();

                }else if(username.isEmpty()){
                    Snackbar.make(findViewById(android.R.id.content),"Write a Username",Snackbar.LENGTH_LONG).show();

                }else if(password.isEmpty()){
                    Snackbar.make(findViewById(android.R.id.content),"Write a Password",Snackbar.LENGTH_LONG).show();

                }else if(speed.isEmpty()){
                    Snackbar.make(findViewById(android.R.id.content),"Write a speed",Snackbar.LENGTH_LONG).show();

                }else if(fee.isEmpty()){
                    Snackbar.make(findViewById(android.R.id.content),"Write a fee",Snackbar.LENGTH_LONG).show();

                }else if(!isNetworkConnected()){
                    Snackbar.make(findViewById(android.R.id.content),"Please check Internet connection",Snackbar.LENGTH_LONG).show();

                } else {

                   client_details_update();

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

        if (item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void details_load(String got_id)
    {
        progressDialog.showDialog();
        String url = getString(R.string.base_url)+getString(R.string.client_details)+"?id="+got_id;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.hideDialog();

                try{

                    JSONObject jsonObject = new JSONObject(response);

                    boolean m = jsonObject.has("message");
                    if (m)
                    {
                        String message = jsonObject.getString("message");
                        Toast.makeText(ClientDetailsEdit.this,message,Toast.LENGTH_LONG).show();

                    }else
                    {
                        JSONArray jsonArray = jsonObject.getJSONArray("client_details");

                        for (int i=0; i<=jsonArray.length(); i++)
                        {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            //Set the view
                            if (jsonObject1.getString("mode").equals("Active"))
                            {
                                radioGroupClientMode.check(R.id.mode_active);

                            }else{

                                radioGroupClientMode.check(R.id.mode_inactive);
                            }

                            edname.setText(jsonObject1.getString("name"));
                            edphone.setText(jsonObject1.getString("phone"));
                            edaddress.setText(jsonObject1.getString("address"));
                            edemail.setText(jsonObject1.getString("email"));
                            existArea = jsonObject1.getString("area");
                            tvArea.setText(existArea);

                            edUsername.setText(jsonObject1.getString("username"));
                            edPassword.setText(jsonObject1.getString("password"));

                            edspeed.setText(jsonObject1.getString("speed"));
                            edfee.setText(jsonObject1.getString("fee"));

                            if (jsonObject1.getString("payment_method").equals("Cash"))
                            {
                                radioGroupPaymentMethod.check(R.id.mode_cash);

                            }else{
                                radioGroupPaymentMethod.check(R.id.mode_mobile);
                            }

                            area_load();

                        }

                        //area_load();
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
                finish();
            }
        });
        MySingleton.getInstance().addToRequestQueue(stringRequest);

    }


    //Area load
    public void area_load()
    {
        progressDialog.showDialog();
        String url = getString(R.string.base_url)+getString(R.string.area_load);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.hideDialog();
                try {

                    JSONArray jsonArray = new JSONArray(response);

                    for(int i=0; i<jsonArray.length(); i++) {
                        areaList.add(jsonArray.getString(i));
                    }

                    //Setting adapter to show the items in the spinner
                    areaSpinner.setAdapter(new ArrayAdapter<String>(ClientDetailsEdit.this,
                            android.R.layout.simple_spinner_dropdown_item, areaList));

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                Toast.makeText(ClientDetailsEdit.this,error.toString(),Toast.LENGTH_LONG).show();
                finish();
            }
        });
        MySingleton.getInstance().addToRequestQueue(stringRequest);

    }


    public void client_details_update()
    {
        progressDialog.showDialog();
        String url = getString(R.string.base_url)+getString(R.string.update_details);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.hideDialog();

                try{

                    JSONObject jsonObject = new JSONObject(response);

                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("200")){
                        Toast.makeText(ClientDetailsEdit.this, message,Toast.LENGTH_LONG).show();
                        finish();

                    }else if(status.equals("401")){

                        warningShow(message);

                    }else{
                        Toast.makeText(ClientDetailsEdit.this, message,Toast.LENGTH_LONG).show();
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

                int selectedClientMode = radioGroupClientMode.getCheckedRadioButtonId();
                int selectedPaymentMethod = radioGroupPaymentMethod.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                RadioButton radioButtonClientMode = findViewById(selectedClientMode);
                RadioButton radioButtonPaymentMethod = findViewById(selectedPaymentMethod);

                String client_mode = radioButtonClientMode.getText().toString().trim();
                String payment_method = radioButtonPaymentMethod.getText().toString().trim();

                map.put("jwt", jwt);
                map.put("id", client_id);
                map.put("mode", client_mode);
                map.put("name", name);
                map.put("phone", phone);
                map.put("address", address);
                map.put("email", email);

                if (selectedArea.equals("Select Area Name")){
                    //Set the existing area name
                    map.put("area", existArea);

                }else {
                    //Set the selected area
                    map.put("area", selectedArea);
                }

                map.put("username", username);
                map.put("password", password);

                map.put("speed", speed);
                map.put("fee", fee);
                map.put("payment_method", payment_method);

                return map;
            }
        };
        MySingleton.getInstance().addToRequestQueue(stringRequest);
    }

    public void warningShow(String message){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(false);
        alert.setTitle("Warning!!");
        alert.setMessage(message);
        alert.setIcon(R.drawable.ic_baseline_warning_24);

        alert.setPositiveButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog dlg = alert.create();
        dlg.show();
    }

}

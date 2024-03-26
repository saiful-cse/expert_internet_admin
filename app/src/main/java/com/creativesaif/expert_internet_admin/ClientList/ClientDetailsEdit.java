package com.creativesaif.expert_internet_admin.ClientList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.creativesaif.expert_internet_admin.Login;
import com.creativesaif.expert_internet_admin.Model.Client;
import com.creativesaif.expert_internet_admin.Model.DetailsWrapper;
import com.creativesaif.expert_internet_admin.Model.Package;
import com.creativesaif.expert_internet_admin.MySingleton;
import com.creativesaif.expert_internet_admin.Network.ApiInterface;
import com.creativesaif.expert_internet_admin.Network.RetrofitApiClient;
import com.creativesaif.expert_internet_admin.ProgressDialog;
import com.creativesaif.expert_internet_admin.R;
import com.creativesaif.expert_internet_admin.URL_config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;

public class ClientDetailsEdit extends AppCompatActivity{

    //Declaring EditText
    private EditText edclientname, edclientphone, edExpiredate, edpppusername, edpppassword;

    //Declaring RadioButton
    private RadioGroup radioGroupPaymentMethod, radioGroupClientMode;
    //Declaring String
    private String jwt, zone, super_admin, existAreaName, existAreaId;
    private JSONArray jsonArrayArea;
    private String id, pppassword, name, phone, pppname;

    private String selectedAreaId, expire_date, disable_date;

    private String selectedPackage, selectedZone, selectedTakeTime, payment_method;

    //Declaring progress dialog
    private ProgressDialog progressDialog;

    private SharedPreferences preferences;

    //Declaring spinner
    private Spinner areaSpinner,zoneSpinner, packageSpinner, take_timeSpiner;

    //Declaring Array List

    private ApiInterface apiInterface;
    private Client client;
    final Calendar myCalendar = Calendar.getInstance();
    private String employee_id;
    private ImageView imageDocumentView;

    int SELECT_PICTURE = 200;
    Bitmap bitmap;
    private String stringImageDocument;

    private CardView payment_method_card;
    private LinearLayout expired_date_layout, pacakge_layout, take_time_layout, zone_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_details_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferences = getApplicationContext().getSharedPreferences("users", MODE_PRIVATE);

        progressDialog = new ProgressDialog(this);
        id = getIntent().getStringExtra("id");
        jwt = preferences.getString("jwt", null);
        zone = preferences.getString("zone", null);
        super_admin = preferences.getString("super_admin", null);

        apiInterface = RetrofitApiClient.getClient().create(ApiInterface.class);
        client = new Client();
        employee_id = preferences.getString("employee_id", null);
        /*
        Id initialize
         */
        areaSpinner = findViewById(R.id.areaListSpinner);
        packageSpinner = findViewById(R.id.spinnerpkgs);
        zoneSpinner = findViewById(R.id.zoneListSpinner);

        radioGroupPaymentMethod = findViewById(R.id.radioGroupPaymentMethod);
        radioGroupClientMode = findViewById(R.id.radioGroupClientMode);
        edclientname = findViewById(R.id.edclientname);
        edclientphone = findViewById(R.id.edclientphone);
        edExpiredate = findViewById(R.id.edexpdateedit);
        edpppusername = findViewById(R.id.edpppusername);
        edpppassword = findViewById(R.id.edppppassword);
        take_timeSpiner = findViewById(R.id.take_time_ListSpinner);
        payment_method_card = findViewById(R.id.payment_method_card);
        imageDocumentView = findViewById(R.id.img_view_document);
        expired_date_layout = findViewById(R.id.expired_date_layout);
        pacakge_layout = findViewById(R.id.pacakge_layout);
        take_time_layout = findViewById(R.id.take_time_layout);
        zone_layout = findViewById(R.id.zone_layout);

        //View permission


        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.setTimeZone(TimeZone.getTimeZone("Asia/Dhaka"));
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);

                String myFormat="yyyy-MM-dd 09:00:00";
                SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.getDefault());
                edExpiredate.setText(dateFormat.format(myCalendar.getTime()));
                expire_date = dateFormat.format(myCalendar.getTime());
            }
        };

        edExpiredate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (employee_id.equals("9161")){
                    new DatePickerDialog(ClientDetailsEdit.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }else{
                   warningShow("Unable to edit. Something went wrong!!");
                }
            }
        });

        //Declaring Button
        Button buttonUpdate = findViewById(R.id.update_button);

        findViewById(R.id.btn_choose_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });

        if (!isNetworkConnected()) {
            Toast.makeText(getApplicationContext(), "Please!! Check internet connection.", Toast.LENGTH_SHORT).show();

        }else{
            client.setJwt(jwt);
            client.setId(id);
            load_details(client);
        }

        //Submit data to server
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name = edclientname.getText().toString().trim();
                phone = edclientphone.getText().toString().trim();
                pppname = edpppusername.getText().toString().trim();
                pppassword = edpppassword.getText().toString().trim();

                if (name.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Enter client name", Toast.LENGTH_SHORT).show();

                }else if(phone.isEmpty() || phone.length() < 11){
                    Toast.makeText(getApplicationContext(), "Enter correct phone number", Toast.LENGTH_SHORT).show();

                }else if(selectedAreaId.equals("1")){
                    Toast.makeText(getApplicationContext(), "Select Area", Toast.LENGTH_SHORT).show();

                } else if(phone.length() > 11){
                    Toast.makeText(getApplicationContext(), "Enter correct phone number", Toast.LENGTH_SHORT).show();

                } else if(pppname.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Enter PPP name", Toast.LENGTH_SHORT).show();

                }else if(pppassword.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Enter PPP password", Toast.LENGTH_SHORT).show();

                }else if(selectedPackage.equals("---")){
                    Toast.makeText(getApplicationContext(), "Select Package Name", Toast.LENGTH_SHORT).show();

                }else if(selectedZone.equals("---")){
                    Toast.makeText(getApplicationContext(), "Select Zone", Toast.LENGTH_SHORT).show();

                } else if(stringImageDocument.isEmpty()){
                    warningShow("500 KB সাইজের কম ডকুমেন্ট সিলেক্ট করুন");

                } else if (!isNetworkConnected()){
                    Toast.makeText(getApplicationContext(), "Please!! Check internet connection.", Toast.LENGTH_SHORT).show();

                }else if(id == null || jwt == null){
                    Toast.makeText(getApplicationContext(), "Session expired!!", Toast.LENGTH_LONG).show();
                    finish();
                    Intent intent = new Intent(ClientDetailsEdit.this, Login.class);
                    startActivity(intent);

                }else {

                    int selectedPaymentMethod = radioGroupPaymentMethod.getCheckedRadioButtonId();
                    RadioButton radioButtonPaymentMethod = findViewById(selectedPaymentMethod);
                    payment_method = radioButtonPaymentMethod.getText().toString().trim();

                    int selectedClientMode = radioGroupClientMode.getCheckedRadioButtonId();
                    RadioButton radioButtonClientMode = findViewById(selectedClientMode);
                    String client_mode = radioButtonClientMode.getText().toString().trim();

                    if (client_mode.equals("Disable")){

                        Date c = Calendar.getInstance().getTime();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
                        disable_date = df.format(c);
                    }

                    if (client_mode.equals("Disable") && !employee_id.equals("9161")){
                            warningShow(getResources().getString(R.string.disable_warning_message));

                    } else{

                        client.setJwt(jwt);
                        client.setId(id);
                        client.setMode(client_mode);
                        client.setPaymentMethod(payment_method);
                        client.setName(name);
                        client.setDocument(stringImageDocument);
                        client.setPhone(phone);
                        client.setArea_id(selectedAreaId);
                        client.setZone(selectedZone);
                        client.setExpireDate(expire_date);
                        client.setDisableDate(disable_date);
                        client.setTakeTime(selectedTakeTime);
                        client.setPppName(pppname);
                        client.setPppPass(pppassword);
                        client.setPkgId(selectedPackage);

                        updateDetails(client);
                    }

                }
            }
        });

        //Spinner item choice and click event
        areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // On selecting a spinner item
                try {
                    JSONObject json = jsonArrayArea.getJSONObject(position);
                    selectedAreaId = json.getString("id");

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        //Spinner item choice and click event
        zoneSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // On selecting a spinner item
                selectedZone = parentView.getItemAtPosition(position).toString();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        take_timeSpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // On selecting a spinner item
                selectedTakeTime = parentView.getItemAtPosition(position).toString();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        //Spinner item choice and click event
        packageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // On selecting a spinner item
                selectedPackage = parentView.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

    }


    public void load_details(Client mClient) {
        progressDialog.showDialog();
        Call<DetailsWrapper> call = apiInterface.getClientDetailsId(mClient);
        call.enqueue(new Callback<DetailsWrapper>() {
            @SuppressLint("ResourceType")
            @Override
            public void onResponse(Call<DetailsWrapper> call, retrofit2.Response<DetailsWrapper> response) {

                progressDialog.hideDialog();

                DetailsWrapper detailsWrapper = response.body();
                assert detailsWrapper != null;

                if (detailsWrapper.getStatus() == 401) {
                    //Go to phone verification step
                    Toast.makeText(getApplicationContext(), detailsWrapper.getMessage(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ClientDetailsEdit.this, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

                if (detailsWrapper.getStatus() == 200) {
                    edclientname.setText(detailsWrapper.getName());
                    edclientphone.setText(detailsWrapper.getPhone());
                    existAreaId = detailsWrapper.getArea_id();

                    stringImageDocument  = detailsWrapper.getDocument();

                    Glide.with(getApplicationContext())
                            .load(URL_config.BASE_URL+"documents/"+detailsWrapper.getDocument())
                            .into(imageDocumentView);


                    if (zone.equals("All") || zone.equals("Main")){
                        if (detailsWrapper.getPaymentMethod().equals("Cash")) {
                            radioGroupPaymentMethod.check(R.id.payment_cash);

                        } else if (detailsWrapper.getPaymentMethod().equals("Mobile")) {
                            radioGroupPaymentMethod.check(R.id.payment_mobile);
                        }
                    }else{
                        payment_method_card.setVisibility(View.GONE);
                    }

                    if (detailsWrapper.getPaymentMethod().equals("Cash")) {
                        radioGroupPaymentMethod.check(R.id.payment_cash);

                    } else if (detailsWrapper.getPaymentMethod().equals("Mobile")) {
                        radioGroupPaymentMethod.check(R.id.payment_mobile);
                    }

                    if (detailsWrapper.getMode().equals("Enable")){
                        radioGroupClientMode.check(R.id.client_enable);

                    }else if(detailsWrapper.getMode().equals("Disable")){
                        radioGroupClientMode.check(R.id.client_disable);
                    }

                    expire_date = detailsWrapper.getExpireDate();
                    disable_date = detailsWrapper.getDisableDate();

                    edpppusername.setText(detailsWrapper.getPppName());
                    edpppassword.setText(detailsWrapper.getPppPass());

                    edExpiredate.setText(expire_date);


                    if (!super_admin.equals("1")){
                        expired_date_layout.setVisibility(View.GONE);
                    }

                    if (!super_admin.equals("1")){
                        zone_layout.setVisibility(View.GONE);
                        selectedZone = detailsWrapper.getZone();
                    }

                    if (!super_admin.equals("1") && getIntent().getStringExtra("expired").equals("no")){
                        pacakge_layout.setVisibility(View.GONE);
                        selectedPackage = detailsWrapper.getPkgId();
                    }

                    if (getIntent().getStringExtra("expired").equals("no")){
                        take_time_layout.setVisibility(View.GONE);
                    }

                    //setting zone
                    ArrayAdapter<CharSequence> zoneArrayAdapter = ArrayAdapter.createFromResource(ClientDetailsEdit.this,
                            R.array.zone_name, android.R.layout.simple_spinner_item);
                    zoneSpinner.setAdapter(zoneArrayAdapter);

                    int zoneSpinnerPosition = zoneArrayAdapter.getPosition(detailsWrapper.getZone());
                    zoneSpinner.setSelection(zoneSpinnerPosition);
                    //End zone spinner

                    //Setting taketime
                    ArrayAdapter<CharSequence> takeTimeAdapter = ArrayAdapter.createFromResource(ClientDetailsEdit.this,
                            R.array.take_time, android.R.layout.simple_spinner_item);
                    take_timeSpiner.setAdapter(takeTimeAdapter);

                    int takeTimeSpinnerPosition = takeTimeAdapter.getPosition(detailsWrapper.getTakeTime());
                    take_timeSpiner.setSelection(takeTimeSpinnerPosition);
                    //End take time

                    ArrayAdapter<CharSequence> packageAdapter = ArrayAdapter.createFromResource(ClientDetailsEdit.this,
                            R.array.package_name, android.R.layout.simple_spinner_item);
                    packageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    packageSpinner.setAdapter(packageAdapter);

                    int spinnerPosition = packageAdapter.getPosition(detailsWrapper.getPkgId());
                    packageSpinner.setSelection(spinnerPosition);


                }else{
                    Toast.makeText(getApplicationContext(), detailsWrapper.getMessage(), Toast.LENGTH_LONG).show();
                }

                area_load();
            }

            @Override
            public void onFailure(Call<DetailsWrapper> call, Throwable t) {
                progressDialog.hideDialog();
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
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

    //Area load
    public void area_load()
    {
        String url = URL_config.BASE_URL+URL_config.AREA_LOAD;

        progressDialog.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.hideDialog();
                ArrayList<String> areaList = new ArrayList<>();
                try {

                    jsonArrayArea = new JSONArray(response);

                    for(int i=0; i<jsonArrayArea.length(); i++) {
                        JSONObject jsonObjectItem = jsonArrayArea.getJSONObject(i);
                        areaList.add(jsonObjectItem.getString("area_name"));
                    }

                    ArrayAdapter<String> AreaArrayAdapter = new ArrayAdapter<>(ClientDetailsEdit.this,
                            android.R.layout.simple_spinner_dropdown_item, areaList);
                    areaSpinner.setAdapter(AreaArrayAdapter);

                    //Assigning database value into Spinner
                    for(int i=0; i<jsonArrayArea.length(); i++) {
                        JSONObject jsonObjectItem = jsonArrayArea.getJSONObject(i);
                        if (jsonObjectItem.getString("id").equals(existAreaId)){
                            existAreaName = jsonObjectItem.getString("area_name");
                        }
                    }
                    int spinnerPosition = AreaArrayAdapter.getPosition(existAreaName);
                    areaSpinner.setSelection(spinnerPosition);

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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance().addToRequestQueue(stringRequest);;
    }

    public void updateDetails(Client client) {

        progressDialog.showDialog();
        Call<DetailsWrapper> call = apiInterface.updateDetails(client);
        call.enqueue(new Callback<DetailsWrapper>() {
            @SuppressLint("ResourceType")
            @Override
            public void onResponse(Call<DetailsWrapper> call, retrofit2.Response<DetailsWrapper> response) {

                progressDialog.hideDialog();

                DetailsWrapper detailsWrapper = response.body();
                assert detailsWrapper != null;

                //Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();

                if (detailsWrapper.getStatus() == 401) {
                    //Go to phone verification step
                    Toast.makeText(getApplicationContext(), detailsWrapper.getMessage(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ClientDetailsEdit.this, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } else if (detailsWrapper.getStatus() == 200) {
                    Toast.makeText(getApplicationContext(), detailsWrapper.getMessage(), Toast.LENGTH_LONG).show();
                    finish();

                }else{
                    warningShow(detailsWrapper.getMessage());
                }

            }

            @Override
            public void onFailure(Call<DetailsWrapper> call, Throwable t) {
                progressDialog.hideDialog();
                Toast.makeText(getApplicationContext(), "Failure: "+t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void imageChooser(){
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    @SuppressLint("Recycle")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {

                    //Size calculation
                    AssetFileDescriptor fileDescriptor = null;
                    try {
                        fileDescriptor = getApplicationContext().getContentResolver().openAssetFileDescriptor(selectedImageUri , "r");
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    assert fileDescriptor != null;
                    long fileSize = fileDescriptor.getLength() / 1024;

                    if (fileSize > 500){
                        warningShow("ডকুমেন্টের সাইজ অবশ্যই 500 KB এর কম হতে হবে। ফোনে আলাদা এপ ব্যবহার করে সাইজ করুন");
                        //Toast.makeText(getApplicationContext(), "File size must be less than 500 KB", Toast.LENGTH_LONG).show();

                    }else {
                        // update the preview image in the layout
                        imageDocumentView.setImageURI(selectedImageUri);
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),selectedImageUri);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        //Encoding
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageBytes = baos.toByteArray();
                        //imageView.setImageBitmap(bitmap);
                        stringImageDocument = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    }
                }
            }
        }
    }

    public void warningShow(String message){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(false);
        alert.setTitle("Warning!!");
        alert.setMessage(message);
        alert.setIcon(R.drawable.ic_baseline_warning_24);

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

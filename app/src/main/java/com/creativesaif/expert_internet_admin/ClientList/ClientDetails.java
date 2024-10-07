package com.creativesaif.expert_internet_admin.ClientList;



import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creativesaif.expert_internet_admin.Login;
import com.creativesaif.expert_internet_admin.MainActivity;
import com.creativesaif.expert_internet_admin.Model.Client;
import com.creativesaif.expert_internet_admin.Model.DetailsWrapper;
import com.creativesaif.expert_internet_admin.Model.Trns;
import com.creativesaif.expert_internet_admin.MySingleton;
import com.creativesaif.expert_internet_admin.Network.ApiInterface;
import com.creativesaif.expert_internet_admin.Network.RetrofitApiClient;

import com.creativesaif.expert_internet_admin.ProgressDialog;
import com.creativesaif.expert_internet_admin.R;
import com.creativesaif.expert_internet_admin.TransactionList.PaymentHistory;
import com.creativesaif.expert_internet_admin.URL_config;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;

public class ClientDetails extends AppCompatActivity {

    private TextView tvname, tvphone, tvarea, tvzone,
            tvppname, tvpppass, tvpppstatus, tvactivity, tvroutermac, tvlastlogout, tvlastlogin, tvuptime, tvdownload, tvupload, tvConnectedIp,
            tvmode, tvpaymentmethod, tvpackgeid, tvregdate, tvexpiredate, tvdisabledate, tvtaketime;
    private TextView  tv_ppp_macclear, tv_view_document, tvExpireText, tvdiconnecttemp, tvpppinfotemp, tvpaybilltemp, tvhelptemp, tvrouteralogin;
    private LinearLayout linearLayoutPPPStatus, linearLayoutOnuStatus;

    String currentMode;
    private String expired, last_router_mac, olt_url;
    private String document, jwt, name, id, pppName, ppppass, emp_id, phone, informMessage, take_time, connected_ip, mobile_payment_reference;
    private SharedPreferences sharedPreferences;
    private ApiInterface apiInterface;
    private Client client;
    private Trns trns;
    private Button btndeleteClient,btnDetailsEdit;

    private SwipeRefreshLayout swipeRefreshLayout;

    private EditText editTextAmount, editTextInformSms;
    private String zone, payment_type, payment_method, amount;
    private RadioGroup radioGroup, radioGroup2;
    Button buttonTxnSubmit;
    private TextView tvGetPPPStatus;
    private ProgressBar pppStatusProgressbar, onuStatusProgressbar;
    /*
   Progress dialog
    */
    ProgressDialog progressDialog;

    private Switch pppswitch;

    private TextView tvgetOnuStatus, tvoltname, tvolturl, tvonuid, tvonustatus, tvonumac, tvonudesc, tvonudistance, tvlastregtime, tvlastdregtime, tvdregreason, tvonuptime, tvonupower;

    private CardView make_payment_layout;
    private ImageView phonecopybtn, smscpybtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        id = getIntent().getStringExtra("id");
        sharedPreferences = getApplicationContext().getSharedPreferences("users", MODE_PRIVATE);
        jwt = sharedPreferences.getString("jwt", null);
        zone = sharedPreferences.getString("zone", null);

        mobile_payment_reference = "";

        /*
        Txn ID initialize
         */
        swipeRefreshLayout = findViewById(R.id.details_refresh);
        tvExpireText = findViewById(R.id.expireText);
        radioGroup = findViewById(R.id.radioGroup);
        radioGroup2 = findViewById(R.id.radioGroup2);
        editTextAmount = findViewById(R.id.edAmount);
        tv_view_document = findViewById(R.id.tv_view_document);

        pppStatusProgressbar = findViewById(R.id.getPpStatusProgressBar);
        tvGetPPPStatus = findViewById(R.id.tvGetStatus);
        linearLayoutPPPStatus = findViewById(R.id.pppStatusLayout);

        Button btnPaymentHistory = findViewById(R.id.btnPaymentHistory);
        ImageView user_call = findViewById(R.id.user_direct_call);
        phonecopybtn = findViewById(R.id.phonecopybtn);
        smscpybtn = findViewById(R.id.smscopybtn);

        //----------------------
        // Details ID initialize
        //-----------------------

        //-------Profile--------
        tvname = findViewById(R.id.tvname);
        tvphone = findViewById(R.id.tvphone);
        tvarea = findViewById(R.id.tvarea);
        tvzone = findViewById(R.id.tvzone);

        //--------PPPoE Connection-------
        pppswitch = findViewById(R.id.pppswitch);
        tvpppstatus = findViewById(R.id.tvppp_status);
        tvppname = findViewById(R.id.tvppp_name);
        tvpppass = findViewById(R.id.tvppp_pass);
        tv_ppp_macclear= findViewById(R.id.tvppp_mac_clear);

        tvactivity = findViewById(R.id.tvppp_activity);
        tvroutermac = findViewById(R.id.tvrouter_mac);
        tvlastlogout = findViewById(R.id.tvlogout_time);
        tvlastlogin = findViewById(R.id.tvlogin);
        tvuptime = findViewById(R.id.tvuptime);
        tvdownload = findViewById(R.id.tvdownload);
        tvupload = findViewById(R.id.tvupload);
        tvConnectedIp = findViewById(R.id.tvconnectedip);

        //------ONU information ---------
        tvgetOnuStatus = findViewById(R.id.tvGetOnuStatus);
        linearLayoutOnuStatus = findViewById(R.id.onuStatusLayout);
        onuStatusProgressbar = findViewById(R.id.onustatusprogress);
        tvoltname = findViewById(R.id.tvoltname);
        tvolturl = findViewById(R.id.tvolturl);
        tvonuid = findViewById(R.id.tvonuid);
        tvonustatus = findViewById(R.id.tvonustatus);
        tvonumac = findViewById(R.id.tvonumac);
        tvonudesc = findViewById(R.id.tvonudesc);
        tvonudistance = findViewById(R.id.tvonudistance);
        tvlastregtime = findViewById(R.id.tvonulastregtime);
        tvlastdregtime = findViewById(R.id.tvonulastdregtime);
        tvdregreason = findViewById(R.id.tvdregreason);
        tvonuptime = findViewById(R.id.tvonuuptime);
        tvonupower = findViewById(R.id.tvonupower);


        //------Service details--------
        tvpaymentmethod = findViewById(R.id.tvpaymentmethod);
        tvpackgeid = findViewById(R.id.tvpackgeid);
        tvregdate = findViewById(R.id.tvregdate);
        tvexpiredate = findViewById(R.id.tvexpiredate);
        tvdisabledate = findViewById(R.id.tvdisabledate);
        tvtaketime = findViewById(R.id.tvtaketime);

        tvdiconnecttemp = findViewById(R.id.templateDisconnect);
        tvpppinfotemp = findViewById(R.id.templatePppinfo);
        tvpaybilltemp = findViewById(R.id.templatePaybill);
        tvhelptemp = findViewById(R.id.templateHelp);
        make_payment_layout = findViewById(R.id.make_payment_layout);
        tvrouteralogin = findViewById(R.id.templaterouterlogin);
        editTextInformSms = findViewById(R.id.edInformSms);
        btnDetailsEdit = findViewById(R.id.btnEdit);
        btndeleteClient = findViewById(R.id.btnDeleteClient);
        tvmode = findViewById(R.id.tvmode);


        // -----------End-----------------------

        progressDialog = new ProgressDialog(this);

        apiInterface = RetrofitApiClient.getClient().create(ApiInterface.class);
        client = new Client();
        trns = new Trns();

        if (!isNetworkConnected()) {
            Toast.makeText(getApplicationContext(), "Please!! Check internet connection.", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);

        }else{
            client.setJwt(jwt);
            client.setId(id);
            load_details(client);
        }

        if(Objects.equals(sharedPreferences.getString("client_details_update", null), "1")){
            btnDetailsEdit.setVisibility(View.VISIBLE);
        }else {
            btnDetailsEdit.setVisibility(View.GONE);
        }

        if (zone.equals("All") || zone.equals("Main")){
            make_payment_layout.setVisibility(View.VISIBLE);
        } else{
            editTextInformSms.setFocusable(false);
            make_payment_layout.setVisibility(View.GONE);
        }

        tv_view_document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(URL_config.BASE_URL+"documents/"+document));
                startActivity(in);
            }
        });

        user_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_VIEW);
                callIntent.setData(Uri.parse("tel:+88"+phone));
                startActivity(callIntent);
            }
        });

        phonecopybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager)getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(tvphone.getText().toString().trim());
            }
        });

        smscpybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager)getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(editTextInformSms.getText().toString().trim());
            }
        });

        tvConnectedIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse("http://"+connected_ip+":8080"));
                startActivity(in);
            }
        });

        tvolturl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(olt_url+"/action/login.html"));
                startActivity(in);
            }
        });

        tv_ppp_macclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pppMacClear();
            }
        });

        pppswitch = findViewById(R.id.pppswitch); // initiate Switch
        pppswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pppswitch.isChecked()){
                    getPPPAction("enable");
                }else{
                    getPPPAction("disable");
                }
            }
        });



        tvGetPPPStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNetworkConnected()) {
                    Toast.makeText(getApplicationContext(), "Please!! Check internet connection.", Toast.LENGTH_SHORT).show();

                }else{
                    getPPPStatus();
                }
            }
        });

        tvgetOnuStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNetworkConnected()) {
                    Toast.makeText(getApplicationContext(), "Please!! Check internet connection.", Toast.LENGTH_SHORT).show();

                }else{
                    getOnuStatusByRouterMac(last_router_mac);
                }

            }
        });

        //reload or refresh posts
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!isNetworkConnected()){
                    Toast.makeText(getApplicationContext(), "Please!! Check internet connection.", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);

                } else{
                    client.setJwt(jwt);
                    client.setId(id);
                    load_details(client);
                }
            }
        });

        buttonTxnSubmit = findViewById(R.id.txn_submit);
        buttonTxnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount = editTextAmount.getText().toString().trim();
                emp_id = sharedPreferences.getString("employee_id", null);

                if (currentMode.equals("Disable")){
                    warningShowDisablePayment();

                }else if(radioGroup.getCheckedRadioButtonId() == -1)
                {
                    Snackbar.make(findViewById(android.R.id.content),"Select payment type",Snackbar.LENGTH_LONG).show();

                } else if(radioGroup2.getCheckedRadioButtonId() == -1){
                    Snackbar.make(findViewById(android.R.id.content),"Select payment method",Snackbar.LENGTH_LONG).show();

                } else if(amount.isEmpty())
                {
                    Snackbar.make(findViewById(android.R.id.content),"Write an amount",Snackbar.LENGTH_LONG).show();

                }else if(!isNetworkConnected()){
                    Snackbar.make(findViewById(android.R.id.content),"Please!! Check Internet Connection or Try again later.",Snackbar.LENGTH_LONG).show();

                }else if(!take_time.equals("0")){
                    warningShowTakeTime();

                } else{

                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    int selectedMethod = radioGroup2.getCheckedRadioButtonId();
                    RadioButton radioButton = findViewById(selectedId);
                    RadioButton radioButton2 = findViewById(selectedMethod);
                    payment_type = radioButton.getText().toString();
                    payment_method = radioButton2.getText().toString().trim();

                    if (!payment_method.equals("Cash")){
                        getMobilePaymentRefInputDialog();
                    }else{
                        txn_confirm_diaglog();
                    }

                }
            }
        });

        //Make inform
        Button btnSmsSend = findViewById(R.id.btnInformSend);


        tvdiconnecttemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextInformSms.setText("আপনার WiFi সংযোগের মেয়াদ শেষ, অটো চালু করতে লিংক দিয়ে বিল পরিশোধ করুন।\n"+URL_config.PAYBILL_URL+phone);
            }
        });

        tvpppinfotemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextInformSms.setText("Your connection type is PPPoE\n" +
                        "Username: " +pppName+
                        "\nPassword: " +ppppass+
                        "\nDon't share with anyone.");
            }
        });

        tvpaybilltemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextInformSms.setText("Your payment link is:\n"+URL_config.PAYBILL_URL+phone);
            }
        });

        String mainzonehelp = "WiFi Help line number:\n" +
                "01906282646 (Arif)\n" +
                "01621840795 (Shahria)\n" +
                "কলে না পাইলে অফিসের 01975559161 Whatsapp এ মেসেজ দিন।";

        String osmanzonehelp = "WiFi Help line number:\n" +
                "পি এম খালি প্রতিনিধি 01893006606 (উসমান)";

        String siddikzonehelp = "WiFi Help line number:\n" +
                "ঊমখালী প্রতিনিধি 01813838162 (রাজ্জাক)";

        tvhelptemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(zone.equals("Osman")){
                    editTextInformSms.setText(osmanzonehelp);
                }else if(zone.equals("Siddik")){
                    editTextInformSms.setText(siddikzonehelp);
                }
                else{
                    editTextInformSms.setText(mainzonehelp);
                }

            }
        });

        tvrouteralogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String adminpassword = "admin"+pppName.replace("ss-expnet-","");

                editTextInformSms.setText("Router login credentials are: \n" +
                        "URL: 192.168.0.1 or 192.168.1.1 \n"+
                        "Username: admin"+
                        "\nPassword: " +adminpassword+
                        "\nDon't share with wifi users.");

            }
        });

        btnSmsSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                informMessage = editTextInformSms.getText().toString().trim();

                if (jwt == null){
                    finish();
                    startActivity(new Intent(ClientDetails.this, Login.class));

                } else if(informMessage.isEmpty())
                {
                    Snackbar.make(findViewById(android.R.id.content),"Write a message.",Snackbar.LENGTH_LONG).show();

                }else if(!isNetworkConnected()) {
                    Snackbar.make(findViewById(android.R.id.content),"Please!! Check Internet Connection or Try again later.",Snackbar.LENGTH_LONG).show();

                } else{
                    inform_confirm_dialog();
                }
            }
        });

        /*
        id initialize here
         */
        //assign all objects to avoid nullPointerException

        btnDetailsEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(ClientDetails.this,ClientDetailsEdit.class);
                i.putExtra("id", id);
                i.putExtra("expired", expired);
                startActivity(i);
            }
        });

        btndeleteClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                warningShowClientDelete();
                //Toast.makeText(getApplicationContext(), jwt+"\n"+id+"\n"+pppName+"\n"+document, Toast.LENGTH_SHORT).show();

            }
        });

        btnPaymentHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ClientDetails.this, PaymentHistory.class);
                i.putExtra("id", id);
                startActivity(i);
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

    //Internet connection check
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public void load_details(Client mClient) {

        progressDialog.showDialog();
        Call<DetailsWrapper> call = apiInterface.getClientDetailsId(mClient);
        call.enqueue(new Callback<DetailsWrapper>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint("ResourceType")
            @Override
            public void onResponse(Call<DetailsWrapper> call, retrofit2.Response<DetailsWrapper> response) {

                swipeRefreshLayout.setRefreshing(false);
                progressDialog.hideDialog();

                DetailsWrapper detailsWrapper = response.body();
                assert detailsWrapper != null;

                if (detailsWrapper.getStatus() == 401) {
                    //Go to phone verification step
                    loginWarningShow(detailsWrapper.getMessage());

                }else if(detailsWrapper.getStatus() == 404){

                    notfoundShow("Not found client by the PPP name");

                } else if (detailsWrapper.getStatus() == 200) {

                    linearLayoutPPPStatus.setVisibility(View.GONE);
                    linearLayoutOnuStatus.setVisibility(View.GONE);
                    tvGetPPPStatus.setText("Get PPP Status");
                    tvGetPPPStatus.setVisibility(View.VISIBLE);

                    id = detailsWrapper.getId();
                    phone = detailsWrapper.getPhone();
                    name = detailsWrapper.getName();
                    tvname.setText(detailsWrapper.getName());
                    tvphone.setText(detailsWrapper.getPhone());
                    tvarea.setText(detailsWrapper.getArea());
                    tvzone.setText(detailsWrapper.getZone());
                    document = detailsWrapper.getDocument();
                    currentMode = detailsWrapper.getMode();
                    tvmode.setText(currentMode);

                    if (currentMode.equals("Disable")){
                        tvmode.setTextColor(Color.RED);
                        tvExpireText.setVisibility(View.GONE);

                        linearLayoutPPPStatus.setVisibility(View.GONE);
                        tvGetPPPStatus.setText("");
                        tvGetPPPStatus.setVisibility(View.GONE);
                        btndeleteClient.setVisibility(View.VISIBLE);

                    }else{
                        tvmode.setTextColor(Color.GREEN);
                        btndeleteClient.setVisibility(View.GONE);
                    }

                    pppName = detailsWrapper.getPppName();
                    ppppass = detailsWrapper.getPppPass();
                    tvppname.setText(detailsWrapper.getPppName());
                    tvpppass.setText(detailsWrapper.getPppPass());

                    tvpaymentmethod.setText(detailsWrapper.getPaymentMethod());
                    tvpackgeid.setText(detailsWrapper.getPkgId());
                    tvregdate.setText(detailsWrapper.getRegDate());
                    tvexpiredate.setText(detailsWrapper.getExpireDate());
                    tvdisabledate.setText(detailsWrapper.getDisableDate());
                    take_time = detailsWrapper.getTakeTime();
                    tvtaketime.setText(take_time+" Days");

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                    try {
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeZone(TimeZone.getTimeZone("Asia/Dhaka"));
                        Date currentDate = cal.getTime();
                        Date expire_Date = sdf.parse(detailsWrapper.getExpireDate());

                        if (currentDate.getTime() >= expire_Date.getTime()){
                            expired = "yes";
                            cal.setTimeInMillis(currentDate.getTime() - expire_Date.getTime());
                            int m = cal.get(Calendar.MONTH)+1;
                            int d = cal.get(Calendar.DAY_OF_MONTH);
                            tvExpireText.setText(m+" Month "+d +" Day expired (with current month)");
                        }else{
                            expired = "no";
                            tvExpireText.setVisibility(View.GONE);
                        }


                    } catch (ParseException e) {
                        //e.printStackTrace();
                        Toast.makeText(getApplicationContext(), (CharSequence) e, Toast.LENGTH_LONG).show();
                    }

                }else{
                    warningShow(detailsWrapper.getMessage());
                    //Toast.makeText(getApplicationContext(), detailsWrapper.getStatus()+"\n"+detailsWrapper.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<DetailsWrapper> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                progressDialog.hideDialog();
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();

            }
        });
    }

    public void getPPPStatus()
    {
        String url = sharedPreferences.getString("api_base", null)+"pppStatus.php";

        pppStatusProgressbar.setVisibility(View.VISIBLE);
        tvGetPPPStatus.setVisibility(View.GONE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                pppStatusProgressbar.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("status").equals("500"))
                    {
                        linearLayoutPPPStatus.setVisibility(View.GONE);
                        tvGetPPPStatus.setText("Refresh");
                        tvGetPPPStatus.setVisibility(View.VISIBLE);
                        warningShow(jsonObject.getString("message"));

                    }else if(jsonObject.getString("status").equals("200")){

                        linearLayoutPPPStatus.setVisibility(View.VISIBLE);
                        tvGetPPPStatus.setText("Refresh");
                        tvGetPPPStatus.setVisibility(View.VISIBLE);

                        pppswitch.setChecked(jsonObject.getString("ppp_status").equals("Enable"));
                        tvpppstatus.setText(jsonObject.getString("ppp_status"));
                        tvactivity.setText(jsonObject.getString("ppp_activity"));
                        tvroutermac.setText(jsonObject.getString("router_mac"));
                        last_router_mac = jsonObject.getString("router_mac");
                        tvlastlogout.setText(jsonObject.getString("last_loged_out"));
                        tvlastlogin.setText(jsonObject.getString("last_log_in"));
                        tvuptime.setText(jsonObject.getString("uptime"));
                        tvdownload.setText(jsonObject.getString("download"));
                        tvupload.setText(jsonObject.getString("upload"));
                        tvConnectedIp.setTextColor(Color.BLUE);
                        connected_ip = jsonObject.getString("connected_ip");
                        tvConnectedIp.setText(connected_ip);

                        if (jsonObject.getString("ppp_activity").equals("Online")){
                            tvactivity.setTextColor(Color.GREEN);

                        }else{
                            tvactivity.setTextColor(Color.RED);
                            linearLayoutOnuStatus.setVisibility(View.GONE);
                        }

                        tvgetOnuStatus.setVisibility(View.VISIBLE);
                        tvgetOnuStatus.setText("Get ONU Status");

                    }else{
                        linearLayoutPPPStatus.setVisibility(View.GONE);
                        tvGetPPPStatus.setText("Refresh");
                        tvGetPPPStatus.setVisibility(View.VISIBLE);
                        warningShow(jsonObject.getString("message"));
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pppStatusProgressbar.setVisibility(View.GONE);
                linearLayoutPPPStatus.setVisibility(View.GONE);
                warningShow(error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                map.put("ppp_name", pppName);
                map.put("login_ip", Objects.requireNonNull(sharedPreferences.getString("login_ip", null)));
                map.put("username", Objects.requireNonNull(sharedPreferences.getString("username", null)));
                map.put("password", Objects.requireNonNull(sharedPreferences.getString("password", null)));
                return map;

            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance().addToRequestQueue(stringRequest);
    }

    public void getOnuStatusByRouterMac(String mac)
    {

        String url = "https://kgnethost.oltmonitor.com/business_api/api.php?auth=K25sghf6le9b7MkzXpS652&action=onustatus&mac="+mac;

        onuStatusProgressbar.setVisibility(View.VISIBLE);
        tvgetOnuStatus.setVisibility(View.GONE);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                onuStatusProgressbar.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("status_code").equals("200"))
                    {
                        linearLayoutOnuStatus.setVisibility(View.VISIBLE);
                        tvoltname.setText(jsonObject.getString("olt_name"));
                        tvolturl.setTextColor(Color.BLUE);
                        olt_url = jsonObject.getString("olt_url");
                        tvolturl.setText(olt_url);
                        tvonuid.setText(jsonObject.getString("onu_id"));
                        tvonustatus.setText(jsonObject.getString("status"));
                        tvonumac.setText(jsonObject.getString("mac_ddress"));
                        tvonudesc.setText(jsonObject.getString("description"));
                        tvonudistance.setText(jsonObject.getString("distance"));
                        tvlastregtime.setText(jsonObject.getString("last_register_time"));
                        tvlastdregtime.setText(jsonObject.getString("last_deregister_time"));
                        tvdregreason.setText(jsonObject.getString("last_deregister_reason"));
                        tvonuptime.setText(jsonObject.getString("alive_time"));
                        tvonupower.setText(jsonObject.getString("rx_power"));

                        if (jsonObject.getString("status").equals("Online")){
                            tvonustatus.setTextColor(Color.GREEN);
                        }else{
                            tvonustatus.setTextColor(Color.RED);
                        }

                        tvgetOnuStatus.setVisibility(View.VISIBLE);
                        tvgetOnuStatus.setText("Refresh");

                    }else{
                        warningShow(jsonObject.getString("status_code"));
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onuStatusProgressbar.setVisibility(View.GONE);
                warningShow(error.toString());
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance().addToRequestQueue(stringRequest);;
    }


    public void pppMacClear()
    {
        String url = sharedPreferences.getString("api_base", null)+"pppMacClear.php";

        progressDialog.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("500")){
                        linearLayoutPPPStatus.setVisibility(View.GONE);
                        warningShow(jsonObject.getString("message"));

                    }else if(jsonObject.getString("status").equals("200")){
                        linearLayoutPPPStatus.setVisibility(View.GONE);
                        warningShow(jsonObject.getString("message"));
                    }else{
                        linearLayoutPPPStatus.setVisibility(View.GONE);
                        warningShow(jsonObject.getString("message"));
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                linearLayoutPPPStatus.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                map.put("ppp_name", pppName);
                map.put("login_ip", Objects.requireNonNull(sharedPreferences.getString("login_ip", null)));
                map.put("username", Objects.requireNonNull(sharedPreferences.getString("username", null)));
                map.put("password", Objects.requireNonNull(sharedPreferences.getString("password", null)));
                return map;

            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance().addToRequestQueue(stringRequest);;

    }

    public void getPPPAction(String actionType)
    {
        String url = sharedPreferences.getString("api_base", null)+"pppAction.php";

        progressDialog.showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("500")){
                        linearLayoutPPPStatus.setVisibility(View.GONE);
                        warningShow(jsonObject.getString("message"));

                    }else if(jsonObject.getString("status").equals("200")){
                        linearLayoutPPPStatus.setVisibility(View.GONE);
                        warningShow(jsonObject.getString("message"));
                    }else{
                        linearLayoutPPPStatus.setVisibility(View.GONE);
                        warningShow(jsonObject.getString("message"));
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                linearLayoutPPPStatus.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                map.put("ppp_name", pppName);
                map.put("action_type", actionType);
                map.put("login_ip", Objects.requireNonNull(sharedPreferences.getString("login_ip", null)));
                map.put("username", Objects.requireNonNull(sharedPreferences.getString("username", null)));
                map.put("password", Objects.requireNonNull(sharedPreferences.getString("password", null)));
                return map;

            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance().addToRequestQueue(stringRequest);;

    }

    public void informSmsSend()
    {
        progressDialog.showDialog();

        String url = URL_config.BASE_URL+URL_config.IDWISE_CLIENT_SMS;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.hideDialog();

                try{

                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("200")){
                        Toast.makeText(ClientDetails.this, message,Toast.LENGTH_LONG).show();
                        finish();

                    }else if(status.equals("401")){

                        loginWarningShow(message);

                    }else{
                        warningShow(message);
                        //Toast.makeText(ClientDetails.this, message,Toast.LENGTH_LONG).show();
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                Toast.makeText(ClientDetails.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                map.put("jwt", jwt);
                map.put("message", informMessage);
                map.put("phone", phone);
                map.put("client_id", id);

                return map;

            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance().addToRequestQueue(stringRequest);
    }


    public void clientDelete()
    {
        progressDialog.showDialog();

        String url = URL_config.BASE_URL+URL_config.CLIENT_DELETE;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.hideDialog();

                try{
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("200")){
                        Toast.makeText(ClientDetails.this, message,Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ClientDetails.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }else if(status.equals("401")){

                        loginWarningShow(message);

                    }else{
                        warningShow(message);
                        //Toast.makeText(ClientDetails.this, message,Toast.LENGTH_LONG).show();
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                Toast.makeText(ClientDetails.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams()throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                map.put("jwt", jwt);
                map.put("client_id", id);
                map.put("ppp_name", pppName);
                map.put("document", document);
                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance().addToRequestQueue(stringRequest);
    }


    public void employee_make_payment(Trns mTrns)
    {
        progressDialog.showDialog();
        Call<Trns> call = apiInterface.employeeMakePayment(mTrns);
        call.enqueue(new Callback<Trns>() {
            @SuppressLint("ResourceType")
            @Override
            public void onResponse(Call<Trns> call, retrofit2.Response<Trns> response) {

                progressDialog.hideDialog();
                Trns trns = response.body();
                assert trns != null;

                if (trns.getStatus() == 200){
                    Toast.makeText(ClientDetails.this, trns.getMessage(),Toast.LENGTH_LONG).show();
                    finish();

                }else if(trns.getStatus() == 401){

                    loginWarningShow(trns.getMessage());

                }else{
                    warningShow(trns.getMessage());
                    //Toast.makeText(ClientDetails.this, trns.getMessage(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Trns> call, Throwable t) {
                progressDialog.hideDialog();
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void txn_confirm_diaglog(){
        AlertDialog.Builder aleart1 = new AlertDialog.Builder(this);
        aleart1.setCancelable(false);
        aleart1.setTitle("পেমেন্টটি কনফার্ম করুন।");
        aleart1.setMessage("নাম: "+name+"\n"+"পেমেন্টের ধরন: "+payment_type+"\n"+"পরিমান: "+amount+"\nএই মুহুর্থে এই পেমেন্টটি সাবমিট করতে চান?");
        aleart1.setIcon(R.drawable.warning_icon);

        aleart1.setPositiveButton("সব ঠিক আছে", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                trns.setJwt(jwt);
                trns.setClientId(id);
                trns.setName(name);
                trns.setEmpId(emp_id);
                trns.setTxnType(payment_type);
                trns.setMethod(payment_method);
                trns.setDetails(name+", "+pppName+", "+payment_type+", "+payment_method+"-"+mobile_payment_reference);
                trns.setAmount(amount);

                //Toast.makeText(getApplicationContext(), client_id+"\n"+admin_id+"\n"+payment_type+"\n"+payment_method+"\n"+amount,Toast.LENGTH_LONG).show();
                employee_make_payment(trns);
            }
        });

        aleart1.setNegativeButton("না", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog dlg = aleart1.create();
        dlg.show();
    }

    public void inform_confirm_dialog(){
        AlertDialog.Builder aleart1 = new AlertDialog.Builder(this);
        aleart1.setCancelable(false);
        aleart1.setTitle("মেসেজটি কনফার্ম করুন।");
        aleart1.setMessage("এই মেসেজটি "+name+" এর কাছে পাঠাতে চান?");
        aleart1.setIcon(R.drawable.warning_icon);

        aleart1.setPositiveButton("Ok, Sure", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                informSmsSend();
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
//
    public void loginWarningShow(String message){
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(this);
        alert.setCancelable(false);
        alert.setTitle("Warning!!");
        alert.setMessage(message);
        alert.setIcon(R.drawable.warning_icon);

        alert.setPositiveButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
                startActivity(new Intent(ClientDetails.this, Login.class));
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        android.app.AlertDialog dlg = alert.create();
        dlg.show();
    }

    public void warningShow(String message){
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(this);
        alert.setCancelable(false);
        alert.setTitle("Message!!");
        alert.setMessage(message);
        alert.setIcon(R.drawable.ic_baseline_message_24);

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        android.app.AlertDialog dlg = alert.create();
        dlg.show();
    }

    public void notfoundShow(String message){
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(this);
        alert.setCancelable(false);
        alert.setTitle("Warning!!");
        alert.setMessage(message);
        alert.setIcon(R.drawable.ic_baseline_warning_24);

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                finish();
            }
        });
        android.app.AlertDialog dlg = alert.create();
        dlg.show();
    }


    public void warningShowDisablePayment(){
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(this);
        alert.setCancelable(false);
        alert.setTitle("Warning!!");
        alert.setMessage("ক্লায়েন্টের মোড Disable অবস্থায় পেমেন্ট ইনফুট দেওয়া যাবেনা। ক্লায়েন্ট লাইন চালাবে কিনা করফার্ম করে Enable করার পর পেমেন্টে ইনফুট দিন");
        alert.setIcon(R.drawable.ic_baseline_warning_24);

        alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        android.app.AlertDialog dlg = alert.create();
        dlg.show();
    }

    public void warningShowTakeTime(){
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(this);
        alert.setCancelable(false);
        alert.setTitle("Warning!!");
        alert.setMessage("এই ক্লায়েন্টটি বিল পেমেন্টের জন্য সময় নিয়েছিল। সমস্ত বিল পেমেন্ট করে থাকলে take time অপশনটি 0 করে আপডেট দিন। অন্যথায় প্রতি মাসে লাইন অটো অফ হবেনা।");
        alert.setIcon(R.drawable.ic_baseline_warning_24);

        alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                int selectedMethod = radioGroup2.getCheckedRadioButtonId();
                RadioButton radioButton = findViewById(selectedId);
                RadioButton radioButton2 = findViewById(selectedMethod);
                payment_type = radioButton.getText().toString();
                payment_method = radioButton2.getText().toString().trim();

                if (!payment_method.equals("Cash")){
                    getMobilePaymentRefInputDialog();
                }else{
                    txn_confirm_diaglog();
                }
            }
        });
        android.app.AlertDialog dlg = alert.create();
        dlg.show();
    }

    public void warningShowClientDelete(){
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(this);
        alert.setCancelable(true);
        alert.setTitle("সতর্ক");
        alert.setMessage("এই কাস্টমারের PPP সহ সব তথ্য ডিলিট করতে চান? যদি ডিলিট করেন আর পুনরায় ব্যাক আনা যাবেনা। ফাইবার কেবল খুলে আনার পর ডিলিট দিন।");
        alert.setIcon(R.drawable.ic_baseline_warning_24);

        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                clientDelete();
            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        android.app.AlertDialog dlg = alert.create();
        dlg.show();
    }

    public void getMobilePaymentRefInputDialog(){
        AlertDialog.Builder aleart1 = new AlertDialog.Builder(this);
        final EditText input = new EditText(ClientDetails.this);
        input.setSingleLine();
        aleart1.setCancelable(false);
        aleart1.setTitle("Enter any reference");
        aleart1.setView(input);
        aleart1.setPositiveButton("Continue...", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                mobile_payment_reference = input.getText().toString().trim();

                // ensure that user input bar is not empty
                if (mobile_payment_reference.equals("")){
                    Toast.makeText(getBaseContext(), "Enter any reference", Toast.LENGTH_LONG).show();
                }else{
                    txn_confirm_diaglog();
                }
            }
        });
        aleart1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog dlg = aleart1.create();
        dlg.show();
    }

}

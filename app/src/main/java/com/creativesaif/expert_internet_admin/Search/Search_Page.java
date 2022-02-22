package com.creativesaif.expert_internet_admin.Search;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creativesaif.expert_internet_admin.Adapter.ClientAdapter;
import com.creativesaif.expert_internet_admin.Login;
import com.creativesaif.expert_internet_admin.Model.Client;
import com.creativesaif.expert_internet_admin.Model.ClientWrapper;
import com.creativesaif.expert_internet_admin.MySingleton;
import com.creativesaif.expert_internet_admin.Network.ApiInterface;
import com.creativesaif.expert_internet_admin.Network.RetrofitApiClient;
import com.creativesaif.expert_internet_admin.ProgressDialog;
import com.creativesaif.expert_internet_admin.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class Search_Page extends AppCompatActivity {


    ProgressDialog progressDialog;
    RecyclerView recyclerView;
    Context context;

    private ClientAdapter clientAdapter;
    private List<Client> clientList;
    private ApiInterface apiInterface;

    private Client client;
    private ImageView errorImage;
    private TextView errorText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        errorImage = findViewById(R.id.error_icon);
        errorText = findViewById(R.id.error_text);

        progressDialog = new ProgressDialog(this);
        clientList = new ArrayList<>();
        clientAdapter = new ClientAdapter(getApplicationContext(), clientList);

        recyclerView = findViewById(R.id.searchRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(clientAdapter);

        apiInterface = RetrofitApiClient.getClient().create(ApiInterface.class);
        client = new Client();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Type PPPoE, area, name, phone etc.");

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                //Toast.makeText(Search.this, "You have search "+query,Toast.LENGTH_LONG).show();

                if(isNetworkConnected()) {

                    client.setSearchKey(query.trim());
                    load_client(client);
                }
                else{

                    Snackbar.make(findViewById(android.R.id.content),"Please! Check Internet Connection.",Snackbar.LENGTH_LONG).show();

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String tag) {
                // filter recycler view when text is changed
                //Toast.makeText(MainActivity.this, "You have search "+newText,Toast.LENGTH_LONG).show();

                return false;
            }
        });
        return true;
    }


    public void load_client(Client mClient) {

        progressDialog.showDialog();
        Call<ClientWrapper> call = apiInterface.search_data(mClient);
        call.enqueue(new Callback<ClientWrapper>() {
            @Override
            public void onResponse(Call<ClientWrapper> call, retrofit2.Response<ClientWrapper> response) {

                progressDialog.hideDialog();
                clientList.clear();

                ClientWrapper clientWrapper = response.body();
                assert clientWrapper != null;

                //Toast.makeText(getApplicationContext(), clientWrapper.toString(), Toast.LENGTH_LONG).show();

                if (clientWrapper.getStatus() == 404) {
                    //client not found then visible error
                    errorImage.setImageResource(R.drawable.ic_baseline_client_24);
                    errorText.setText(clientWrapper.getMessage());

                }else if (clientWrapper.getStatus() == 200) {
                    errorImage.setVisibility(View.GONE);
                    errorText.setVisibility(View.GONE);
                    clientAdapter.setClientList(clientWrapper.getClients());

                }else {
                    Toast.makeText(getApplicationContext(), clientWrapper.getMessage(), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<ClientWrapper> call, Throwable t) {
                errorImage.setImageResource(R.drawable.ic_baseline_error_outline_24);
                errorText.setText(t.toString());
                progressDialog.hideDialog();
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
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
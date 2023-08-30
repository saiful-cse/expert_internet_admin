package com.creativesaif.expert_internet_admin.Search;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.creativesaif.expert_internet_admin.Adapter.SearchAdapter;
import com.creativesaif.expert_internet_admin.Login;
import com.creativesaif.expert_internet_admin.Model.Client;
import com.creativesaif.expert_internet_admin.Model.ClientWrapper;
import com.creativesaif.expert_internet_admin.Network.ApiInterface;
import com.creativesaif.expert_internet_admin.Network.RetrofitApiClient;
import com.creativesaif.expert_internet_admin.ProgressDialog;
import com.creativesaif.expert_internet_admin.R;

import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;

public class Search_Page extends AppCompatActivity {


    ProgressDialog progressDialog;

    // creating variables for
    // our ui components.
    private RecyclerView recyclerView;

    Context context;
    private Client client;
    private SharedPreferences preferences;

    // variable for our adapter
    // class and array list
    private SearchAdapter searchAdapter;
    private ArrayList<Client> clientArrayList;

    private ApiInterface apiInterface;
    private ImageView errorImage;
    private TextView errorText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        errorImage = findViewById(R.id.error_icon);
        errorText = findViewById(R.id.error_text);
        preferences = this.getSharedPreferences("users", MODE_PRIVATE);
        progressDialog = new ProgressDialog(this);
        clientArrayList = new ArrayList<>();
        setupRecyclerView();

        apiInterface = RetrofitApiClient.getClient().create(ApiInterface.class);
        client = new Client();

        if (!isNetworkConnected()) {
            Toast.makeText(getApplicationContext(), "Please!! Check internet connection.", Toast.LENGTH_SHORT).show();
        }else{
            client.setJwt(preferences.getString("jwt", null));
            load_client(client);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // below line is to get our inflater
        MenuInflater inflater = getMenuInflater();

        // inside inflater we are inflating our menu file.
        inflater.inflate(R.menu.search_menu, menu);

        // below line is to get our menu item.
        MenuItem searchItem = menu.findItem(R.id.action_search);

        // getting search view of our item.
        SearchView searchView = (SearchView) searchItem.getActionView();

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newtext) {
                searchAdapter.getFilter().filter(newtext.trim());
                return false;
            }
        });
        return true;
    }


    private void setupRecyclerView(){
        recyclerView = findViewById(R.id.searchRecyclerView);
        recyclerView.setHasFixedSize(true);
        searchAdapter = new SearchAdapter(this, clientArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(searchAdapter);
    }

    public void load_client(Client mClient) {

        progressDialog.showDialog();
        Call<ClientWrapper> call = apiInterface.search_data(mClient);
        call.enqueue(new Callback<ClientWrapper>() {
            @Override
            public void onResponse(Call<ClientWrapper> call, retrofit2.Response<ClientWrapper> response) {

                progressDialog.hideDialog();
                clientArrayList.clear();

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
                    searchAdapter.setClientList(clientWrapper.getClients());

                } else if (clientWrapper.getStatus() == 401) {
                    //Go to phone verification step
                    Toast.makeText(getApplicationContext(), clientWrapper.getMessage(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Search_Page.this, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
                else {
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
package com.creativesaif.expert_internet_admin.Search;

import android.app.SearchManager;
import android.content.Context;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creativesaif.expert_internet_admin.ClientList.Client;
import com.creativesaif.expert_internet_admin.ClientList.ClientAdapter;
import com.creativesaif.expert_internet_admin.MySingleton;
import com.creativesaif.expert_internet_admin.ProgressDialog;
import com.creativesaif.expert_internet_admin.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchPage extends AppCompatActivity {

    ProgressDialog progressDialog;
    RecyclerView recyclerView;
    Context context;

    private ClientAdapter clientAdapter;
    private ArrayList<Client> clientArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        clientArrayList = new ArrayList<>();
        clientAdapter = new ClientAdapter(this, clientArrayList);

        recyclerView = findViewById(R.id.searchRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(clientAdapter);
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

                if(isNetworkConnected()){

                    loadClients(query);
                    //Toast.makeText(getApplicationContext(),"You search: "+query,Toast.LENGTH_LONG).show();
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

    private void loadClients(String query) {

        progressDialog.showDialog();
        String url = getString(R.string.base_url)+getString(R.string.search)+query.trim();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
                progressDialog.hideDialog();
                try{

                    clientArrayList.clear();
                    JSONObject jsonObject = new JSONObject(response);

                    boolean m = jsonObject.has("message");
                    if (m)
                    {
                        String message = jsonObject.getString("message");
                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

                    }else
                    {
                        JSONArray jsonArray = jsonObject.getJSONArray("search_data");
                        //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();

                        for (int i=0; i<=jsonArray.length(); i++)
                        {
                            Client client = new Client();

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            client.setId(jsonObject1.getString("id"));
                            client.setName(jsonObject1.getString("name"));
                            client.setPhone(jsonObject1.getString("phone"));
                            client.setArea(jsonObject1.getString("area"));
                            client.setUsername(jsonObject1.getString("username"));
                            client.setPayment_method(jsonObject1.getString("payment_method"));

                            clientArrayList.add(client);

                            clientAdapter.notifyDataSetChanged();
                        }

                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideDialog();
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
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

    //Internet connection check
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
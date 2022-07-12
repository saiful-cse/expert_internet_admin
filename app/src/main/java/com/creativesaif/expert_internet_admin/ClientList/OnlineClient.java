package com.creativesaif.expert_internet_admin.ClientList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.creativesaif.expert_internet_admin.Adapter.ClientAdapter;
import com.creativesaif.expert_internet_admin.Login;
import com.creativesaif.expert_internet_admin.Model.Client;
import com.creativesaif.expert_internet_admin.Model.ClientWrapper;
import com.creativesaif.expert_internet_admin.Network.ApiInterface;
import com.creativesaif.expert_internet_admin.Network.RetrofitApiClient;
import com.creativesaif.expert_internet_admin.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;

import static android.content.Context.MODE_PRIVATE;

public class OnlineClient extends Fragment {

    private ClientAdapter clientAdapter;
    private List<Client> clientList;
    private ApiInterface apiInterface;
    private String jwt;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Client client;
    private ImageView errorImage;
    private TextView errorText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.activity_client_view, container, false);

        errorImage = view.findViewById(R.id.error_icon);
        errorText = view.findViewById(R.id.error_text);
        SharedPreferences preferences = view.getContext().getSharedPreferences("users", MODE_PRIVATE);
        swipeRefreshLayout = view.findViewById(R.id.layout_refresh);
        clientList = new ArrayList<>();
        clientAdapter = new ClientAdapter(getActivity(), clientList);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewClient);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(clientAdapter);

        apiInterface = RetrofitApiClient.getClient().create(ApiInterface.class);
        client = new Client();

        jwt = preferences.getString("jwt", null);

        //reload or refresh posts
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (jwt == null) {
                    //Go to phone verification step
                    Toast.makeText(getContext(), "Session expired!!", Toast.LENGTH_LONG).show();
                    getActivity().finish();
                    Intent intent = new Intent(getActivity(), Login.class);
                    getActivity().startActivity(intent);

                } else if (!isConnected()) {
                    Toast.makeText(getContext(), "Please!! Check internet connection.", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);

                } else {
                    //sent value on client model class
                    client.setJwt(jwt);

                    //Network call to load client list
                    load_client(client);
                }
            }
        });

        return view;
    }

    public void load_client(Client mClient) {

        Call<ClientWrapper> call = apiInterface.getOnline_client(mClient);
        call.enqueue(new Callback<ClientWrapper>() {
            @Override
            public void onResponse(Call<ClientWrapper> call, retrofit2.Response<ClientWrapper> response) {

                swipeRefreshLayout.setRefreshing(false);
                clientList.clear();

                ClientWrapper clientWrapper = response.body();
                assert clientWrapper != null;

                if (clientWrapper.getStatus() == 401) {
                    //Go to phone verification step
                    Toast.makeText(getActivity(), clientWrapper.getMessage(), Toast.LENGTH_LONG).show();
                    getActivity().finish();
                    Intent intent = new Intent(getActivity(), Login.class);
                    startActivity(intent);

                }else if (clientWrapper.getStatus() == 404) {
                    //client not found then visible error
                    errorImage.setImageResource(R.drawable.ic_baseline_client_24);
                    errorText.setText(clientWrapper.getMessage());

                }else if (clientWrapper.getStatus() == 200) {
                    errorImage.setVisibility(View.GONE);
                    errorText.setVisibility(View.GONE);
                    clientAdapter.setClientList(clientWrapper.getClients());

                }else {
                    Toast.makeText(getActivity(), clientWrapper.getMessage(), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<ClientWrapper> call, Throwable t) {
                errorImage.setImageResource(R.drawable.ic_baseline_error_outline_24);
                errorText.setText(t.toString());
                swipeRefreshLayout.setRefreshing(false);

            }
        });
    }

    //Internet connection check
    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

}

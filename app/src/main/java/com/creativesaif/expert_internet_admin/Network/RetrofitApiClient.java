package com.creativesaif.expert_internet_admin.Network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitApiClient
{
    private static final String BASE_URL = "http://192.168.1.3:8012/";
    private static Retrofit retrofit = null;


    private static Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    private RetrofitApiClient()
    {

    }

    public static Retrofit getClient()
    {
        if (retrofit == null)
        {
            synchronized (RetrofitApiClient.class)
            {
                //thread sfe single tone implementation
                if (retrofit == null)
                {
                    retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();
                }
            }
        }
        return retrofit;
    }
}

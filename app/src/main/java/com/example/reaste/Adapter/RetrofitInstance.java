package com.example.reaste.Adapter;

import com.example.reaste.Model.RetrofitApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    public static final String BASE_URL= "https://reastewebapp.azurewebsites.net";
    private static RetrofitInstance instance;
    private Retrofit retrofit;

    private RetrofitInstance(){
        retrofit=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitInstance getInstance(){
        if (instance == null){
            instance=new RetrofitInstance();
        }
        return instance;
    }
    public RetrofitApi api(){
        return retrofit.create(RetrofitApi.class);
    }
}

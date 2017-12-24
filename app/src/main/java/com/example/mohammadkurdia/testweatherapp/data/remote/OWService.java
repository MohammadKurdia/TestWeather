package com.example.mohammadkurdia.testweatherapp.data.remote;

import com.example.mohammadkurdia.testweatherapp.data.model.OWResponse;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by David on 07/02/2017.
 */

public interface OWService {
    @GET("2.5/forecast?cnt=16&units=metric")
    Call<OWResponse> getResponse(@Query("q") String city, @Query("APPID") String apiKey);
    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://api.openweathermap.org/data/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}


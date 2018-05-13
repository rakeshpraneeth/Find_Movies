package com.krp.findmovies.common;

import com.krp.findmovies.interfaces.FmApiService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Rakesh Praneeth.
 * This class contains the basic url that the project is handling for each API.
 * It contains a method that returns Retrofit object.
 * It contains a static method to create FmApiService using Retrofit.
 */
public final class BaseUrl {

    private BaseUrl(){

    }

    // It's value cannot be accessed and changed in the project.
    private static final String BASE_URL = "https://api.themoviedb.org/";

    // This method is used to build a Retrofit object using Base Url and convert factory.
    private static Retrofit getRetrofit(){

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    // Creates an ApiService.
    public static FmApiService getFmApiService(){
        return BaseUrl.getRetrofit().create(FmApiService.class);
    }
}

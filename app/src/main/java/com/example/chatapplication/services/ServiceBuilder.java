package com.example.chatapplication.services;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceBuilder {

    private static final String Url = "https://chat.promactinfo.com/api/";

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(Url)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder.build();

    public static <S> S createService(Class<S> serviceType) {
       //Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceType);
    }
}

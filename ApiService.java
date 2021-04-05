package com.example.twitterchallenge.network;

import com.example.twitterchallenge.data.WeatherStatus;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {

    @GET("/current.json")
    Call<WeatherStatus> getCurrentTemperature();

    @GET("/future_{day}.json")
    Call<WeatherStatus> getTemperatureFor(@Path("day") String day);


}

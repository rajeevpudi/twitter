package com.example.twitterchallenge;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.twitterchallenge.data.WeatherStatus;
import com.example.twitterchallenge.network.ApiService;
import com.example.twitterchallenge.network.NetworkUtils;
import com.example.twitterchallenge.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    TextView temperatureView;
    TextView windSpeed;
    TextView standardDeviation;
    ImageView cloudImage;
    LinearLayout sdLayout;

    ApiService service;
    WeatherStatus weatherStatus;
    public static final String TEMPERATURE = "temperature";
    public static final String SPEED = "speed";
    int dayCount = 1;
    List<WeatherStatus> weatherStatusesList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        temperatureView = (TextView) findViewById(R.id.temperature);
        windSpeed = (TextView) findViewById(R.id.wind_speed);
        standardDeviation = (TextView) findViewById(R.id.standard_deviation);
        cloudImage = (ImageView) findViewById(R.id.cloud_img);
        sdLayout = findViewById(R.id.sd_layout);

        //initiates the progress dialog
        initProgressDialog();

        //initiates retrofit client to fetch data from service
        initRetrofit();


        if(savedInstanceState != null && savedInstanceState.getString(TEMPERATURE) != null && savedInstanceState.getString(SPEED) != null){
            loadUiFromBundle(savedInstanceState);
        }else {
            if(NetworkUtils.isOnline(MainActivity.this)){
                //get current weather condition for service current.json
                fetchCurrentTemperature();
            }else{
                // display toast for offline scenario
                Toast.makeText(MainActivity.this, "No network is available, please try again once available", Toast.LENGTH_LONG).show();
            }

        }
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading....");
    }

    private void initRetrofit() {
        service = RetrofitClient.getRetrofitInstance().create(ApiService.class);
    }

    private void fetchTemperatureForNext5Days( ) {
        showProgressDialog();

        Call<WeatherStatus> temperature = service.getTemperatureFor(String.valueOf(dayCount));
        temperature.enqueue(new Callback<WeatherStatus>() {
            @Override
            public void onResponse(Call<WeatherStatus> call, Response<WeatherStatus> response) {
                hideProgressDialog();
                WeatherStatus weatherStatus = response.body();
                weatherStatusesList.add(weatherStatus);
                dayCount += 1;
                if(dayCount <= 5){
                    fetchTemperatureForNext5Days();// recursively calling the service to fetch data for 5 days
                }else {
                    calculateStandardDeviation();// all 5 days data is available now
                }
            }

            @Override
            public void onFailure(Call<WeatherStatus> call, Throwable t) {
                hideProgressDialog();
                Toast.makeText(MainActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateStandardDeviation() {
        float totalTemperature = 0f;
        for (WeatherStatus weatherStatus: weatherStatusesList){
            if(weatherStatus != null){
                Log.e("all temp: ",weatherStatus.getWeather().getTemp());
                totalTemperature += Float.parseFloat(weatherStatus.getWeather().getTemp());
            }
        }

        //calculates the mean for all temperatures
        double mean = totalTemperature / weatherStatusesList.size();

        //taking diffrebnce and soing squares of all temperatures
        List<Double> weatherStatuses = new ArrayList<>();
        for (WeatherStatus weatherStatus: weatherStatusesList){
            if(weatherStatus != null){
                double value = Math.pow((Float.parseFloat(weatherStatus.getWeather().getTemp())-mean),2);
                weatherStatuses.add(value);
            }
        }
        if(weatherStatuses.size() > 0){
            //calculating sum
            double total = 0.0f;
            for (double weatherStatus: weatherStatuses){
                total += weatherStatus;
            }

            //calcuating the standard deviation
            double standardDeviationText = Math.sqrt(total / weatherStatuses.size());

            //displaying the standard deviation
            standardDeviation.setText(String.valueOf(standardDeviationText));
            sdLayout.setVisibility(View.VISIBLE);
        }else{
            Toast.makeText(MainActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchCurrentTemperature() {
        showProgressDialog();

        Call<WeatherStatus> currentTemperature = service.getCurrentTemperature();
        currentTemperature.enqueue(new Callback<WeatherStatus>() {
            @Override
            public void onResponse(Call<WeatherStatus> call, Response<WeatherStatus> response) {
                hideProgressDialog();
                weatherStatus = response.body();
                renderUI(weatherStatus.getWeather().getTemp(), weatherStatus.getWind().getSpeed());
            }

            @Override
            public void onFailure(Call<WeatherStatus> call, Throwable t) {
                hideProgressDialog();
                Toast.makeText(MainActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void renderUI(String temperature, String  speed) {
        if(temperature != null){
            float temperatureFromService = Float.parseFloat(temperature);
            temperatureView.setText(getString(R.string.temperature, temperatureFromService, TemperatureConverter.celsiusToFahrenheit(temperatureFromService)));
        }

        if(speed != null){
            windSpeed.setText(speed);
            if(Float.parseFloat(speed) * 100 > 50){
                cloudImage.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        Log.e("onSaveInstanceState","onSaveInstanceState");
        if(weatherStatus != null){
            Log.e(TEMPERATURE, weatherStatus.getWeather().getTemp());
            Log.e(SPEED, weatherStatus.getWind().getSpeed());
            outState.putString(TEMPERATURE,weatherStatus.getWeather().getTemp());
            outState.putString(SPEED,weatherStatus.getWind().getSpeed());
        }
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null && savedInstanceState.getString(TEMPERATURE) != null && savedInstanceState.getString(SPEED) != null)
            loadUiFromBundle(savedInstanceState);

    }

    private void loadUiFromBundle(Bundle savedInstanceState) {
        String temperature = null;
        String speed = null;
        if(savedInstanceState.getString(TEMPERATURE) != null){
            temperature = savedInstanceState.getString(TEMPERATURE);
            Log.e(TEMPERATURE, temperature);
        }
        if(savedInstanceState.getString(SPEED) != null){
            speed = savedInstanceState.getString(SPEED);
            Log.e(SPEED, speed);
        }


        renderUI(temperature, speed);
    }

    private void hideProgressDialog() {
        progressDialog.dismiss();
    }

    private void showProgressDialog() {
        progressDialog.show();
    }

    public void getStandardDeviation(View view) {
        if(NetworkUtils.isOnline(MainActivity.this)){
            // get current weather condition for next 5 days
            fetchTemperatureForNext5Days();
        }else{
            // display toast for offline scenario
            Toast.makeText(MainActivity.this, "No network is available, please try again once available", Toast.LENGTH_LONG).show();
        }
    }
}
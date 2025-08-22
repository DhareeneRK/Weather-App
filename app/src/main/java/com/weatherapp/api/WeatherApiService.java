package com.weatherapp.api;

import com.weatherapp.model.AirQualityData;
import com.weatherapp.model.WeatherData;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApiService {
    // Get your free API key from https://openweathermap.org/api
    String API_KEY = "183f2d6691486291b2a133aba1ffe60d"; // Replace with your actual API key
    String BASE_URL = "https://api.openweathermap.org/data/2.5/";

    @GET("weather")
    Call<WeatherData> getCurrentWeather(
            @Query("lat") double latitude,
            @Query("lon") double longitude,
            @Query("appid") String apiKey,
            @Query("units") String units
    );

    @GET("weather")
    Call<WeatherData> getCurrentWeatherByCity(
            @Query("q") String cityName,
            @Query("appid") String apiKey,
            @Query("units") String units
    );

    @GET("air_pollution")
    Call<AirQualityData> getAirQuality(
            @Query("lat") double latitude,
            @Query("lon") double longitude,
            @Query("appid") String apiKey
    );
}
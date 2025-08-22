package com.weatherapp.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WeatherData {
    @SerializedName("name")
    private String cityName;

    @SerializedName("main")
    private Main main;

    @SerializedName("weather")
    private List<Weather> weather;

    @SerializedName("wind")
    private Wind wind;

    @SerializedName("visibility")
    private int visibility;

    @SerializedName("sys")
    private Sys sys;

    @SerializedName("dt")
    private long dateTime;

    @SerializedName("coord")
    private Coord coord;

    // Getters
    public String getCityName() { return cityName; }
    public Main getMain() { return main; }
    public List<Weather> getWeather() { return weather; }
    public Wind getWind() { return wind; }
    public int getVisibility() { return visibility; }
    public Sys getSys() { return sys; }
    public long getDateTime() { return dateTime; }
    public Coord getCoord() { return coord; }

    public static class Main {
        @SerializedName("temp")
        private double temperature;

        @SerializedName("feels_like")
        private double feelsLike;

        @SerializedName("temp_min")
        private double tempMin;

        @SerializedName("temp_max")
        private double tempMax;

        @SerializedName("pressure")
        private int pressure;

        @SerializedName("humidity")
        private int humidity;

        // Getters
        public double getTemperature() { return temperature; }
        public double getFeelsLike() { return feelsLike; }
        public double getTempMin() { return tempMin; }
        public double getTempMax() { return tempMax; }
        public int getPressure() { return pressure; }
        public int getHumidity() { return humidity; }
    }

    public static class Weather {
        @SerializedName("main")
        private String main;

        @SerializedName("description")
        private String description;

        @SerializedName("icon")
        private String icon;

        // Getters
        public String getMain() { return main; }
        public String getDescription() { return description; }
        public String getIcon() { return icon; }
    }

    public static class Wind {
        @SerializedName("speed")
        private double speed;

        @SerializedName("deg")
        private int degree;

        // Getters
        public double getSpeed() { return speed; }
        public int getDegree() { return degree; }
    }

    public static class Sys {
        @SerializedName("sunrise")
        private long sunrise;

        @SerializedName("sunset")
        private long sunset;

        @SerializedName("country")
        private String country;

        // Getters
        public long getSunrise() { return sunrise; }
        public long getSunset() { return sunset; }
        public String getCountry() { return country; }
    }

    public static class Coord {
        @SerializedName("lat")
        private double latitude;

        @SerializedName("lon")
        private double longitude;

        // Getters
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
    }
}
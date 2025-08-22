package com.weatherapp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WeatherUtils {

    public static String formatTemperature(double temperature) {
        return Math.round(temperature) + "°C";
    }

    public static String formatTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp * 1000));
    }

    public static String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM dd", Locale.getDefault());
        return sdf.format(new Date(timestamp * 1000));
    }

    public static String getWindDirection(int degree) {
        String[] directions = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE",
                "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"};
        return directions[(int) Math.round(((degree % 360) / 22.5))];
    }

    public static String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    public static String getWeatherIconUrl(String iconCode) {
        return "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
    }

    public static int getWeatherBackground(String weatherMain) {
        switch (weatherMain.toLowerCase()) {
            case "clear":
                return android.R.color.holo_blue_light;
            case "clouds":
                return android.R.color.darker_gray;
            case "rain":
            case "drizzle":
                return android.R.color.holo_blue_dark;
            case "thunderstorm":
                return android.R.color.black;
            case "snow":
                return android.R.color.white;
            case "mist":
            case "fog":
                return android.R.color.darker_gray;
            default:
                return android.R.color.holo_blue_light;
        }
    }
}
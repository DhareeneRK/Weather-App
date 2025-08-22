package com.weatherapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedPreferencesHelper {
    private static final String PREF_NAME = "WeatherAppPrefs";
    private static final String KEY_SAVED_LOCATIONS = "saved_locations";
    private static final String KEY_LAST_LOCATION = "last_location";
    private static final String KEY_TEMPERATURE_UNIT = "temperature_unit";

    private SharedPreferences sharedPreferences;
    private Gson gson;

    public SharedPreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveLocations(List<String> locations) {
        String json = gson.toJson(locations);
        sharedPreferences.edit().putString(KEY_SAVED_LOCATIONS, json).apply();
    }

    public List<String> getSavedLocations() {
        String json = sharedPreferences.getString(KEY_SAVED_LOCATIONS, null);
        if (json != null) {
            Type type = new TypeToken<List<String>>() {}.getType();
            return gson.fromJson(json, type);
        }
        return new ArrayList<>();
    }

    public void saveLastLocation(String location) {
        sharedPreferences.edit().putString(KEY_LAST_LOCATION, location).apply();
    }

    public String getLastLocation() {
        return sharedPreferences.getString(KEY_LAST_LOCATION, null);
    }

    public void saveTemperatureUnit(String unit) {
        sharedPreferences.edit().putString(KEY_TEMPERATURE_UNIT, unit).apply();
    }

    public String getTemperatureUnit() {
        return sharedPreferences.getString(KEY_TEMPERATURE_UNIT, "metric");
    }

    public void clearAll() {
        sharedPreferences.edit().clear().apply();
    }
}
package com.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.weatherapp.api.ApiClient;
import com.weatherapp.api.WeatherApiService;
import com.weatherapp.databinding.ActivityAddLocationBinding;
import com.weatherapp.model.WeatherData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddLocationActivity extends AppCompatActivity {
    private static final String TAG = "AddLocationActivity";
    private ActivityAddLocationBinding binding;
    private WeatherApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            binding = ActivityAddLocationBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            setupToolbar();
            initializeComponents();
            setupClickListeners();

            Log.d(TAG, "AddLocationActivity created successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading screen", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupToolbar() {
        try {
            setSupportActionBar(binding.toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Add Location");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up toolbar: " + e.getMessage(), e);
        }
    }

    private void initializeComponents() {
        try {
            apiService = ApiClient.getWeatherApiService();
            if (apiService == null) {
                Log.e(TAG, "Failed to initialize API service");
                Toast.makeText(this, "Network service unavailable", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing components: " + e.getMessage(), e);
        }
    }

    private void setupClickListeners() {
        try {
            binding.btnAddLocation.setOnClickListener(v -> {
                String location = binding.editTextLocation.getText().toString().trim();
                if (TextUtils.isEmpty(location)) {
                    binding.editTextLocation.setError("Please enter a location");
                    return;
                }
                validateAndAddLocation(location);
            });

            binding.btnCancel.setOnClickListener(v -> finish());
        } catch (Exception e) {
            Log.e(TAG, "Error setting up click listeners: " + e.getMessage(), e);
        }
    }

    private void validateAndAddLocation(String location) {
        if (apiService == null) {
            Toast.makeText(this, "Network service not available", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnAddLocation.setEnabled(false);
        binding.progressBar.setVisibility(android.view.View.VISIBLE);

        try {
            // Check if API_KEY is available
            String apiKey = WeatherApiService.API_KEY;
            if (TextUtils.isEmpty(apiKey)) {
                Log.e(TAG, "API key is missing");
                binding.btnAddLocation.setEnabled(true);
                binding.progressBar.setVisibility(android.view.View.GONE);
                Toast.makeText(this, "Configuration error. Please check API setup.", Toast.LENGTH_LONG).show();
                return;
            }

            // Validate location by making API call
            apiService.getCurrentWeatherByCity(location, apiKey, "metric")
                    .enqueue(new Callback<WeatherData>() {
                        @Override
                        public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                            binding.btnAddLocation.setEnabled(true);
                            binding.progressBar.setVisibility(android.view.View.GONE);

                            if (response.isSuccessful() && response.body() != null) {
                                // Location is valid, return it to main activity
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("location", response.body().getCityName());
                                setResult(RESULT_OK, resultIntent);
                                Toast.makeText(AddLocationActivity.this, "Location added successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Log.e(TAG, "API response error: " + response.code() + " - " + response.message());
                                Toast.makeText(AddLocationActivity.this, "Location not found. Please check the spelling.", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<WeatherData> call, Throwable t) {
                            binding.btnAddLocation.setEnabled(true);
                            binding.progressBar.setVisibility(android.view.View.GONE);
                            Log.e(TAG, "Network error: " + t.getMessage(), t);
                            Toast.makeText(AddLocationActivity.this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            binding.btnAddLocation.setEnabled(true);
            binding.progressBar.setVisibility(android.view.View.GONE);
            Log.e(TAG, "Error making API call: " + e.getMessage(), e);
            Toast.makeText(this, "Error connecting to weather service", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
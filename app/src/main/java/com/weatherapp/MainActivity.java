package com.weatherapp;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.weatherapp.adapter.LocationAdapter;
import com.weatherapp.api.ApiClient;
import com.weatherapp.api.WeatherApiService;
import com.weatherapp.databinding.ActivityMainBinding;
import com.weatherapp.model.AirQualityData;
import com.weatherapp.model.WeatherData;
import com.weatherapp.utils.LocationHelper;
import com.weatherapp.utils.WeatherUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationHelper.LocationListener {
    private ActivityMainBinding binding;
    private LocationHelper locationHelper;
    private WeatherApiService apiService;
    private LocationAdapter locationAdapter;
    private List<String> savedLocations;
    private boolean isCurrentLocationLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeComponents();
        setupRecyclerView();
        setupClickListeners();
        requestLocationPermissions();
    }

    private void initializeComponents() {
        locationHelper = new LocationHelper(this);
        apiService = ApiClient.getWeatherApiService();
        savedLocations = new ArrayList<>();

        // Add some default locations for demonstration
        savedLocations.add("Chennai");
        savedLocations.add("Mumbai");
        savedLocations.add("Delhi");
    }

    private void setupRecyclerView() {
        locationAdapter = new LocationAdapter(savedLocations, new LocationAdapter.OnLocationClickListener() {
            @Override
            public void onLocationClick(String location) {
                loadWeatherForCity(location);
            }

            @Override
            public void onLocationDelete(String location) {
                savedLocations.remove(location);
                locationAdapter.notifyDataSetChanged();
            }
        });

        binding.recyclerViewLocations.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewLocations.setAdapter(locationAdapter);
    }

    private void setupClickListeners() {
        binding.fabAddLocation.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddLocationActivity.class);
            startActivityForResult(intent, 100);
        });

        binding.swipeRefresh.setOnRefreshListener(() -> {
            if (isCurrentLocationLoaded) {
                getCurrentLocation();
            } else {
                binding.swipeRefresh.setRefreshing(false);
            }
        });

        binding.btnCurrentLocation.setOnClickListener(v -> getCurrentLocation());
    }

    private void requestLocationPermissions() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            getCurrentLocation();
                        } else {
                            Toast.makeText(MainActivity.this, "Location permissions required", Toast.LENGTH_SHORT).show();
                            // Load default city weather
                            loadWeatherForCity("Chennai");
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void getCurrentLocation() {
        showLoading(true);
        locationHelper.getCurrentLocation(this);
    }

    @Override
    public void onLocationReceived(double latitude, double longitude) {
        isCurrentLocationLoaded = true;
        loadWeatherData(latitude, longitude);
        loadAirQualityData(latitude, longitude);
    }

    @Override
    public void onLocationError(String error) {
        showLoading(false);
        Toast.makeText(this, "Error getting location: " + error, Toast.LENGTH_SHORT).show();
        // Load default city weather
        loadWeatherForCity("Chennai");
    }

    private void loadWeatherData(double latitude, double longitude) {
        apiService.getCurrentWeather(latitude, longitude, WeatherApiService.API_KEY, "metric")
                .enqueue(new Callback<WeatherData>() {
                    @Override
                    public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                        showLoading(false);
                        if (response.isSuccessful() && response.body() != null) {
                            updateWeatherUI(response.body());
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to load weather data", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherData> call, Throwable t) {
                        showLoading(false);
                        Toast.makeText(MainActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadWeatherForCity(String cityName) {
        showLoading(true);
        apiService.getCurrentWeatherByCity(cityName, WeatherApiService.API_KEY, "metric")
                .enqueue(new Callback<WeatherData>() {
                    @Override
                    public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                        showLoading(false);
                        if (response.isSuccessful() && response.body() != null) {
                            WeatherData weatherData = response.body();
                            updateWeatherUI(weatherData);
                            // Load AQI data for this city
                            loadAirQualityData(weatherData.getCoord().getLatitude(), weatherData.getCoord().getLongitude());
                        } else {
                            Toast.makeText(MainActivity.this, "City not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherData> call, Throwable t) {
                        showLoading(false);
                        Toast.makeText(MainActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadAirQualityData(double latitude, double longitude) {
        apiService.getAirQuality(latitude, longitude, WeatherApiService.API_KEY)
                .enqueue(new Callback<AirQualityData>() {
                    @Override
                    public void onResponse(Call<AirQualityData> call, Response<AirQualityData> response) {
                        if (response.isSuccessful() && response.body() != null &&
                                !response.body().getList().isEmpty()) {
                            updateAirQualityUI(response.body().getList().get(0));
                        }
                    }

                    @Override
                    public void onFailure(Call<AirQualityData> call, Throwable t) {
                        // AQI data is optional, don't show error
                    }
                });
    }

    private void updateWeatherUI(WeatherData weatherData) {
        binding.textCityName.setText(weatherData.getCityName());
        binding.textTemperature.setText(WeatherUtils.formatTemperature(weatherData.getMain().getTemperature()));
        binding.textWeatherDescription.setText(WeatherUtils.capitalizeFirst(weatherData.getWeather().get(0).getDescription()));
        binding.textDateTime.setText(WeatherUtils.formatDate(weatherData.getDateTime()));

        // Weather details
        binding.textFeelsLike.setText("Feels like " + WeatherUtils.formatTemperature(weatherData.getMain().getFeelsLike()));
        binding.textHumidity.setText(weatherData.getMain().getHumidity() + "%");
        binding.textPressure.setText(weatherData.getMain().getPressure() + " hPa");
        binding.textWindSpeed.setText(String.format("%.1f m/s", weatherData.getWind().getSpeed()));
        binding.textWindDirection.setText(WeatherUtils.getWindDirection(weatherData.getWind().getDegree()));
        binding.textVisibility.setText(String.format("%.1f km", weatherData.getVisibility() / 1000.0));
        binding.textSunrise.setText(WeatherUtils.formatTime(weatherData.getSys().getSunrise()));
        binding.textSunset.setText(WeatherUtils.formatTime(weatherData.getSys().getSunset()));

        // Load weather icon
        String iconUrl = WeatherUtils.getWeatherIconUrl(weatherData.getWeather().get(0).getIcon());
        Glide.with(this)
                .load(iconUrl)
                .into(binding.imageWeatherIcon);

        binding.layoutWeatherDetails.setVisibility(View.VISIBLE);
    }

    private void updateAirQualityUI(AirQualityData.AirQuality airQuality) {
        binding.textAqiValue.setText(String.valueOf(airQuality.getMain().getAqi()));
        binding.textAqiDescription.setText(airQuality.getMain().getAqiDescription());
        binding.cardAqi.setCardBackgroundColor(airQuality.getMain().getAqiColor());

        // Update air quality components
        binding.textPm25.setText(String.format("%.1f μg/m³", airQuality.getComponents().getPm25()));
        binding.textPm10.setText(String.format("%.1f μg/m³", airQuality.getComponents().getPm10()));
        binding.textCo.setText(String.format("%.1f μg/m³", airQuality.getComponents().getCo()));
        binding.textNo2.setText(String.format("%.1f μg/m³", airQuality.getComponents().getNo2()));
        binding.textO3.setText(String.format("%.1f μg/m³", airQuality.getComponents().getO3()));
        binding.textSo2.setText(String.format("%.1f μg/m³", airQuality.getComponents().getSo2()));

        binding.layoutAirQuality.setVisibility(View.VISIBLE);
    }

    private void showLoading(boolean show) {
        binding.swipeRefresh.setRefreshing(show);
        if (show) {
            binding.layoutWeatherDetails.setVisibility(View.GONE);
            binding.layoutAirQuality.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            String newLocation = data.getStringExtra("location");
            if (newLocation != null && !savedLocations.contains(newLocation)) {
                savedLocations.add(newLocation);
                locationAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationHelper != null) {
            locationHelper.stopLocationUpdates();
        }
    }
}
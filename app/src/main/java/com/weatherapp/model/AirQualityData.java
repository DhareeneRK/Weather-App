package com.weatherapp.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AirQualityData {
    @SerializedName("list")
    private List<AirQuality> list;

    public List<AirQuality> getList() { return list; }

    public static class AirQuality {
        @SerializedName("main")
        private Main main;

        @SerializedName("components")
        private Components components;

        public Main getMain() { return main; }
        public Components getComponents() { return components; }

        public static class Main {
            @SerializedName("aqi")
            private int aqi;

            public int getAqi() { return aqi; }

            public String getAqiDescription() {
                switch (aqi) {
                    case 1: return "Good";
                    case 2: return "Fair";
                    case 3: return "Moderate";
                    case 4: return "Poor";
                    case 5: return "Very Poor";
                    default: return "Unknown";
                }
            }

            public int getAqiColor() {
                switch (aqi) {
                    case 1: return 0xFF4CAF50; // Green
                    case 2: return 0xFF8BC34A; // Light Green
                    case 3: return 0xFFFFC107; // Yellow
                    case 4: return 0xFFFF9800; // Orange
                    case 5: return 0xFFF44336; // Red
                    default: return 0xFF9E9E9E; // Gray
                }
            }
        }

        public static class Components {
            @SerializedName("co")
            private double co;

            @SerializedName("no")
            private double no;

            @SerializedName("no2")
            private double no2;

            @SerializedName("o3")
            private double o3;

            @SerializedName("so2")
            private double so2;

            @SerializedName("pm2_5")
            private double pm25;

            @SerializedName("pm10")
            private double pm10;

            @SerializedName("nh3")
            private double nh3;

            // Getters
            public double getCo() { return co; }
            public double getNo() { return no; }
            public double getNo2() { return no2; }
            public double getO3() { return o3; }
            public double getSo2() { return so2; }
            public double getPm25() { return pm25; }
            public double getPm10() { return pm10; }
            public double getNh3() { return nh3; }
        }
    }
}
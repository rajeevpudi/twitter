package com.example.twitterchallenge.data;

import com.google.gson.annotations.SerializedName;

public class WeatherStatus {
    @SerializedName("coord")
    Coordinates coordinates;

    @SerializedName("weather")
    Weather weather;

    @SerializedName("wind")
    Wind wind;

    @SerializedName("rain")
    Rain rain;

    @SerializedName("clouds")
    Clouds clouds;

    @SerializedName("name")
    String name;

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public Rain getRain() {
        return rain;
    }

    public void setRain(Rain rain) {
        this.rain = rain;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public void setClouds(Clouds clouds) {
        this.clouds = clouds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    class Clouds{
        @SerializedName("cloudiness")
        String cloudiness;

        public String getCloudiness() {
            return cloudiness;
        }

        public void setCloudiness(String cloudiness) {
            this.cloudiness = cloudiness;
        }
    }

    public class Wind{
        @SerializedName("speed")
        String speed;

        @SerializedName("deg")
        String  deg;

        public String getSpeed() {
            return speed;
        }

        public void setSpeed(String speed) {
            this.speed = speed;
        }

        public String getDeg() {
            return deg;
        }

        public void setDeg(String deg) {
            this.deg = deg;
        }
    }

    class Rain{

    }

    public class Weather{
        @SerializedName("temp")
        String temp;

        @SerializedName("pressure")
        String pressure;

        @SerializedName("humidity")
        String humidity;

        public String getTemp() {
            return temp;
        }

        public void setTemp(String temp) {
            this.temp = temp;
        }

        public String getPressure() {
            return pressure;
        }

        public void setPressure(String pressure) {
            this.pressure = pressure;
        }

        public String getHumidity() {
            return humidity;
        }

        public void setHumidity(String humidity) {
            this.humidity = humidity;
        }
    }

    class Coordinates{
        @SerializedName("lon")
        String lon;

        @SerializedName("lat")
        String lat;

        public String getLon() {
            return lon;
        }

        public void setLon(String lon) {
            this.lon = lon;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }
    }
}

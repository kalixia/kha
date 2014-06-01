package com.kalixia.ha.devices.weather;

import java.util.Locale;

public class WeatherRequest {
    private LocationType locationType;
    private Float[] coordinates;
    private String zipCode;
    private String city;
    private String state;
    private String country;
    private Locale locale;

    public WeatherRequest forCoordinates(Float coordinateX, Float coordinateY) {
        this.coordinates = new Float[]{coordinateX, coordinateY};
        this.locationType = LocationType.COORDINATES;
        return this;
    }

    public WeatherRequest forZipCode(String zipCode) {
        this.zipCode = zipCode;
        this.locationType = LocationType.ZIPCODE;
        return this;
    }

    public WeatherRequest forCityInState(String city, String state) {
        this.city = city;
        this.state = state;
        this.locationType = LocationType.CITY_STATE;
        return this;
    }

    public WeatherRequest forCityInCountry(String city, String country) {
        this.city = city;
        this.country = country;
        this.locationType = LocationType.CITY_COUNTRY;
        return this;
    }

    public WeatherRequest forLocalIP() {
        this.locationType = LocationType.AUTO_IP;
        return this;
    }

    public WeatherRequest withLocale(Locale locale) {
        this.locale = locale;
        return this;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public Float[] getCoordinates() {
        return coordinates;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public Locale getLocale() {
        return locale;
    }
}

package com.kalixia.ha.devices.weather;

import javax.measure.Measurable;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Velocity;

public class WeatherConditions {
    private Measurable<Temperature> temperature;
    private Measurable<Temperature> feelsLikeTemperature;
    private Measurable<Temperature> dewPoint;
    private Measurable<Dimensionless> relativeHumidity;
    private Measurable<Pressure> pressure;
    private Measurable<Velocity> windSpeed;
    private Measurable<Velocity> windGust;
    private Measurable<Angle> windDirection;
    private WeatherIcon icon;
    private String description;

    public Measurable<Temperature> getTemperature() {
        return temperature;
    }

    public void setTemperature(Measurable<Temperature> temperature) {
        this.temperature = temperature;
    }

    public Measurable<Temperature> getFeelsLikeTemperature() {
        return feelsLikeTemperature;
    }

    public void setFeelsLikeTemperature(Measurable<Temperature> feelsLikeTemperature) {
        this.feelsLikeTemperature = feelsLikeTemperature;
    }

    public Measurable<Temperature> getDewPoint() {
        return dewPoint;
    }

    public void setDewPoint(Measurable<Temperature> dewPoint) {
        this.dewPoint = dewPoint;
    }

    public Measurable<Dimensionless> getRelativeHumidity() {
        return relativeHumidity;
    }

    public void setRelativeHumidity(Measurable<Dimensionless> relativeHumidity) {
        this.relativeHumidity = relativeHumidity;
    }

    public Measurable<Pressure> getPressure() {
        return pressure;
    }

    public void setPressure(Measurable<Pressure> pressure) {
        this.pressure = pressure;
    }

    public Measurable<Velocity> getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Measurable<Velocity> windSpeed) {
        this.windSpeed = windSpeed;
    }

    public Measurable<Velocity> getWindGust() {
        return windGust;
    }

    public void setWindGust(Measurable<Velocity> windGust) {
        this.windGust = windGust;
    }

    public Measurable<Angle> getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(Measurable<Angle> windDirection) {
        this.windDirection = windDirection;
    }

    public WeatherIcon getIcon() {
        return icon;
    }

    public void setIcon(WeatherIcon icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WeatherConditions{");
        sb.append("temperature=").append(temperature);
        sb.append(", feelsLikeTemperature=").append(feelsLikeTemperature);
        sb.append(", dewPoint=").append(dewPoint);
        sb.append(", relativeHumidity=").append(relativeHumidity);
        sb.append(", pressure=").append(pressure);
        sb.append(", windSpeed=").append(windSpeed);
        sb.append(", windGust=").append(windGust);
        sb.append(", windDirection=").append(windDirection);
        sb.append(", icon=").append(icon);
        sb.append(", description=").append(description);
        sb.append('}');
        return sb.toString();
    }
}

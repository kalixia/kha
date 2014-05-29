package com.kalixia.ha.devices.weather;

import rx.Observable;

public interface WeatherService {
    Observable<WeatherConditions> getConditions(Observable<WeatherRequest> requests);
}

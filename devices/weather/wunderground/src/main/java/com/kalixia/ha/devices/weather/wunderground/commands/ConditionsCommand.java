package com.kalixia.ha.devices.weather.wunderground.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalixia.ha.devices.weather.WeatherConditions;
import com.kalixia.ha.devices.weather.WeatherIcon;
import com.kalixia.ha.devices.weather.WeatherRequest;
import com.kalixia.ha.devices.weather.wunderground.WundergroundDeviceConfiguration;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.client.HttpClient;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.exceptions.Exceptions;

import javax.measure.Measure;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import static javax.measure.unit.SI.PASCAL;

/**
 * Fetch weather conditions for a specific city.
 *
 * <a href="http://www.wunderground.com/weather/api/d/docs?d=data/conditions&MR=1">API Documentation</a>
 */
public class ConditionsCommand extends HystrixCommand<WeatherConditions> {
    private final WeatherRequest request;
    private final String apiKey;
    private final HttpClient<ByteBuf, ByteBuf> httpClient;
    private final ObjectMapper mapper;
    private static final Logger logger = LoggerFactory.getLogger(ConditionsCommand.class);

    public ConditionsCommand(WeatherRequest request,
                             WundergroundDeviceConfiguration configuration,
                             HttpClient<ByteBuf, ByteBuf> httpClient, ObjectMapper mapper) {
        super(Setter
                        .withGroupKey(HystrixCommandGroupKey.Factory.asKey("Wunderground"))
                        .andCommandKey(HystrixCommandKey.Factory.asKey("ConditionsCommand"))
        );
        this.request = request;
        this.apiKey = configuration.getApiKey();
        this.httpClient = httpClient;
        this.mapper = mapper;
    }

    @Override
    protected WeatherConditions run() throws Exception {
        String requestURL = "";

        // build request URL depending on the location type chosen
        switch (request.getLocationType()) {
            case COORDINATES:
                throw new IllegalArgumentException("COORDINATES is not a valid LocationType for Conditions requests");
            case ZIPCODE:
                String zipCode = request.getZipCode();
                requestURL = String.format("/api/%s/conditions/q/%s.json", apiKey, zipCode);
                logger.info("Fetching Wunderground conditions for '{}'...", zipCode);
                break;
            case AUTO_IP:
                throw new IllegalArgumentException("AUTO_IP is not a valid LocationType for Conditions requests");
            case CITY_STATE:
                String state = request.getState();
                String city = URLEncoder.encode(request.getCity(), "UTF-8");
                requestURL = String.format("/api/%s/conditions/q/%s/%s.json", apiKey, state, city);
                logger.info("Fetching Wunderground conditions for '{}', '{}'...", city, state);
                break;
            case CITY_COUNTRY:
                String country = URLEncoder.encode(request.getCountry(), "UTF-8");
                city = URLEncoder.encode(request.getCity(), "UTF-8");
                requestURL = String.format("/api/%s/conditions/q/%s/%s.json", apiKey, country, city);
                logger.info("Fetching Wunderground conditions for '{}', '{}'...", city, country);
                break;
        }

        logger.debug("Request is: {}", requestURL);
        WeatherConditions conditions = httpClient.submit(HttpClientRequest.<ByteBuf>createGet(requestURL))
                .flatMap(HttpClientResponse::getContent)
                .map(data -> data.toString(Charset.defaultCharset()))
                .map(this::extractConditionsFromJsonResponse)
                .toBlockingObservable().single();

        logger.info("Fetched conditions '{}'", conditions);
        return conditions;
    }

    /**
     * Parse Json response for the token.
     * @param json the Json response
     * @return the extract token
     *
     * Expect a Json response like this one:
     * <pre>
     * {
     *   "response": {
     *     "version":"0.1",
     *     "termsofService":"http://www.wunderground.com/weather/api/d/terms.html",
     *     "features": {
     *       "conditions": 1
     *     }
     *   },
     *   "current_observation": {
     *     "image": {
     *       "url":"http://icons.wxug.com/graphics/wu2/logo_130x80.png",
     *       "title":"Weather Underground",
     *       "link":"http://www.wunderground.com"
     *     },
     *     "display_location": {
     *       "full":"Paris, France",
     *       "city":"Paris",
     *       "state":"",
     *       "state_name":"France",
     *       "country":"FR",
     *       "country_iso3166":"FR",
     *       "zip":"00000",
     *       "magic":"37",
     *       "wmo":"07156",
     *       "latitude":"48.86666489",
     *       "longitude":"2.33333302",
     *       "elevation":"47.00000000"
     *     },                                                                                                                         [28/377]
     *     "observation_location": {
     *       "full":"Paris-centre, Paris",
     *       "city":"Paris-centre",
     *       "state":"Paris",
     *       "country":"France",
     *       "country_iso3166":"FR",
     *       "latitude":"48.851727",
     *       "longitude":"2.335968",
     *       "elevation":"161 ft"
     *     },
     *     "estimated": {
     *     },
     *     "station_id":"I75003PA1",
     *     "observation_time":"Last Updated on May 29, 4:31 PM CEST",
     *     "observation_time_rfc822":"Thu, 29 May 2014 16:31:09 +0200",
     *     "observation_epoch":"1401373869",
     *     "local_time_rfc822":"Thu, 29 May 2014 16:31:58 +0200",
     *     "local_epoch":"1401373918",
     *     "local_tz_short":"CEST",
     *     "local_tz_long":"Europe/Paris",
     *     "local_tz_offset":"+0200",
     *     "weather":"Mostly Cloudy",
     *     "temperature_string":"65.5 F (18.6 C)",
     *     "temp_f":65.5,
     *     "temp_c":18.6,
     *     "relative_humidity":"55%",
     *     "wind_string":"From the West at 1.0 MPH Gusting to 4.0 MPH",
     *     "wind_dir":"West",
     *     "wind_degrees":270,
     *     "wind_mph":1.0,
     *     "wind_gust_mph":"4.0",
     *     "wind_kph":1.6,
     *     "wind_gust_kph":"6.4",
     *     "pressure_mb":"1016",
     *     "pressure_in":"30.01",
     *     "pressure_trend":"0",
     *     "dewpoint_string":"49 F (9 C)",
     *     "dewpoint_f":49,
     *     "dewpoint_c":9,
     *     "heat_index_string":"NA",
     *     "heat_index_f":"NA",
     *     "heat_index_c":"NA",
     *     "windchill_string":"NA",
     *     "windchill_f":"NA",
     *     "windchill_c":"NA",
     *     "feelslike_string":"65.5 F (18.6 C)",
     *     "feelslike_f":"65.5",
     *     "feelslike_c":"18.6",
     *     "visibility_mi":"6.2",
     *     "visibility_km":"10.0",
     *     "solarradiation":"--",
     *     "UV":"4","precip_1hr_string":"0.00 in ( 0 mm)",
     *     "precip_1hr_in":"0.00",
     *     "precip_1hr_metric":" 0",
     *     "precip_today_string":"-999.00 in (-25375 mm)",
     *     "precip_today_in":"-999.00",
     *     "precip_today_metric":"--",
     *     "icon":"mostlycloudy",
     *     "icon_url":"http://icons.wxug.com/i/c/k/mostlycloudy.gif",
     *     "forecast_url":"http://www.wunderground.com/global/stations/07156.html",
     *     "history_url":"http://www.wunderground.com/weatherstation/WXDailyHistory.asp?ID=I75003PA1",
     *     "ob_url":"http://www.wunderground.com/cgi-bin/findweather/getForecast?query=48.851727,2.335968"
     *   }
     * }
     * </pre>
     */
    private WeatherConditions extractConditionsFromJsonResponse(String json) {
        WeatherConditions conditions = new WeatherConditions();
        try {
            JsonNode rootNode = mapper.readTree(json);
            if (rootNode.has("error"))
                throw new RuntimeException("Wunderground did not accept the request!");
            JsonNode current = rootNode.get("current_observation");
            JsonNode temperature = current.get("temp_c");
            JsonNode feelsLikeTemperature = current.get("feelslike_c");
            JsonNode dewpoint = current.get("dewpoint_c");
            JsonNode humidity = current.get("relative_humidity");
            JsonNode pressure = current.get("pressure_mb");
            JsonNode windSpeed = current.get("wind_kph");
            JsonNode windGust = current.get("wind_gust_kph");
            JsonNode windDirection = current.get("wind_degrees");
            JsonNode icon = current.get("icon");
            if (temperature != null)
                conditions.setTemperature(Measure.valueOf(temperature.asDouble(), SI.CELSIUS));
            if (feelsLikeTemperature != null)
                conditions.setFeelsLikeTemperature(Measure.valueOf(feelsLikeTemperature.asDouble(), SI.CELSIUS));
            if (dewpoint != null)
                conditions.setDewPoint(Measure.valueOf(dewpoint.asDouble(), SI.CELSIUS));
            if (humidity != null) {
                String humidityText = humidity.asText();
                humidityText = humidityText.substring(0, humidityText.length() - 1);
                // remove last '%' char before parsing as a percentage
                double humidityValue = Double.parseDouble(humidityText);
                conditions.setRelativeHumidity(Measure.valueOf(humidityValue, NonSI.PERCENT));
            }
            if (pressure != null)
                conditions.setPressure(Measure.valueOf(pressure.asDouble(), PASCAL.times(100)));
            if (windSpeed != null)
                conditions.setWindSpeed(Measure.valueOf(windSpeed.asDouble(), NonSI.KILOMETERS_PER_HOUR));
            if (windGust != null)
                conditions.setWindGust(Measure.valueOf(windGust.asDouble(), NonSI.KILOMETERS_PER_HOUR));
            if (windDirection != null)
                conditions.setWindDirection(Measure.valueOf(windDirection.asDouble(), NonSI.DEGREE_ANGLE));
            if (icon != null)
                conditions.setIcon(getFromIconString(icon.asText()));
            return conditions;
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }

    private WeatherIcon getFromIconString(String iconString) {
        switch (iconString) {
//            case "chanceflurries":
//            case "chancerain":
//            case "chancesleet":
//            case "chancesnow":
//            case "chancetstorms":
            case "clear": case "sunny":
                return WeatherIcon.SUN;
            case "mostlycloudy": case "mostlysunny": case "partlycloudy": case "partlysunny":
                return WeatherIcon.CLOUD_SUN;
            case "cloudy":
                return WeatherIcon.CLOUD;
            case "flurries":
                return WeatherIcon.FLURRIES;
            case "fog":
                return WeatherIcon.FOG;
            case "hazy":
                return WeatherIcon.HAZE;
            case "sleet":
                return WeatherIcon.SLEET;
            case "rain":
                return WeatherIcon.RAIN;
            case "snow":
                return WeatherIcon.SNOW;
            case "tstorms":
                return WeatherIcon.LIGHTNING;
            default:
                throw new IllegalArgumentException(String.format("Can't find icon for '%s'", iconString));
        }

    }

}

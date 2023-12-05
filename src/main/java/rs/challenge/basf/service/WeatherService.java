package rs.challenge.basf.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import rs.challenge.basf.model.WeatherInfo;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeatherService {

    @Value("${api.weather.key}")
    private String WEATHER_API_KEY;
    @Value("${api.weather.uri}")
    private String WEATHER_API_URI;

    public double getTemperature(String city) {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

        String uriWithParams = WEATHER_API_URI + "?q={city}&key={key}";

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("city", city);
        queryParams.put("key", WEATHER_API_KEY);

        try {
            ResponseEntity<WeatherInfo> response = restTemplate
                    .exchange(uriWithParams, HttpMethod.GET, requestEntity, WeatherInfo.class, queryParams);

            WeatherInfo weatherInfo = response.getBody();
            return weatherInfo.getCurrent().get("temp_c").asDouble();
        } catch (Exception e) {
            throw new RestClientException("Error fetching temperature data for city " + city + ".");
        }

    }

}

package rs.challenge.basf.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class WeatherInfo {

    private JsonNode current;

}

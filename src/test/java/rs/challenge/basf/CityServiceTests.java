package rs.challenge.basf;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.client.RestClientException;
import rs.challenge.basf.model.City;
import rs.challenge.basf.repository.CityRepository;
import rs.challenge.basf.request.CreateCityRequest;
import rs.challenge.basf.request.UpdateCityRequest;
import rs.challenge.basf.service.CityService;
import rs.challenge.basf.service.WeatherService;

import java.math.BigInteger;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CityServiceTests {

    @Mock
    private CityRepository cityRepository;

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private CityService cityService;

    private static City city;

    @BeforeAll
    public static void setup() {
        city = new City(
                7085L,
                "RandomName", "RandomCountry",
                "RandomRegion", BigInteger.valueOf(50715),
                14);
    }

    @Test
    public void testFindCitySuccess() {

        when(cityRepository.findByName(any(String.class))).thenReturn(Optional.ofNullable(city));
        when(weatherService.getTemperature(any(String.class))).thenReturn(10.0);

        City c = cityService.findByName("Ibiza");

        assertThat(c).isNotNull();
        assertThat(c.getId()).isEqualTo(city.getId());
    }

    @Test
    public void testFindCityNoSuchElementException() {

        when(cityRepository.findByName(any(String.class))).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
           cityService.findByName("RandomCityName");
        });

    }

    @Test
    public void testFindCityWeatherServiceRestException() {

        when(weatherService.getTemperature(any(String.class)))
                .thenThrow(new RestClientException("Error fetching temperature data for city" + city + "."));
        when(cityRepository.findByName(any(String.class))).thenReturn(Optional.ofNullable(city));

        Exception exception = assertThrows(RestClientException.class, () -> {
            cityService.findByName("Ibiza");
        });

        String expectedMessage = "Error fetching temperature data for city" + city + ".";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

    }

    @Test
    public void testCreateCitySuccess() {

        CreateCityRequest cityRequest = getCreateCityRequest();
        City newCity = getCityFromRequest(cityRequest);

        when(cityRepository.save(any(City.class))).thenReturn(newCity);

        City c = cityService.create(cityRequest);

        assertThat(c).isNotNull();
        assertThat(c).isEqualTo(newCity);
    }

    @Test
    public void testCreateCityDuplicateElementException() {

        CreateCityRequest cityRequest = getCreateCityRequest();

        when(cityRepository.findByName(any(String.class))).thenReturn(Optional.ofNullable(city));

        Exception exception = assertThrows(DuplicateKeyException.class, () -> {
            cityService.create(cityRequest);
        });

        String expectedMessage = "City already exists.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

    }

    @Test
    public void testUpdateCitySuccess() {

        UpdateCityRequest cityRequest = getUpdateCityRequest();
        City updatedCity = getCityFromRequest(cityRequest);

        when(cityRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(city));
        when(cityRepository.save(any(City.class))).thenReturn(updatedCity);

        City c = cityService.update(cityRequest);

        assertThat(c).isNotNull();
        assertThat(c).isEqualTo(updatedCity);
    }

    @Test
    public void testUpdateCityNoSuchElementException() {

        UpdateCityRequest cityRequest = getUpdateCityRequest();

        when(cityRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            cityService.update(cityRequest);
        });

    }

    @Test
    public void testDeleteCityByIdSuccess() {

        when(cityRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(city));
        cityService.deleteById(1L);

        verify(cityRepository, times(1)).delete(city);

    }

    @Test
    public void testDeleteCityByIdNoSuchElementException() {

        when(cityRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            cityService.deleteById(1L);
        });

    }

    @Test
    public void testDeleteCityByNameSuccess() {

        when(cityRepository.findByName(any(String.class))).thenReturn(Optional.ofNullable(city));
        cityService.deleteByName("RandomName");

        verify(cityRepository, times(1)).delete(city);

    }

    @Test
    public void testDeleteCityByNameNoSuchElementException() {

        when(cityRepository.findByName(any(String.class))).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            cityService.deleteByName("RandomName");
        });

    }

    private CreateCityRequest getCreateCityRequest() {
        return new CreateCityRequest("NewCity", "NewCountry",
                "NewRegion", "1234567");
    }

    private UpdateCityRequest getUpdateCityRequest() {
        return new UpdateCityRequest(1L, "UpdatedCity", "UpdatedCountry",
                "UpdatedRegion", "7654321");
    }

    private City getCityFromRequest(CreateCityRequest cityRequest) {
        City newCity = new City();
        newCity.setName(cityRequest.getName());
        newCity.setCountry(cityRequest.getCountry());
        newCity.setStateRegion(cityRequest.getStateRegion());
        newCity.setPopulation(new BigInteger(cityRequest.getPopulation()));
        return newCity;
    }

    private City getCityFromRequest(UpdateCityRequest cityRequest) {
        City city = new City();
        city.setName(cityRequest.getName());
        city.setCountry(cityRequest.getCountry());
        city.setStateRegion(cityRequest.getStateRegion());
        city.setPopulation(new BigInteger(cityRequest.getPopulation()));
        return city;
    }



}

package rs.challenge.basf.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import rs.challenge.basf.model.City;
import rs.challenge.basf.repository.CityRepository;
import rs.challenge.basf.request.CreateCityRequest;
import rs.challenge.basf.request.UpdateCityRequest;

import java.math.BigInteger;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CityService {

    private final CityRepository cityRepository;
    private final WeatherService weatherService;

    public CityService(CityRepository cityRepository, WeatherService weatherService) {
        this.cityRepository = cityRepository;
        this.weatherService = weatherService;
    }

    public List<City> findAll() {
        return cityRepository.findAll();
    }

    public City findById(Long id) {
        return cityRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
    }

    public City findByName(String name) {

        City city = cityRepository.findByName(name)
                .orElseThrow(NoSuchElementException::new);

        int temp = (int) Math.round(weatherService.getTemperature(name));
        city.setTemperature(temp);

        return city;
    }

    public City create(CreateCityRequest request) {

        if (cityRepository.findByName(request.getName()).isPresent()) {
            throw new DuplicateKeyException("City already exists.");
        }
        City city = new City();
        city.setName(request.getName());
        city.setCountry(request.getCountry());
        city.setStateRegion(request.getStateRegion());
        city.setPopulation(new BigInteger(request.getPopulation()));

        try {
            city = cityRepository.save(city);
        } catch (Exception e) {
            throw new RuntimeException("Error while trying to persist city.");
        }

        return city;
    }

    public City update(UpdateCityRequest request) {

        City city = findById(request.getId());

        if (StringUtils.isNotEmpty(request.getName())) {
            city.setName(request.getName());
        }
        if (StringUtils.isNotEmpty(request.getCountry())) {
            city.setCountry(request.getCountry());
        }
        if (StringUtils.isNotEmpty(request.getPopulation())) {
            city.setPopulation(new BigInteger(request.getPopulation()));
        }

        try {
            city = cityRepository.save(city);
        } catch (Exception e) {
            throw new RuntimeException("Error while trying to update city.");
        }

        return city;
    }

    public void deleteById(Long id) {

        City city = cityRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
        try {
            cityRepository.delete(city);
        } catch (Exception e) {
            throw new RuntimeException("Error while trying to delete city.");
        }
    }

    public void deleteByName(String name) {

        City city = cityRepository.findByName(name)
                .orElseThrow(NoSuchElementException::new);
        try {
            cityRepository.delete(city);
        } catch (Exception e) {
            throw new RuntimeException("Error while trying to delete city.");
        }
    }


}

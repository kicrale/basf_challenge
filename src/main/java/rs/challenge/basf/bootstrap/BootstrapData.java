package rs.challenge.basf.bootstrap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rs.challenge.basf.model.City;
import rs.challenge.basf.model.User;
import rs.challenge.basf.repository.CityRepository;
import rs.challenge.basf.repository.UserRepository;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigInteger;
import java.util.Arrays;

@Component
public class BootstrapData implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CityRepository cityRepository;

    public BootstrapData(UserRepository userRepository, PasswordEncoder passwordEncoder,
                         CityRepository cityRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.cityRepository = cityRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        // Import data from CSV run only once, comment out to populate city data
        // importData();

        System.out.println("Loading users......");

        for (int i = 0; i < 5; i++) {
            User user = new User();
            user.setFirstName("UserFirst" + i);
            user.setLastName("UserLast" + i);
            user.setEmail("user" + i + "@gmail.com");
            user.setPassword(passwordEncoder.encode("user" + i));
            try {
                userRepository.save(user);
            } catch (DataIntegrityViolationException ignored) {}
        }

        System.out.println("Data Loaded.");

    }

    private void importData() throws IOException {
        String[] HEADERS = {"id", "country", "name", "population", "region"};

        Reader reader = new FileReader("src/main/resources/cities.csv");

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(HEADERS)
                .setSkipHeaderRecord(true)
                .build();

        Iterable<CSVRecord> records = csvFormat.parse(reader);
        for (CSVRecord record: records) {
            String name = record.get("name");
            String country = record.get("country");
            String stateRegion = record.get("region");
            String population = record.get("population");

            City city = new City();
            city.setName(name);
            city.setCountry(country);
            city.setStateRegion(stateRegion);
            if (StringUtils.isNotEmpty(population) && !population.equals("null")) {
                city.setPopulation(new BigInteger(population.split("\\.")[0]));
            }

            try {
                cityRepository.save(city);
            } catch (DataIntegrityViolationException ignored) {}
        }


    }

}

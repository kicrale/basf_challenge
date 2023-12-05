package rs.challenge.basf.controller;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import rs.challenge.basf.model.City;
import rs.challenge.basf.request.CreateCityRequest;
import rs.challenge.basf.request.UpdateCityRequest;
import rs.challenge.basf.service.CityService;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping()
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(cityService.findAll());
    }

    @GetMapping("/{name}")
    public ResponseEntity<?> findByName(@PathVariable("name") String cityName) {
        try {
            City city = cityService.findByName(cityName);
            return ResponseEntity.ok(city);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No data for given city.");
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }
    }

    @PostMapping()
    public ResponseEntity<?> createCity(@RequestBody CreateCityRequest request) {
        try {
            City city = cityService.create(request);
            return ResponseEntity.ok(city);
        } catch (DuplicateKeyException dke) {
            return ResponseEntity.badRequest().body(dke.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping()
    public ResponseEntity<?> updateCity(@RequestBody UpdateCityRequest request) {
        try {
            City city = cityService.update(request);
            return ResponseEntity.ok(city);
        } catch (NoSuchElementException nse) {
            return ResponseEntity.badRequest().body(nse.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteCity(@RequestParam(required = false) Long id,
                                        @RequestParam(required = false) String name) {
        try {
            if (id == null && name == null) {
                return ResponseEntity.badRequest().body("Id or Name parameter is required.");
            }
            if (id != null) {
                cityService.deleteById(id);
            } else {
                cityService.deleteByName(name);
            }
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException nse) {
            return ResponseEntity.badRequest().body(nse.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

}

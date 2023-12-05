package rs.challenge.basf.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateCityRequest {

    @NotBlank(message = "Name is mandatory")
    String name;
    @NotBlank(message = "Country is mandatory")
    String country;
    String stateRegion;
    @NotBlank(message = "Population is mandatory")
    String population;

}

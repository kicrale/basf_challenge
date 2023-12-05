package rs.challenge.basf.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdateCityRequest {

    @NotBlank(message = "Id is mandatory")
    Long id;
    String name;
    String country;
    String stateRegion;
    String population;

}

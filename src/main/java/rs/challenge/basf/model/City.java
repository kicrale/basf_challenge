package rs.challenge.basf.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "cities")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true)
    private String name;
    @Column(name = "country")
    private String country;
    @Column(name = "state/region")
    private String stateRegion;
    @Column(name = "population")
    @JsonSerialize(using = ToStringSerializer.class)
    private BigInteger population;

    @Transient
    private int temperature;

}

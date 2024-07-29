package wasteManagement.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.lang.Nullable;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bin {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "capacity(L)")
    private int capacity;

    @NonNull
    @Column(name  = "city")
    private String city;

    @Column(name  = "road")
    private String road;

    @Column(name  = "civicNumber")
    private String civicNumber;

    @NonNull
    @Column(name  = "longitude")
    private Float longitude;

    @NonNull
    @Column(name  = "latitude")
    private Float latitude;

    @Nullable
    @Column(name = "associatedUser")
    private String user;

    @Column(name = "needsEmptying")
    private Boolean needsEmptying;

    @Column(name = "beingEmptied")
    private Boolean beingEmptied;

    // Method to calculate distance between two bins
    public double distanceTo(Bin other) {
        double deltaX = this.latitude - other.latitude;
        double deltaY = this.longitude - other.longitude;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }
}


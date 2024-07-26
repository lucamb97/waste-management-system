package wasteManagement.model.entitys;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "authorities", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username", "authority"})
})
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "authority", length = 50, nullable = false)
    private String authority;

    @ManyToOne
    @JoinColumn(name = "username", nullable = false)
    private User user;

}


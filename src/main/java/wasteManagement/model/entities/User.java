package wasteManagement.model.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import wasteManagement.model.entities.issues.Issue;
import wasteManagement.model.entities.observer.Observer;

import java.util.List;

@Slf4j
@Entity
@Data
@Table(name = "users")
public class User implements Observer{

    @Id
    @Column(name = "username", length = 50, nullable = false)
    private String username;

    @Column(name = "password", length = 500, nullable = false)
    private String password;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "city", nullable = false)
    private String city;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Authority> authorities;

    //this is used for the observer design pattern
    @Override
    public void update(Issue issue) {
        if (this.city.equals(issue.getCity())) {
            //in a real implementation, this would send a notification to all the workers of the city
            //one worker would then accept to check out the issue
            log.info("Worker " + username + " notified of issue in " + city + " for issue id: " + issue.getId());
        }
    }
}



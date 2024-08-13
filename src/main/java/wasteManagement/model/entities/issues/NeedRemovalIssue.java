package wasteManagement.model.entities.issues;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("NEED_REMOVAL")
public class NeedRemovalIssue extends Issue {

    @Override
    public void handle() {
        // Implementation of handling a resolved issue
    }
}
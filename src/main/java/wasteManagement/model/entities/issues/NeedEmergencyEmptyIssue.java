package wasteManagement.model.entities.issues;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("NEED_EMERGENCY_EMPTY")
public class NeedEmergencyEmptyIssue extends Issue {

    @Override
    public void handle() {
        // Implementation of handling a resolved issue
    }
}

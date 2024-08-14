package wasteManagement.model.entities.issues;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("MISSING_BIN")
public class MissingBinIssue extends Issue {

    @Override
    public void handle() {
        this.setResolved(true);
    }
}

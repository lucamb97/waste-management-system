package wasteManagement.model.entities.issues;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("BROKEN_BIN")
public class BrokenBinIssue extends Issue {

    @Override
    public void handle(){
        this.setResolved(true);
    }
}

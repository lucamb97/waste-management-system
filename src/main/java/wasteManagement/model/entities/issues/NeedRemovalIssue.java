package wasteManagement.model.entities.issues;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import wasteManagement.model.repositorys.BinsRepository;
import wasteManagement.model.repositorys.IssueRepository;

@Entity
@DiscriminatorValue("NEED_REMOVAL")
public class NeedRemovalIssue extends Issue {

    @Override
    public void handle(BinsRepository binsRepository, IssueRepository issueRepository, IssueFactory issueFactory, Boolean fixed) {
        // remove the bin
        binsRepository.deleteById(this.getBinId());
        this.setResolved(true);
    }
}

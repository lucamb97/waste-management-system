package wasteManagement.model.entities.issues;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import wasteManagement.model.entities.Bin;
import wasteManagement.model.repositorys.BinsRepository;
import wasteManagement.model.repositorys.IssueRepository;

import java.util.Optional;

@Entity
@DiscriminatorValue("MISSING_BIN")
public class MissingBinIssue extends Issue {

    @Override
    public void handle(BinsRepository binsRepository, IssueRepository issueRepository, IssueFactory issueFactory, Boolean fixed) {
        //if it was fixed the front-end should have added the new bin to the db
        //check if it was added
        Optional<Bin> optionalBin = binsRepository.findById(this.getBinId());
        if (optionalBin.isPresent()) {
            this.setResolved(true);
        }
    }
}

package wasteManagement.model.entities.issues;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import wasteManagement.model.entities.Bin;
import wasteManagement.model.repositorys.BinsRepository;
import wasteManagement.model.repositorys.IssueRepository;

import java.util.Optional;

@Entity
@DiscriminatorValue("NEED_EMPTY")
public class NeedEmergencyEmptyIssue extends Issue {

    @Override
    public void handle(BinsRepository binsRepository, IssueRepository issueRepository, IssueFactory issueFactory, Boolean fixed) {
        //change the bin to empty
        Optional<Bin> optionalBin = binsRepository.findById(this.getBinId());
        if (optionalBin.isPresent()){
            Bin bin = optionalBin.get();
            bin.setNeedsEmptying(false);
            bin.setStatus("WORKING");
            binsRepository.save(bin);
            this.setResolved(true);
        }
    }
}

package wasteManagement.model.entities.issues;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import wasteManagement.model.entities.Bin;
import wasteManagement.model.repositorys.BinsRepository;
import wasteManagement.model.repositorys.IssueRepository;

import java.util.Optional;

@Entity
@DiscriminatorValue("BROKEN_BIN")
public class BrokenBinIssue extends Issue {

    @Override
    public void handle(BinsRepository binsRepository, IssueRepository thisRepository, IssueFactory thisFactory, Boolean fixed){
        //With a broken bin we simply change the bin status to WORKING after it is fixed
        //Or we delete the bin if it can't be fixed

        if (fixed) {
            //change the bin status to working
            Bin bin = binsRepository.findById(this.getBinId()).get();
            bin.setStatus("WORKING");
            binsRepository.save(bin);
            this.setResolved(true);
        }

        //the bin can't be fixed and needs to be removed
        //we will also create a missing bin this
        if (!fixed) {
            //first remove the broken bin
            binsRepository.deleteById(this.getBinId());

            //this converts the broken bin this into a missing bin this
            //Create a new MissingBinIssue and copy properties
            Issue missingBinIssue = thisFactory.createIssue("MISSING_BIN", this.getBinId());
            missingBinIssue.setId(this.getId());
            missingBinIssue.setIssueDescription(this.getIssueDescription());
            missingBinIssue.setCity(this.getCity());
            missingBinIssue.setCreatedBy(this.getCreatedBy());
            missingBinIssue.setAssignedWorker(this.getAssignedWorker());

            //Delete the old BrokenBinIssue
            thisRepository.delete(this);

            // Save the new MissingBinIssue
            thisRepository.save(missingBinIssue);
        }
    }
}

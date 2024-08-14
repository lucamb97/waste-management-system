package wasteManagement.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wasteManagement.model.entities.Bin;
import wasteManagement.model.entities.issues.Issue;
import wasteManagement.model.entities.issues.MissingBinIssue;
import wasteManagement.model.repositorys.BinsRepository;
import wasteManagement.model.repositorys.IssueRepository;

import java.util.Optional;

@Service
public class IssueHandler {
    @Autowired
    private BinsRepository binsRepository;
    @Autowired
    private IssueRepository issueRepository;

    public void handleIssue(long issueId, Boolean fixed) {
        Optional<Issue> optionalIssue = issueRepository.findById(issueId);
        Issue issue;
        if (optionalIssue.isPresent()) {
            issue = optionalIssue.get();
        } else {
            throw new EntityNotFoundException("Couldn't find issue");
        }
        switch (issue.getIssueType()) {
            case "MISSING_BIN":
                missingBinHandler(issue);
                break;
            case "BROKEN_BIN":
                brokenBinHandler(issue, fixed);
                break;
            case "NEED_EMERGENCY_EMPTY":
                needEmptyHandler(issue);
                break;
            case "NEED_REMOVAL":
                needRemovalHandler(issue);
                break;
        }
        issueRepository.save(issue);
    }

    @Transactional
    public void brokenBinHandler(Issue issue, Boolean fixed){
        //With a broken bin we simply change the bin status to WORKING after it is fixed
        //Or we delete the bin if it can't be fixed

        if (fixed) {
            //change the bin status to working
            Bin bin = binsRepository.findById(issue.getBinId()).get();
            bin.setStatus("WORKING");
            binsRepository.save(bin);
            issue.handle();
        }

        //the bin can't be fixed and needs to be removed
        //we will also create a missing bin issue
        if (!fixed) {
            //first remove the broken bin
            binsRepository.deleteById(issue.getBinId());

            //issue converts the broken bin issue into a missing bin issue
            //Create a new MissingBinIssue and copy properties
            MissingBinIssue missingBinIssue = new MissingBinIssue();
            missingBinIssue.setBinId(issue.getBinId());
            missingBinIssue.setCity(issue.getCity());
            missingBinIssue.setIssueDescription(issue.getIssueDescription());
            missingBinIssue.setCreatedBy(issue.getCreatedBy());
            missingBinIssue.setCreatedAt(issue.getCreatedAt());
            missingBinIssue.setResolved(issue.isResolved());

            //Delete the old BrokenBinIssue
            issueRepository.delete(issue);

            // Save the new MissingBinIssue
            issueRepository.save(missingBinIssue);
        }
    }

    @Transactional
    public void missingBinHandler(Issue issue) {
        //if it was fixed the front-end should have added the new bin to the db
        //check if it was added
        Bin bin = binsRepository.findById(issue.getBinId()).get();
        if (bin != null) {
            issue.handle();
        }
    }

    @Transactional
    public void needEmptyHandler(Issue issue) {
        //change the bin to empty
        Bin bin = binsRepository.findById(issue.getBinId()).get();
        bin.setNeedsEmptying(false);
        bin.setStatus("WORKING");
        binsRepository.save(bin);
        issue.handle();
    }

    @Transactional
    public void needRemovalHandler(Issue issue) {
        // remove the bin
        binsRepository.deleteById(issue.getBinId());
        issue.handle();
    }
}

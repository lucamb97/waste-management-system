package wasteManagement.services;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wasteManagement.model.entities.Bin;
import wasteManagement.model.entities.issues.Issue;
import wasteManagement.model.entities.UserInfo;
import wasteManagement.model.entities.issues.IssueFactory;
import wasteManagement.model.entities.observer.Observer;
import wasteManagement.model.entities.observer.Subject;
import wasteManagement.model.repositorys.BinsRepository;
import wasteManagement.model.repositorys.IssueRepository;
import wasteManagement.model.repositorys.UserRepository;
import wasteManagement.model.utils.IssueRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class IssueTracker implements Subject {
    private List<Observer> observers = new ArrayList<>();
    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private BinsRepository binsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IssueFactory issueFactory;

    @PostConstruct
    public void init() {
        List<UserInfo> workers = userRepository.findWorkers();
        for (UserInfo worker : workers) {
            addObserver(worker);
        }

        log.info("All workers have been added as observers");
    }
    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Issue issue) {
        for (Observer observer : observers) {
            observer.update(issue);
        }
    }

    //create and save new issue in db
    public void createIssue(IssueRequest request) {
        //make sure the bin exists in the city
        Optional<Bin> optionalBin = binsRepository.findById(request.getBinId());
        Bin bin;
        if (optionalBin.isPresent()) {
            bin = optionalBin.get();
        } else {throw new EntityNotFoundException();}

        if ((bin.getCity()).equals(request.getCity())) {
            //create the correct issue state
            Issue issue = issueFactory.createIssue(request.getType(), request.getBinId());
            issue.setCity(request.getCity());
            issue.setBinId(request.getBinId());
            issue.setCreatedBy(request.getUsername());
            issue.setIssueDescription(request.getDescription());
            issue.setCreatedAt(LocalDateTime.now());
            //save the issue
            issueRepository.save(issue);
            log.info("Issue created in city {} for bin: {}", request.getCity(), request.getBinId());
            //notify the workers
            notifyObservers(issue);
        } else {
            throw new EntityNotFoundException();
        }
    }

    //Assign an issue to a worker and return the issueBin
    public Bin assignIssue(String worker, Long issueId) {
        // Assign the issue to the worker
        issueRepository.assignIssueToWorker(worker, issueId);
        // Retrieve the updated issue
        Optional<Issue> issue = issueRepository.findById(issueId);
        // retrieve the issue bin
        Optional<Bin> issueBin = binsRepository.findById(issue.get().getBinId());

        return issueBin.get();
    }


}

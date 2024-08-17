package wasteManagement.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wasteManagement.model.entities.Bin;
import wasteManagement.services.IssueHandler;
import wasteManagement.services.IssueTracker;
import wasteManagement.services.WorkerService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/worker")
public class WorkerController {

    @Autowired
    private WorkerService workerService;
    @Autowired
    private IssueTracker issueTracker;
    @Autowired
    private IssueHandler issueHandler;

    // end-point for getting a list of bins from a city, and plot them through a route
    @GetMapping("/getRoute")
    public ResponseEntity<List<Bin>> getRoute(@RequestParam String city) {
        try {
            List<Bin> orderedBins = workerService.plotWorkerRoute(city);
            log.info("Route for city {} completed", city);
            return new ResponseEntity<>(orderedBins, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error creating route {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //this end-point is used to report that a bin has been emptied
    @PutMapping("/binEmptied")
    public ResponseEntity<Void> binEmptied(@RequestParam Long id) {
        try {
            workerService.binEmptied(id);
            log.info("Bin {} emptied", id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error emptying bin {}: {}", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //this is the endpoint to accept a received issue
    @PutMapping("/assignIssue")
    public ResponseEntity<Bin> assignIssue(@RequestParam String worker, Long issueId) {
        try {
            Bin issueBin = issueTracker.assignIssue(worker, issueId);
            log.info("Issue: {} assigned to worker {}", issueId, worker);
            return new ResponseEntity<>(issueBin, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error assigning issue {} to worker {}: {}",issueId, worker, e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //this shows that the issue was handled
    @GetMapping("/handleIssue")
    public ResponseEntity<String> handleIssue(@RequestParam long issueId, Boolean fixed){
        try {
            issueHandler.handleIssue(issueId, fixed);
            log.info("Issue: {} handled", issueId);
            return new ResponseEntity<>(null, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            log.warn("Issue id {} not found", issueId);
            return new ResponseEntity<>("Issue id not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error handling issue {}: {} ",issueId, e.getMessage());
            return new ResponseEntity<>("Couldn't complete issue handling", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

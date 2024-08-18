package wasteManagement.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wasteManagement.model.entities.issues.Issue;
import wasteManagement.model.utils.IssueRequest;
import wasteManagement.services.IssueTracker;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IssueTracker issueTracker;

    //this is used to create a new issue with a bin
    @PostMapping("/createIssue")
    public ResponseEntity<Issue> createIssue(@RequestBody IssueRequest issueRequest) {
        try {
            Issue issue = issueTracker.createIssue(issueRequest);
            log.info("Created issue {}", issueRequest.getDescription());
            return new ResponseEntity<>(issue, HttpStatus.OK);
        } catch (EntityNotFoundException e){
            log.warn("The specified bin is not in this city");
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception e) {
            log.error("Error creating issue {}: {}",issueRequest.getDescription(), e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //this is used to check the status of the issues associated to the user
    @GetMapping("/checkIssue")
    public ResponseEntity<List<Issue>> checkIssue(@RequestParam String username) {
        try {
            List<Issue> issues = issueTracker.checkIssues(username);
            return new ResponseEntity<>(issues, HttpStatus.OK);
        } catch (EntityNotFoundException e){
            log.warn("No issues found for user: {}", username);
            return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error searching issues for user {}: {}",username, e.getMessage());
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

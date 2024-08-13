package wasteManagement.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wasteManagement.model.utils.IssueRequest;
import wasteManagement.services.IssueTracker;


@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IssueTracker issueTracker;

    //this is used to create a new issue with a bin
    @PostMapping("/createIssue")
    public ResponseEntity<Void> createIssue(@RequestBody IssueRequest issueRequest) {
        try {
            issueTracker.createIssue(issueRequest);
            log.info("Created issue {}", issueRequest.getDescription());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException e){
            log.error("The specified bin is not in this city");
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception e) {
            log.error("Error creating issue {}: {}",issueRequest.getDescription(), e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

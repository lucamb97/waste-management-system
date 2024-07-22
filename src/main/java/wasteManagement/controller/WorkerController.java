package wasteManagement.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wasteManagement.model.entitys.Bin;
import wasteManagement.services.WorkerService;

import java.util.List;

@RestController
@RequestMapping("/worker")
public class WorkerController {

    @Autowired
    private WorkerService workerService;

    // end-point for getting a list of bins from a city, and plot them through a route
    @GetMapping("/getRoute")
    public ResponseEntity<List<Bin>> getRoute(@RequestParam String city) {
        try {
            List<Bin> orderedBins = workerService.plotWorkerRoute(city);
            return new ResponseEntity<>(orderedBins, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //this end-point is used to report that a bin has been emptied
    @PutMapping("/binEmptied")
    public ResponseEntity<Void> binEmptied(@RequestParam long id) {
        try {
            workerService.binEmptied(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

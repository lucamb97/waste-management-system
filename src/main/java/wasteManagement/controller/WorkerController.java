package wasteManagement.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    public ResponseEntity<List<Bin>> getAllCityBins(@RequestParam(required = true) String city) {
        try {
            List orderedBins = workerService.plotWorkerRoute(city);
            return new ResponseEntity<>(orderedBins, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

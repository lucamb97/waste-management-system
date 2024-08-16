package wasteManagement.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wasteManagement.model.entities.Bin;
import wasteManagement.services.BinService;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/bins")
public class BinController {

    @Autowired
    private BinService binService;

    // end-point for adding new bins, takes in a list of bin objects
    @PostMapping
    public ResponseEntity<Void> addBins(@RequestBody List<Bin> bins) {
        try {
            binService.addBins(bins);
            log.info("New bins saved");
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Couldn't save bins {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // end-point for getting a list of bins from a city, bins, takes in the city name as a string
    @GetMapping
    public ResponseEntity<List<Bin>> getAllCityBins(@RequestParam String city) {
        try {
            List<Bin> bins = binService.getBinsByCity(city);
            if (!bins.isEmpty()) {
                log.info("Returning list of bins for city {}",city);
                return new ResponseEntity<>(bins, HttpStatus.OK);
            } else {
                log.warn("No bins found for city {}" ,city);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Error retrieving bins {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // End-point for retrieving a bin by its ID
    @GetMapping("/id/{id}")
    public ResponseEntity<Bin> getBinById(@PathVariable("id") long id) {
        try {
            Optional<Bin> bin = binService.getBinById(id);
            if (bin.isPresent()) {
                log.info("Returning bin with id {}",id);
                return new ResponseEntity<>(bin.get(), HttpStatus.OK);
            } else {
                log.warn("No bins found with id {}" ,id);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Error retrieving bins for id{}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // End-point for retrieving a bin by its associated user
    @GetMapping("/user/{user}")
    public ResponseEntity<List<Bin>> getBinById(@PathVariable("user") String user) {
        try {
            List<Bin> bins = binService.getBinByUser(user);
            if (!bins.isEmpty()) {
                log.info("Returning bin with user {}",user);
                return new ResponseEntity<>(bins, HttpStatus.OK);
            } else {
                log.warn("No bins found with user {}" ,user);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Error retrieving bins for user {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // end-point for deleting bins, takes in a list of IDs as key
    @DeleteMapping(path = "/deleteBins")
    public ResponseEntity<Void> deleteBins(@RequestBody List<Long> ids){
        try {
            binService.deleteBins(ids);
            log.info("Deleted bins");
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (Exception e) {
            log.error("Error deleting bins {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
            }
}

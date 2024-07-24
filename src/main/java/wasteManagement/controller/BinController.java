package wasteManagement.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wasteManagement.model.entitys.Bin;
import wasteManagement.services.BinService;

import java.util.List;

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
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // End-point for retrieving a bin by its ID
    @GetMapping("/{id}")
    public ResponseEntity<Bin> getBinById(@PathVariable("id") long id) {
        try {
            Bin bin = binService.getBinById(id);
            if (bin != null) {
                log.info("Returning bin with id {}",id);
                return new ResponseEntity<>(bin, HttpStatus.OK);
            } else {
                log.warn("No bins found with id {}" ,id);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
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
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
            }
}

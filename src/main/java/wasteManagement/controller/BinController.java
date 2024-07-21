package wasteManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wasteManagement.model.entitys.Bin;
import wasteManagement.services.BinService;

import java.util.List;

@RestController
@RequestMapping("/bins")
public class BinController {

    @Autowired
    private BinService binService;

    // end-point for getting a list of bins from a city, bins, takes in the city name as a string
    @GetMapping
    public ResponseEntity<List<Bin>> getAllCityBins(@RequestParam(required = true) String city) {
        try {
            List bins = binService.getBinsByCity(city);
            return new ResponseEntity<>(bins, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // end-point for adding new bins, takes in a list of bin objects
    @PostMapping
    public ResponseEntity<Void> addBins(@RequestBody List<Bin> bins) {
        try {
            binService.addBins(bins);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // end-point for deleting bins, takes in a list of IDs as key
    @DeleteMapping(path = "/deleteBins")
    public ResponseEntity<Void> deleteBins(@RequestBody List<Long> ids){
        try {
            binService.deleteBins(ids);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
            }
}

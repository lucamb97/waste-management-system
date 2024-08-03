package wasteManagement.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wasteManagement.model.entities.Bin;
import wasteManagement.model.repositorys.BinsRepository;

import java.util.*;

import static wasteManagement.configuration.utils.Constants.MAX_ROUTE_STOPS;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkerService {

    private final BinsRepository binsRepository;

    //this method allows to change the status of a bin
    public void binEmptied(long id){
        try{
            binsRepository.binEmptied(id);
        } catch (Exception e){
            log.error("There was an error contacting the database for bins emptied", e);
            throw e;
        }

    }

    // Function to find the shortest path using a heuristic approach (Nearest Neighbor)
    public List<Bin> plotWorkerRoute(String city){

        //get a list of all bins that need to be emptied from the workers city
        List<Bin> bins = binsRepository.findByNeedEmptying(city);

        if (bins.isEmpty()){
            return null;
        }

        //start creating a route from a random starting point
        List<Bin> route = new ArrayList<>();
        Set<Bin> visited = new HashSet<>();
        Bin current = bins.get(0);
        route.add(current);
        visited.add(current);


        while (visited.size() < bins.size() && route.size() < MAX_ROUTE_STOPS) {
            Bin nearest = null;
            double minDistance = Double.MAX_VALUE;
            //find the nearest bin
            for (Bin bin : bins) {
                if (!visited.contains(bin)) {
                    double distance = Objects.requireNonNull(current).distanceTo(bin);
                    if (distance < minDistance) {
                        minDistance = distance;
                        nearest = bin;
                    }
                }
            }
            route.add(nearest);
            visited.add(nearest);
            current = nearest;
        }

        //change state of the bins in the route we found to beingEmptied = True
        try {
            List<Long> routeIds = new ArrayList<>();
            for (Bin bin : route) {
                routeIds.add(bin.getId());
            }
            binsRepository.updateBeingEmptied(routeIds);
        } catch (Exception e) {
            log.error("Error updating route bins status", e);
            throw e;
        }

        return route;
    }
}

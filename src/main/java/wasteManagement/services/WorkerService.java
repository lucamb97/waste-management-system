package wasteManagement.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wasteManagement.model.entitys.Bin;
import wasteManagement.model.repositorys.BinsRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class WorkerService {

    private final BinsRepository binsRepository;

    // Function to find the shortest path using a heuristic approach (Nearest Neighbor)
    public List<Bin> plotWorkerRoute(String city){

        //get a list of all bins that need to be emptied from the workers city
        List<Bin> bins = binsRepository.findByNeedEmptying(city);

        //start creating a route from the list
        List<Bin> route = new ArrayList<>();
        Set<Bin> visited = new HashSet<>();
        Bin current = bins.get(0);
        route.add(current);
        visited.add(current);

        while (visited.size() < bins.size()) {
            Bin nearest = null;
            double minDistance = Double.MAX_VALUE;
            for (Bin bin : bins) {
                if (!visited.contains(bin)) {
                    double distance = current.distanceTo(bin);
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
        return route;
    }
}



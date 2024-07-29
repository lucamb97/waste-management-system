package wasteTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static wasteManagement.configuration.utils.Constants.MAX_ROUTE_STOPS;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import wasteManagement.Main;
import wasteManagement.configuration.utils.Constants;
import wasteManagement.model.entities.Bin;
import wasteManagement.model.repositorys.BinsRepository;
import wasteManagement.services.WorkerService;

import java.util.ArrayList;
import java.util.List;

public class WorkerServiceTest {

    @Mock
    private BinsRepository binsRepository;

    @InjectMocks
    private WorkerService workerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPlotWorkerRouteWithBins() {
        // Setup
        String city = "TestCity";
        //(id,capacity,city,road,civicNumber,longitude,latitude,user,needsEmptying,beingEmptied)
        Bin bin1 = new Bin(1L, 120, city, "TestRoad1", "Test1",0.0f ,0.0f  , "TestUser1", true, false);
        Bin bin2 = new Bin(2L, 100, city, "TestRoad2", "Test2",20.0f ,15.0f  , "TestUser2", true, false);
        Bin bin3 = new Bin(3L, 150, city, "TestRoad3", "Test3",1.0f ,0.0f  , "TestUser3", true, false);
        Bin bin4 = new Bin(4L, 90, city, "TestRoad4", "Test4",10.0f ,10.0f  , "TestUser4", true, false);
        Bin bin5 = new Bin(5L, 190, city, "TestRoad5", "Test5",10.0f ,10.0f  , "TestUser4", true, false);
        List<Bin> bins = List.of(bin1, bin2, bin3, bin4, bin5);

        when(binsRepository.findByNeedEmptying(city)).thenReturn(bins);

        // Execute
        List<Bin> route = workerService.plotWorkerRoute(city);;
        // Verify the order
        assertNotNull(route);
        assertTrue(route.size() <= MAX_ROUTE_STOPS); // Check if the size is within the allowed maximum
        assertEquals(1L, route.get(0).getId()); // Bin ID 1
        assertEquals(3L, route.get(1).getId()); // Bin ID 3
        assertEquals(4L, route.get(2).getId()); // Bin ID 4
        assertEquals(5L, route.get(3).getId()); // Bin ID 5
        assertEquals(2L, route.get(4).getId()); // Bin ID 2

        // Verify that updateBeingEmptied was called with correct IDs
        verify(binsRepository).updateBeingEmptied(List.of(1L, 3L, 4L, 5L, 2L));
    }

    @Test
    void testPlotWorkerRouteNoBins() {
        // Setup
        String city = "EmptyCity";
        when(binsRepository.findByNeedEmptying(city)).thenReturn(new ArrayList<>());

        // Execute
        List<Bin> route = workerService.plotWorkerRoute(city);

        // Verify
        assertNull(route);
        verify(binsRepository, never()).updateBeingEmptied(anyList());
    }

    @Test
    void testPlotWorkerRouteWithException() {
        // Setup
        String city = "TestCity";
        Bin bin1 = new Bin(1L, 120, city, "TestRoad1", "Test1",0.0f ,0.0f  , "TestUser1", true, false);
        List<Bin> bins = List.of(bin1);

        when(binsRepository.findByNeedEmptying(city)).thenReturn(bins);
        doThrow(new RuntimeException("Database error")).when(binsRepository).updateBeingEmptied(anyList());

        // Execute & Verify
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> workerService.plotWorkerRoute(city));
        assertEquals("Database error", thrown.getMessage());
    }
}
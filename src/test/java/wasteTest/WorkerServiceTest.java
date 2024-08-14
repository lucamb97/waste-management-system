package wasteTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static wasteManagement.configuration.utils.Constants.MAX_ROUTE_STOPS;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import wasteManagement.model.entities.Bin;
import wasteManagement.model.repositorys.BinsRepository;
import wasteManagement.services.WorkerService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
class WorkerServiceTest {

    @Mock
    private BinsRepository binsRepository;

    @InjectMocks
    private WorkerService workerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    //Test the internal logic of binEmptied() method.
    @Test
    void testBinEmptied_Success() {
        //No exception should be thrown during a successful database call
        doNothing().when(binsRepository).binEmptied(anyLong());

        workerService.binEmptied(1L);

        //Verify that the repository was called exactly once
        verify(binsRepository, times(1)).binEmptied(1L);
    }

    @Test
    void testBinEmptied_ErrorHandling() {
        //Simulate a database error
        doThrow(new RuntimeException("Database error")).when(binsRepository).binEmptied(anyLong());

        //Verify that the exception is properly logged
        assertThrows(RuntimeException.class, () -> workerService.binEmptied(1L));

        //Verify that the repository was called and the error was logged
        verify(binsRepository, times(1)).binEmptied(1L);
    }

    //Test the internal logic of plotWorkerRoute() method
    @Test
    void testPlotWorkerRoute_Success() {
        //Set up mocks
        Bin bin1 = new Bin(1L, 100, "CityA", "Road1", "1", 10.0f, 20.0f, null, true, false, "OK");
        Bin bin2 = new Bin(2L, 150, "CityA", "Road2", "2", 10.4f, 20.1f, null, true, false, "OK");
        Bin bin3 = new Bin(3L, 200, "CityA", "Road3", "3", 10.1f, 20.2f, null, true, false, "OK");
        List<Bin> mockBins = Arrays.asList(bin1, bin2, bin3);

        when(binsRepository.findByNeedEmptying("CityA")).thenReturn(mockBins);

        //Call the method and verify internal logic
        List<Bin> route = workerService.plotWorkerRoute("CityA");

        //Verify the route was calculated correctly (bin1 -> bin3 -> bin2)
        assertEquals(3, route.size());
        assertEquals(bin1, route.get(0));
        assertEquals(bin3, route.get(1));
        assertEquals(bin2, route.get(2));

        //Verify that the repository was called
        verify(binsRepository, times(1)).updateBeingEmptied(Arrays.asList(1L, 3L, 2L));
    }

    @Test
    void testPlotWorkerRoute_EmptyBinList() {
        //Mock the repository to return an empty list
        when(binsRepository.findByNeedEmptying("CityA")).thenReturn(Collections.emptyList());

        //Call the method and verify that it returns null when no bins need emptying
        List<Bin> route = workerService.plotWorkerRoute("CityA");
        assertNull(route);

        //Verify that the repository method was called
        verify(binsRepository, times(1)).findByNeedEmptying("CityA");

        //Verify that the repository was never called
        verify(binsRepository, never()).updateBeingEmptied(anyList());
    }

    @Test
    void testPlotWorkerRoute_ErrorHandling() {
        //Set up mocks
        Bin bin1 = new Bin(1L, 100, "CityA", "Road1", "1", 10.0f, 20.0f, null, true, false, "OK");
        Bin bin2 = new Bin(2L, 150, "CityA", "Road2", "2", 10.1f, 20.1f, null, true, false, "OK");

        List<Bin> mockBins = Arrays.asList(bin1, bin2);

        when(binsRepository.findByNeedEmptying("CityA")).thenReturn(mockBins);

        //Simulate a database error when updating the bins status
        doThrow(new RuntimeException("Database error")).when(binsRepository).updateBeingEmptied(anyList());

        //Verify that the exception is properly logged
        assertThrows(RuntimeException.class, () -> workerService.plotWorkerRoute("CityA"));

        //Verify that the repository was called
        verify(binsRepository, times(1)).findByNeedEmptying("CityA");
        verify(binsRepository, times(1)).updateBeingEmptied(Arrays.asList(1L, 2L));
    }

    @Test
    void testPlotWorkerRoute_MaxRouteStops() {
        //Set up mor than MAX_ROUTE_STOPS bins to test that the route is limited to MAX_ROUTE_STOPS
        List<Bin> mockBins = new ArrayList<>();
        for (int i = 1; i <= MAX_ROUTE_STOPS + 5; i++) {
            mockBins.add(new Bin((long) i, 100, "CityA", "Road" + i, String.valueOf(i), 10.0f + i, 20.0f + i, null, true, false, "OK"));
        }

        //Mock the repository to return the bins
        when(binsRepository.findByNeedEmptying("CityA")).thenReturn(mockBins);

        //Call the method and verify that the route is limited to MAX_ROUTE_STOPS
        List<Bin> route = workerService.plotWorkerRoute("CityA");

        //Verify that the route size is limited to MAX_ROUTE_STOPS
        assertEquals(MAX_ROUTE_STOPS, route.size());

        //Verify that the repository method to update the bins status was called for MAX_ROUTE_STOPS bins
        List<Long> expectedRouteIds = mockBins.subList(0, MAX_ROUTE_STOPS)
                .stream()
                .map(Bin::getId)
                .collect(Collectors.toList());
        verify(binsRepository, times(1)).updateBeingEmptied(expectedRouteIds);
    }
}

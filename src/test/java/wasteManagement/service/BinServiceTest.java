package wasteManagement.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wasteManagement.model.entities.Bin;
import wasteManagement.model.repositorys.BinsRepository;
import wasteManagement.services.BinService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class BinServiceTest {

    @Mock
    private BinsRepository binsRepository;
    @InjectMocks
    private BinService binService;

    private Bin bin1;
    private Bin bin2;

    //Setting the bins used in test
    @BeforeEach
    void setUp() {
        bin1 = new Bin();
        bin1.setId(1L);
        bin1.setCity("TestCity");
        bin1.setUser("user1");

        bin2 = new Bin();
        bin2.setId(2L);
        bin2.setCity("TestCity");
        bin2.setUser("user2");
    }

    //Test the GetBinsByCity method
    @Test
    void testGetBinsByCity() {
        //Setup mocks
        when(binsRepository.findByCity("TestCity")).thenReturn(Arrays.asList(bin1, bin2));

        List<Bin> bins = binService.getBinsByCity("TestCity");
        //Verifications
        assertEquals(2, bins.size());
        assertEquals("TestCity", bins.get(0).getCity());
        verify(binsRepository, times(1)).findByCity("TestCity");
    }

    //Test the GetBinsById method
    @Test
    void testGetBinById() {
        //Setup mocks
        when(binsRepository.findById(1L)).thenReturn(Optional.of(bin1));

        Bin bin = binService.getBinById(1L).get();
        //verifications
        assertNotNull(bin);
        assertEquals(1L, bin.getId());
        verify(binsRepository, times(1)).findById(1L);
    }

    //Test the GetBinsByUser method
    @Test
    void testGetBinByUser() {
        //Setup mocks
        when(binsRepository.findByUser("user1")).thenReturn(Arrays.asList(bin1));

        List<Bin> bins = binService.getBinByUser("user1");
        //Verifications
        assertEquals(1, bins.size());
        assertEquals("user1", bins.get(0).getUser());
        verify(binsRepository, times(1)).findByUser("user1");
    }

    //Test the AddBins method
    @Test
    void testAddBins() {
        //Setup mocks
        List<Bin> binsToAdd = Arrays.asList(bin1, bin2);

        binService.addBins(binsToAdd);
        //Verifications
        verify(binsRepository, times(1)).saveAll(binsToAdd);
    }

    //Test the DeleteBins method
    @Test
    void testDeleteBins() {
        //Setup mocks
        List<Long> idsToDelete = Arrays.asList(1L, 2L);

        binService.deleteBins(idsToDelete);
        //Verifications
        verify(binsRepository, times(1)).deleteAllById(idsToDelete);
    }

    @Test
    public void testBinFull_Success() {
        Long binId = bin1.getId();
        when(binsRepository.findById(binId)).thenReturn(Optional.of(bin1));

        binService.binFull(binId);

        assertTrue(bin1.getNeedsEmptying());
        verify(binsRepository, times(1)).save(bin1);
    }

    @Test
    public void testBinFull_BinNotFound() {
        Long binId = 1L;

        when(binsRepository.findById(binId)).thenReturn(Optional.empty());

        binService.binFull(binId);

        verify(binsRepository, never()).save(any(Bin.class));
    }
}

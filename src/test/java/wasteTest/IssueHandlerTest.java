package wasteTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wasteManagement.model.entities.Bin;
import wasteManagement.model.entities.issues.*;
import wasteManagement.model.repositorys.BinsRepository;
import wasteManagement.model.repositorys.IssueRepository;
import wasteManagement.services.IssueHandler;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class IssueHandlerTest {

    @Mock
    private BinsRepository binsRepository;
    @Mock
    private IssueRepository issueRepository;
    @InjectMocks
    private IssueHandler issueHandler;

    private BrokenBinIssue brokenBinIssue;
    private Bin bin;

    @BeforeEach
    void setUp() {
        brokenBinIssue = new BrokenBinIssue();
        brokenBinIssue.setId(1L);
        brokenBinIssue.setBinId(1L);
        brokenBinIssue.setCity("TestCity");

        bin = new Bin();
        bin.setId(1L);
        bin.setStatus("BROKEN");
    }

    @Test
    void testHandleIssue_BrokenBinFixed() {
        //Setup mocks
        when(issueRepository.findById(1L)).thenReturn(Optional.of(brokenBinIssue));
        doReturn(Optional.of(bin)).when(binsRepository).findById(bin.getId());

        issueHandler.handleIssue(1L, true);
        //Verifications
        assertEquals("WORKING", bin.getStatus());
        assertTrue(brokenBinIssue.isResolved());
        verify(binsRepository, times(1)).save(bin);
        verify(issueRepository, times(1)).save(brokenBinIssue);
    }

    @Test
    void testHandleIssue_BrokenBinNotFixed() {
        //Setup mocks
        when(issueRepository.findById(1L)).thenReturn(Optional.of(brokenBinIssue));

        issueHandler.handleIssue(1L, false);
        //Verifications
        verify(binsRepository, times(1)).deleteById(1L);
        verify(issueRepository, times(1)).delete(brokenBinIssue);
        verify(issueRepository, times(1)).save(any(MissingBinIssue.class));
    }

    @Test
    void testHandleIssue_MissingBinHandler() {
        //Setup mocks
        MissingBinIssue missingBinIssue = new MissingBinIssue();
        missingBinIssue.setId(1L);
        missingBinIssue.setBinId(1L);
        missingBinIssue.setCity("TestCity");

        when(issueRepository.findById(1L)).thenReturn(Optional.of(missingBinIssue));
        doReturn(Optional.of(bin)).when(binsRepository).findById(bin.getId());

        issueHandler.handleIssue(1L, null);
        //Verifications
        assertTrue(missingBinIssue.isResolved());
        verify(issueRepository, times(1)).save(missingBinIssue);
    }

    @Test
    void testHandleIssue_NeedEmptyHandler() {
        //Setup mocks
        NeedEmergencyEmptyIssue needEmptyIssue = new NeedEmergencyEmptyIssue();
        needEmptyIssue.setId(1L);
        needEmptyIssue.setBinId(1L);
        needEmptyIssue.setCity("TestCity");
        when(issueRepository.findById(1L)).thenReturn(Optional.of(needEmptyIssue));
        doReturn(Optional.of(bin)).when(binsRepository).findById(bin.getId());

        issueHandler.handleIssue(1L, null);
        //Verifications
        assertTrue(needEmptyIssue.isResolved());
        verify(binsRepository, times(1)).save(bin);
        verify(issueRepository, times(1)).save(needEmptyIssue);
    }

    @Test
    void testHandleIssue_NeedRemovalHandler() {
        //Setup mocks
        NeedRemovalIssue needRemovalIssue = new NeedRemovalIssue();
        needRemovalIssue.setId(1L);
        needRemovalIssue.setBinId(1L);
        needRemovalIssue.setCity("TestCity");
        when(issueRepository.findById(1L)).thenReturn(Optional.of(needRemovalIssue));

        issueHandler.handleIssue(1L, null);
        //Verifications
        assertTrue(needRemovalIssue.isResolved());
        verify(binsRepository, times(1)).deleteById(1L);
        verify(issueRepository, times(1)).save(needRemovalIssue);
    }

    @Test
    void testHandleIssue_IssueNotFound() {
        //Setup mocks
        when(issueRepository.findById(1L)).thenReturn(Optional.empty());
        //Verifications
        assertThrows(EntityNotFoundException.class, () -> issueHandler.handleIssue(1L, true));
    }
}

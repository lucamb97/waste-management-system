package wasteManagement.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wasteManagement.model.entities.Bin;
import wasteManagement.model.entities.issues.BrokenBinIssue;
import wasteManagement.model.entities.issues.Issue;
import wasteManagement.model.entities.issues.IssueFactory;
import wasteManagement.model.entities.issues.MissingBinIssue;
import wasteManagement.model.entities.observer.Observer;
import wasteManagement.model.repositorys.BinsRepository;
import wasteManagement.model.repositorys.IssueRepository;
import wasteManagement.model.repositorys.UserRepository;
import wasteManagement.model.utils.IssueRequest;
import wasteManagement.services.IssueTracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IssueTrackerTest {

    @InjectMocks
    private IssueTracker issueTracker;
    @Mock
    private IssueRepository issueRepository;
    @Mock
    private BinsRepository binsRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private IssueFactory issueFactory;
    @Mock
    private Observer observer1;
    @Mock
    private Observer observer2;

    @BeforeEach
    void setUp() {
        // Assuming that observer1 and observer2 are observers that need to be added
        issueTracker.addObserver(observer1);
        issueTracker.addObserver(observer2);
    }

    @Test
    void testCreateIssue_Success() {
        IssueRequest request = new IssueRequest();
        request.setCity("TestCity");
        request.setBinId(1L);
        request.setType("BROKEN_BIN");
        request.setUsername("testUser");
        request.setDescription("Test Description");

        Bin bin = new Bin();
        bin.setId(1L);
        bin.setCity("TestCity");

        Issue issue = new BrokenBinIssue();

        // Use doReturn() instead of when().thenReturn() to avoid stubbing issues
        doReturn(Optional.of(bin)).when(binsRepository).findById(request.getBinId());
        doReturn(issue).when(issueFactory).createIssue(request.getType(), request.getBinId());

        issueTracker.createIssue(request);

        verify(issueRepository, times(1)).save(issue);
        verify(observer1, times(1)).update(issue);
        verify(observer2, times(1)).update(issue);
    }

    @Test
    void testCreateIssue_BinNotFound() {
        IssueRequest request = new IssueRequest();
        request.setCity("TestCity");
        request.setBinId(1L);
        request.setType("BROKEN_BIN");

        // Use doReturn() to mock an empty Optional, simulating bin not found
        doReturn(Optional.empty()).when(binsRepository).findById(request.getBinId());

        assertThrows(EntityNotFoundException.class, () -> issueTracker.createIssue(request));

        verify(issueRepository, never()).save(any());
        verify(observer1, never()).update(any());
        verify(observer2, never()).update(any());
    }

    @Test
    void testAssignIssue_Success() {
        String worker = "worker1";
        Long issueId = 1L;

        Issue issue = mock(Issue.class);
        Bin bin = mock(Bin.class);

        // Mock the getBinId method
        doReturn(1L).when(issue).getBinId();

        // Mock the repository methods to return the expected values
        doReturn(Optional.of(issue)).when(issueRepository).findById(issueId);
        doReturn(Optional.of(bin)).when(binsRepository).findById(1L);

        // Call the method under test
        Bin assignedBin = issueTracker.assignIssue(worker, issueId);

        // Assertions
        assertNotNull(assignedBin);
        assertEquals(bin, assignedBin); // Verify the returned bin is the one we set up in the mock

        // Verify interactions
        verify(issueRepository, times(1)).findById(issueId);
        verify(binsRepository, times(1)).findById(1L);
        verify(issueRepository, times(1)).assignIssueToWorker(worker, issueId);
    }


    @Test
    void testAssignIssue_IssueNotFound() {
        String worker = "worker1";
        Long issueId = 1L;

        // Mocking an empty Optional to simulate issue not found
        doReturn(Optional.empty()).when(issueRepository).findById(issueId);

        // Expecting an EntityNotFoundException when the issue is not found
        assertThrows(EntityNotFoundException.class, () -> issueTracker.assignIssue(worker, issueId));

        // Verify
        verify(issueRepository, never()).assignIssueToWorker(anyString(), anyLong());
        verify(binsRepository, never()).findById(anyLong());
    }

    @Test
    void testAssignIssue_BinNotFound() {
        String worker = "worker1";
        Long issueId = 1L;
        Issue issue = mock(Issue.class);
        Long binId = 1L;
        doReturn(binId).when(issue).getBinId();
        doReturn(Optional.of(issue)).when(issueRepository).findById(issueId);
        // Mock the binsRepository to return an empty Optional to simulate bin not found
        doReturn(Optional.empty()).when(binsRepository).findById(binId);

        // Expecting an EntityNotFoundException when the bin is not found
        assertThrows(EntityNotFoundException.class, () -> issueTracker.assignIssue(worker, issueId));

        verify(issueRepository, never()).assignIssueToWorker(worker, issueId);
        verify(binsRepository, times(1)).findById(binId);
    }

    @Test
    void testHandleIssue_Found() {
        // Setup mocks
        Issue mockIssue = mock(Issue.class);
        when(issueRepository.findById(1L)).thenReturn(Optional.of(mockIssue));

        // Call the method
        issueTracker.handleIssue(1L, true);

        // Verify the issue's handle method is called with the right arguments
        verify(mockIssue, times(1)).handle(binsRepository, issueRepository, issueFactory, true);

        // Verify the issue is saved
        verify(issueRepository, times(1)).save(mockIssue);
    }

    @Test
    void testHandleIssue_NotFound() {
        // Setup mock behavior
        Issue mockIssue = mock(Issue.class);
        when(issueRepository.findById(1L)).thenReturn(Optional.empty());

        // Assert that the method throws EntityNotFoundException
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            issueTracker.handleIssue(1L, true);
        });

        // Verify the exception message
        assertEquals("Couldn't find issue", exception.getMessage());

        // Verify that handle and save methods are never called
        verify(mockIssue, never()).handle(any(), any(), any(), anyBoolean());
        verify(issueRepository, never()).save(any());
    }

    @Test
    void testCheckIssues_Found() {
        // Setup mocks
        List<Issue> mockIssues = new ArrayList<>();
        Issue issue1 = new BrokenBinIssue();
        issue1.setId(1L);
        issue1.setCreatedBy("Test_User");
        issue1.setIssueDescription("Broken bin issue");

        Issue issue2 = new MissingBinIssue();
        issue2.setId(2L);
        issue2.setCreatedBy("Test_User");
        issue2.setIssueDescription("Missing bin issue");

        mockIssues.add(issue1);
        mockIssues.add(issue2);
        when(issueRepository.findByUsername("Test_User")).thenReturn(mockIssues);

        List<Issue> issues = issueTracker.checkIssues("Test_User");

        // Verify the method returns the correct list
        assertNotNull(issues);
        assertEquals(2, issues.size());
        verify(issueRepository, times(1)).findByUsername("Test_User");
    }

    @Test
    void testCheckIssues_NotFound() {
        // Setup mocks
        when(issueRepository.findByUsername("Test_User")).thenReturn(Collections.emptyList());

        // Assert throws EntityNotFoundException
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            issueTracker.checkIssues("Test_User");
        });

        // Verify
        assertEquals("Couldn't find issue", exception.getMessage());
        verify(issueRepository, times(1)).findByUsername("Test_User");
    }
}

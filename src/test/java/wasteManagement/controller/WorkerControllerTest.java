package wasteManagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import wasteManagement.model.entities.Bin;
import wasteManagement.services.IssueTracker;
import wasteManagement.services.WorkerService;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class WorkerControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private WorkerService workerService;
    @MockBean
    private IssueTracker issueTracker;
    @Autowired
    private ObjectMapper objectMapper;

    private Bin bin;
    private List<Bin> bins;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        bin = new Bin();
        bin.setId(1L);
        bin.setCapacity(100);
        bin.setCity("TestCity");
        bin.setRoad("TestRoad");
        bin.setCivicNumber("123");
        bin.setLongitude(12.34F);
        bin.setLatitude(56.78F);
        bin.setUser("testUser");
        bin.setNeedsEmptying(true);
        bin.setBeingEmptied(false);
        bin.setStatus("ACTIVE");

        bins = Collections.singletonList(bin);
    }

    @Test
    @WithMockUser(roles = "WORKER")
    public void testGetRoute_Success() throws Exception {
        when(workerService.plotWorkerRoute("TestCity")).thenReturn(bins);

        mockMvc.perform(MockMvcRequestBuilders.get("/worker/getRoute")
                        .param("city", "TestCity")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(bin.getId()));

        verify(workerService, times(1)).plotWorkerRoute("TestCity");
    }

    @Test
    @WithMockUser(roles = "WORKER")
    public void testGetRoute_Error() throws Exception {
        when(workerService.plotWorkerRoute("TestCity")).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(MockMvcRequestBuilders.get("/worker/getRoute")
                        .param("city", "TestCity")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());

        verify(workerService, times(1)).plotWorkerRoute("TestCity");
    }

    @Test
    @WithMockUser(roles = "WORKER")
    public void testBinEmptied_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/worker/binEmptied")
                        .param("id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(workerService, times(1)).binEmptied(1L);
    }

    @Test
    @WithMockUser(roles = "WORKER")
    public void testBinEmptied_Error() throws Exception {
        doThrow(new RuntimeException("Error")).when(workerService).binEmptied(1L);

        mockMvc.perform(MockMvcRequestBuilders.put("/worker/binEmptied")
                        .param("id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());

        verify(workerService, times(1)).binEmptied(1L);
    }

    @Test
    @WithMockUser(roles = "WORKER")
    public void testAssignIssue_Success() throws Exception {
        when(issueTracker.assignIssue("worker1", 1L)).thenReturn(bin);

        mockMvc.perform(MockMvcRequestBuilders.put("/worker/assignIssue")
                        .param("worker", "worker1")
                        .param("issueId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(bin.getId()));

        verify(issueTracker, times(1)).assignIssue("worker1", 1L);
    }

    @Test
    @WithMockUser(roles = "WORKER")
    public void testAssignIssue_Error() throws Exception {
        doThrow(new RuntimeException("Error")).when(issueTracker).assignIssue("worker1", 1L);

        mockMvc.perform(MockMvcRequestBuilders.put("/worker/assignIssue")
                        .param("worker", "worker1")
                        .param("issueId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());

        verify(issueTracker, times(1)).assignIssue("worker1", 1L);
    }

    @Test
    @WithMockUser(roles = "WORKER")
    public void testHandleIssue_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/worker/handleIssue")
                        .param("issueId", "1")
                        .param("fixed", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(issueTracker, times(1)).handleIssue(1L, true);
    }

    @Test
    @WithMockUser(roles = "WORKER")
    public void testHandleIssue_NotFound() throws Exception {
        doThrow(new EntityNotFoundException()).when(issueTracker).handleIssue(1L, true);

        mockMvc.perform(MockMvcRequestBuilders.get("/worker/handleIssue")
                        .param("issueId", "1")
                        .param("fixed", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Issue id not found"));

        verify(issueTracker, times(1)).handleIssue(1L, true);
    }

    @Test
    @WithMockUser(roles = "WORKER")
    public void testHandleIssue_Error() throws Exception {
        doThrow(new RuntimeException("Error")).when(issueTracker).handleIssue(1L, true);

        mockMvc.perform(MockMvcRequestBuilders.get("/worker/handleIssue")
                        .param("issueId", "1")
                        .param("fixed", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Couldn't complete issue handling"));

        verify(issueTracker, times(1)).handleIssue(1L, true);
    }
}

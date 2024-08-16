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
import wasteManagement.model.utils.IssueRequest;
import wasteManagement.services.IssueTracker;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private IssueTracker issueTracker;
    @Autowired
    private ObjectMapper objectMapper;
    private IssueRequest issueRequest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        issueRequest = new IssueRequest();
        issueRequest.setCity("TestCity");
        issueRequest.setBinId(1L);
        issueRequest.setType("BROKEN_BIN");
        issueRequest.setUsername("testUser");
        issueRequest.setDescription("Test Description");
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testCreateIssue_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/user/createIssue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issueRequest)))
                .andExpect(status().isOk());

        verify(issueTracker, times(1)).createIssue(any(IssueRequest.class));
    }


    @Test
    @WithMockUser(roles = "USER")
    public void testCreateIssue_EntityNotFound() throws Exception {

        doThrow(new EntityNotFoundException()).when(issueTracker).createIssue(any(IssueRequest.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/user/createIssue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issueRequest)))
                .andExpect(status().isNotAcceptable());

        verify(issueTracker, times(1)).createIssue(any(IssueRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testCreateIssue_Error() throws Exception {

        doThrow(new RuntimeException()).when(issueTracker).createIssue(any(IssueRequest.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/user/createIssue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issueRequest)))
                .andExpect(status().isInternalServerError());

        verify(issueTracker, times(1)).createIssue(any(IssueRequest.class));
    }
}

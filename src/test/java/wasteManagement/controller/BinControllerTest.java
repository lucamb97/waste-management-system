package wasteManagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import wasteManagement.services.BinService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BinControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BinService binService;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(roles = "WORKER")
    public void testAddBins_Success() throws Exception {
        //Setup mocks
        List<Bin> bins = Arrays.asList(
                new Bin(null, 100, "New York", "5th Ave", "12", 40.7128f, -74.0060f, "user1", false, false, "active"),
                new Bin(null, 200, "Los Angeles", "Sunset Blvd", "101", 34.0522f, -118.2437f, "user2", true, false, "active")
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/bins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bins)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @WithMockUser(roles = "WORKER")
    public void testAddBins_Error() throws Exception {
        //Setup mocks
        List<Bin> bins = Arrays.asList(
                new Bin(null, 100, "New York", "5th Ave", "12", 40.7128f, -74.0060f, "user1", false, false, "active"),
                new Bin(null, 200, "Los Angeles", "Sunset Blvd", "101", 34.0522f, -118.2437f, "user2", true, false, "active")
        );

        doThrow(new RuntimeException("Error")).when(binService).addBins(anyList());

        mockMvc.perform(MockMvcRequestBuilders.post("/bins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bins)))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "WORKER")
    public void testGetAllCityBins_Success() throws Exception {
        //Setup mocks
        List<Bin> bins = Arrays.asList(
                new Bin(1L, 100, "New York", "5th Ave", "12", 40.7128f, -74.0060f, "user1", false, false, "active"),
                new Bin(2L, 200, "New York", "6th Ave", "15", 40.7128f, -74.0060f, "user2", true, false, "active")
        );
        when(binService.getBinsByCity("New York")).thenReturn(bins);

        mockMvc.perform(MockMvcRequestBuilders.get("/bins")
                        .param("city", "New York"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].city").value("New York"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].city").value("New York"));
    }

    @Test
    @WithMockUser(roles = "WORKER")
    public void testGetAllCityBins_NoBinsFound() throws Exception {
        //Setup mocks
        when(binService.getBinsByCity("New York")).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/bins")
                        .param("city", "New York"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "WORKER")
    public void testGetAllCityBins_Error() throws Exception {
        //Setup mocks
        when(binService.getBinsByCity("New York")).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(MockMvcRequestBuilders.get("/bins")
                        .param("city", "New York"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "WORKER")
    public void testGetBinById_Success() throws Exception {
        //Setup mocks
        Bin bin = new Bin(1L, 100, "New York", "5th Ave", "12", 40.7128f, -74.0060f, "user1", false, false, "active");
        when(binService.getBinById(1L)).thenReturn(Optional.of(bin));

        mockMvc.perform(MockMvcRequestBuilders.get("/bins/id/{id}", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.city").value("New York"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.road").value("5th Ave"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.civicNumber").value("12"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.longitude").value(40.7128))
                .andExpect(MockMvcResultMatchers.jsonPath("$.latitude").value(-74.0060))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user").value("user1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.needsEmptying").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.beingEmptied").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("active"));
    }

    @Test
    @WithMockUser(roles = "WORKER")
    public void testGetBinById_NotFound() throws Exception {
        //Setup mocks
        when(binService.getBinById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/bins/id/{id}", 1L))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "WORKER")
    public void testGetBinById_Error() throws Exception {
        //Setup mocks
        when(binService.getBinById(1L)).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(MockMvcRequestBuilders.get("/bins/id/{id}", 1L))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "WORKER")
    public void testGetBinByUser_Success() throws Exception {
        //Setup mocks
        List<Bin> bins = Arrays.asList(
                new Bin(1L, 100, "New York", "5th Ave", "12", 40.7128f, -74.0060f, "user1", false, false, "active"),
                new Bin(2L, 200, "New York", "6th Ave", "15", 40.7128f, -74.0060f, "user1", true, false, "active")
        );
        when(binService.getBinByUser("user1")).thenReturn(bins);

        mockMvc.perform(MockMvcRequestBuilders.get("/bins/user/{user}", "user1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].city").value("New York"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].city").value("New York"));
    }

    @Test
    @WithMockUser(roles = "WORKER")
    public void testGetBinByUser_NoBinsFound() throws Exception {
        //Setup mocks
        when(binService.getBinByUser("user1")).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/bins/user/{user}", "user1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "WORKER")
    public void testGetBinByUser_Error() throws Exception {
        //Setup mocks
        when(binService.getBinByUser("user1")).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(MockMvcRequestBuilders.get("/bins/user/{user}", "user1"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "WORKER")
    public void testDeleteBins_Success() throws Exception {
        //Setup mocks
        List<Long> ids = Arrays.asList(1L, 2L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/bins/deleteBins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(MockMvcResultMatchers.status().isAccepted());
    }

    @Test
    @WithMockUser(roles = "WORKER")
    public void testDeleteBins_Error() throws Exception {
        //Setup mocks
        List<Long> ids = Arrays.asList(1L, 2L);
        //Throw the exception
        doThrow(new RuntimeException("Error")).when(binService).deleteBins(anyList());

        mockMvc.perform(MockMvcRequestBuilders.delete("/bins/deleteBins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }
}

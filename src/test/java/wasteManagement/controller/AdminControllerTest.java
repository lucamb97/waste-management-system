package wasteManagement.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import wasteManagement.model.entities.UserInfo;
import wasteManagement.model.utils.RegisterRequest;
import wasteManagement.services.AdminService;
import wasteManagement.services.AuthService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AdminService adminService;
    @MockBean
    private AuthService authService;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testRegisterWorker_Success() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testuser", "password", "WORKER", "New York");

        doNothing().when(authService).register(any(RegisterRequest.class), anyString());

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/registerAnyRole")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("User registered successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testRegisterWorker_UserExists() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testuser", "password", "WORKER", "New York");

        doThrow(new AuthenticationException("User already exists") {}).when(authService)
                .register(any(RegisterRequest.class), anyString());

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/registerAnyRole")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().string("User already exists"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetAllUsersInfo_Success() throws Exception {
        List<UserInfo> users = new ArrayList<>();
        UserInfo user = new UserInfo();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEnabled(true);
        user.setCity("New York");
        user.setAuthorities(new ArrayList<>());
        users.add(user);

        when(adminService.getAllUsers()).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/getAllUsersInfo"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(users)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetAllUsersInfo_NoUsersFound() throws Exception {
        when(adminService.getAllUsers()).thenThrow(new NoSuchElementException("No users found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/getAllUsersInfo"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetUserRoles_Success() throws Exception {
        List<String> roles = new ArrayList<>();
        roles.add("WORKER");

        when(adminService.getUserRoles("testuser")).thenReturn(roles);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/getUserRoles")
                        .param("username", "testuser"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(roles)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetUserRoles_NoSuchUser() throws Exception {
        when(adminService.getUserRoles("testuser")).thenThrow(new NoSuchElementException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/getUserRoles")
                        .param("username", "testuser"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAddRole_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/admin/addRole")
                        .param("username", "testuser")
                        .param("role", "MANAGER"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Role added to user"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAddRole_InvalidRole() throws Exception {
        doThrow(new IllegalArgumentException("Invalid role")).when(adminService).addRoleToUser("testuser", "INVALID_ROLE");

        mockMvc.perform(MockMvcRequestBuilders.put("/admin/addRole")
                        .param("username", "testuser")
                        .param("role", "INVALID_ROLE"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Invalid role provided"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testRemoveRole_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/removeRole")
                        .param("username", "testuser")
                        .param("role", "WORKER"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Role removed from user"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testRemoveRole_NoSuchUser() throws Exception {
        doThrow(new NoSuchElementException("User not found")).when(adminService).removeRoleFromUser("testuser", "WORKER");

        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/removeRole")
                        .param("username", "testuser")
                        .param("role", "WORKER"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("User not found"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteUser_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/deleteUser")
                        .param("username", "testuser"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User deleted"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteUser_NoSuchUser() throws Exception {
        doThrow(new NoSuchElementException("User not found")).when(adminService).deleteUser("testuser");

        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/deleteUser")
                        .param("username", "testuser"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Could not find any users"));
    }
}
package wasteManagement.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import wasteManagement.configuration.utils.JwtUtils;
import wasteManagement.model.utils.LoginResponse;
import wasteManagement.model.utils.RegisterRequest;
import wasteManagement.services.AuthService;

import java.util.Collections;

import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthService authService;
    @MockBean
    private UserDetailsService userDetailsService;
    @MockBean
    private JwtUtils jwtUtils;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private JdbcUserDetailsManager jdbcUserDetailsManager;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegister_Success() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testuser", "password", "USER", "New York");

        // Ensure that the register method does not throw an exception
        doNothing().when(authService).register(any(RegisterRequest.class), anyString());

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User registered successfully"));
    }

    @Test
    public void testRegister_UserAlreadyExists() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testuser", "password", "USER", "New York");

        doThrow(new AuthenticationException("User already exists") {}).when(authService)
                .register(any(RegisterRequest.class), anyString());

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().string("User already exists"));
    }

    @Test
    public void testRegister_InvalidRole() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testuser", "password", "INVALID_ROLE", "New York");

        doThrow(new IllegalArgumentException("Role is not valid")).when(authService)
                .register(any(RegisterRequest.class), anyString());

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().string("Role is not valid"));
    }

    @Test
    public void testRegister_UnexpectedError() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testuser", "password", "USER", "New York");

        doThrow(new RuntimeException("Unexpected error")).when(authService)
                .register(any(RegisterRequest.class), anyString());

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Error during registration"));
    }

    @Test
    public void testAuthenticateUser_Success() throws Exception {
        LoginResponse loginResponse = new LoginResponse("username", Collections.singletonList("USER"),"token");

        when(authService.userLogin("testuser", "password")).thenReturn(loginResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .param("username", "testuser")
                        .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.jwtToken").value("token"));
    }

    @Test
    public void testAuthenticateUser_BadCredentials() throws Exception {
        doThrow(new AuthenticationException("Bad credentials") {}).when(authService)
                .userLogin(anyString(), anyString());

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .param("username", "testuser")
                        .param("password", "wrongpassword"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Bad credentials"));
    }
}

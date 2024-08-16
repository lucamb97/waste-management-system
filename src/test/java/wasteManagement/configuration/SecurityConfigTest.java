package wasteManagement.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testPublicEndpointsAccessible() throws Exception {
        mockMvc.perform(get("/swagger-ui"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    // Assert that the status is not 401 Unauthorized
                    assertNotEquals(HttpStatus.UNAUTHORIZED.value(), status);
                });

        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    // Assert that the status is not 401 Unauthorized
                    assertNotEquals(HttpStatus.UNAUTHORIZED.value(), status);
                });

        mockMvc.perform(get("/auth/login"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    // Assert that the status is not 401 Unauthorized
                    assertNotEquals(HttpStatus.UNAUTHORIZED.value(), status);
                });
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testAuthenticatedUserCanAccessUserEndpoints() throws Exception {
        mockMvc.perform(get("/user/createIssue"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    // Assert that the status is not 401 Unauthorized
                    assertNotEquals(HttpStatus.UNAUTHORIZED.value(), status);
                });
    }

    @Test
    @WithMockUser(roles = "WORKER")
    public void testWorkerCanAccessWorkerAndBinEndpoints() throws Exception {
        mockMvc.perform(get("/worker/getRoute"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    // Assert that the status is not 401 Unauthorized
                    assertNotEquals(HttpStatus.UNAUTHORIZED.value(), status);
                });

        mockMvc.perform(get("/bins"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    // Assert that the status is not 401 Unauthorized
                    assertNotEquals(HttpStatus.UNAUTHORIZED.value(), status);
                });
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAdminCanAccessAdminEndpoints() throws Exception {
        mockMvc.perform(get("/admin/addRole"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    // Assert that the status is not 401 Unauthorized
                    assertNotEquals(HttpStatus.UNAUTHORIZED.value(), status);
                });
    }

    @Test
    public void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/user/createIssue"))
                .andExpect(status().isUnauthorized());
    }
}

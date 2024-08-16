package wasteManagement.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import wasteManagement.configuration.utils.JwtUtils;
import wasteManagement.model.entities.Authority;
import wasteManagement.model.entities.UserInfo;
import wasteManagement.model.repositorys.AuthorityRepository;
import wasteManagement.model.repositorys.UserRepository;
import wasteManagement.model.utils.LoginResponse;
import wasteManagement.model.utils.RegisterRequest;
import wasteManagement.services.AuthService;
import wasteManagement.services.IssueTracker;


@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private JdbcUserDetailsManager jdbcUserDetailsManager;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private IssueTracker issueTracker;
    @InjectMocks
    private AuthService authService;
    @Mock
    private AuthorityRepository authorityRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    void testUserLogin_Success() {
        // Arrange
        String username = "user1";
        String password = "password";
        UserDetails userDetails = User.withUsername(username)
                .password(password)
                .roles("USER")
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());

        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(jwtUtils.generateTokenFromUsername(userDetails)).thenReturn("jwtToken");

        LoginResponse response = authService.userLogin(username, password);

        //Verify the successful login
        assertEquals(username, response.getUsername());
        assertTrue(response.getRoles().contains("ROLE_USER"));
        assertEquals("jwtToken", response.getJwtToken());
        verify(authenticationManager, times(1)).authenticate(any(Authentication.class));
        verify(jwtUtils, times(1)).generateTokenFromUsername(userDetails);
    }

    @Test
    void testUserLogin_Failure() {
        //Setup mocks
        String username = "user1";
        String password = "wrongPassword";

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        //Verify the exception is thrown
        assertThrows(BadCredentialsException.class, () -> authService.userLogin(username, password));
        verify(authenticationManager, times(1)).authenticate(any(Authentication.class));
    }

    @Test
    void testRegister_Success() throws AuthenticationException {
        //Setup mocks
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user1");
        request.setPassword("password");
        request.setRole("WORKER");
        request.setCity("TestCity");

        when(jdbcUserDetailsManager.userExists(request.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");


        authService.register(request, request.getRole());
        //Verify the successful registration
        verify(userRepository, times(1)).save(any(UserInfo.class));
        verify(authorityRepository, times(1)).save(any(Authority.class));
        verify(issueTracker, times(1)).addObserver(any(UserInfo.class));
    }


    @Test
    void testRegister_UserAlreadyExists() throws AuthenticationException {
        // Set up mocks
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user1");
        request.setPassword("password");
        request.setRole("USER");
        request.setCity("TestCity");

        when(jdbcUserDetailsManager.userExists(request.getUsername())).thenReturn(true);

        //Verify the exception is thrown
        assertThrows(AuthenticationException.class, () -> authService.register(request, request.getRole()));
        verify(userRepository, never()).save(any(UserInfo.class));
        verify(authorityRepository, never()).save(any(Authority.class));
    }

    @Test
    void testRegister_InvalidRole() throws AuthenticationException {
        //Set up mocks
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user1");
        request.setPassword("password");
        request.setRole("INVALID_ROLE");
        request.setCity("TestCity");

        //Verify the exception is thrown
        assertThrows(IllegalArgumentException.class, () -> authService.register(request, request.getRole()));
        verify(userRepository, never()).save(any(UserInfo.class));
        verify(authorityRepository, never()).save(any(Authority.class));
    }
}

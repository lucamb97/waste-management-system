package wasteManagement.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import wasteManagement.configuration.utils.JwtUtils;
import wasteManagement.model.utils.LoginRequest;
import wasteManagement.model.utils.RegisterRequest;
import wasteManagement.model.utils.LoginResponse;
import wasteManagement.services.AuthService;


@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtUtils jwtUtils;

    //This is used to create a new USER, can be used without authentication
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest user) {
        try {
        authService.register(user, "USER");
        log.info("User: {} registered successfully", user.getUsername());
        return ResponseEntity.ok("User registered successfully");
        } catch (AuthenticationException e) {
            log.warn("User already exists");
            return new ResponseEntity<>("User already exists", HttpStatus.FORBIDDEN);
        } catch (IllegalArgumentException e) {
            log.error("Error during registration, invalid role");
            return new ResponseEntity<>("Role is not valid", HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            log.error("Error during registration: {}", e.getMessage());
            return new ResponseEntity<>("Error during registration", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //This is used to login and get a JWT token for authorization
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@RequestBody LoginRequest request) {
        LoginResponse response;
        try {
            response = authService.userLogin(request.getUsername(), request.getPassword());
            log.info("user: {} logged in",request.getUsername());
        } catch (AuthenticationException e) {
            log.warn("Login with bad credentials");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(response);
    }
}

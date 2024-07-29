package wasteManagement.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import wasteManagement.configuration.utils.JwtUtils;
import wasteManagement.model.utils.RegisterRequest;
import wasteManagement.model.utils.LoginResponse;
import wasteManagement.services.AuthService;


@Slf4j
@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtils jwtUtils;

    //This is used to create a new USER, can be used without authentication
    @PostMapping("/user/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest user) {
        try {
        authService.register(user, "USER");
        return ResponseEntity.ok("User registered successfully");
        } catch (AuthenticationException e) {
            log.error("User already exists");
            return new ResponseEntity<>("User already exists", HttpStatus.FORBIDDEN);
        }
        catch (Exception e) {
            log.error("Error during worker registration");
            return new ResponseEntity<>("Error during registration", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //This is used to create any other role, can only be used by admins
    @PostMapping("/admin/registerAnyRole")
    public ResponseEntity<String> registerWorker(@RequestBody RegisterRequest user) {
        try {
            authService.register(user, user.getRole());
            return ResponseEntity.ok("User registered successfully");
        } catch (AuthenticationException e) {
            log.error("User already exists");
            return new ResponseEntity<>("User already exists", HttpStatus.FORBIDDEN);
        }
        catch (Exception e) {
            log.error("Error during worker registration");
            return new ResponseEntity<>("Error during registration", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //This is used to login and get a JWT token for authorization
    @PostMapping("/user/login")
    public ResponseEntity<?> authenticateUser(@RequestParam String username, @RequestParam String password) {
        LoginResponse response;
        try {
            response = authService.userLogin(username, password);
        } catch (AuthenticationException e) {
            return new ResponseEntity<String>("Bad credentials", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(response);
    }
}

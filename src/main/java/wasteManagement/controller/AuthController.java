package wasteManagement.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import wasteManagement.configuration.utils.JwtUtil;
import wasteManagement.model.entitys.AuthenticationRequest;
import wasteManagement.services.UserService;

@Slf4j
@RestController
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public void register(@RequestBody AuthenticationRequest user) {
    }

    @PostMapping("/login")
    public String login(@RequestBody AuthenticationRequest authRequest) throws Exception {
        try {
            log.warn("hello");
        } catch (AuthenticationException e) {
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
        return jwtUtil.generateToken(authRequest.getUsername());
    }
}

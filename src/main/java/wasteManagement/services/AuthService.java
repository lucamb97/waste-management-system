package wasteManagement.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Service;
import wasteManagement.configuration.utils.JwtUtils;
import wasteManagement.model.utils.RegisterRequest;
import wasteManagement.model.utils.LoginResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private JdbcUserDetailsManager jdbcUserDetailsManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponse userLogin(String username, String password) {

        Authentication authentication;

        //authenticate the user
        authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        //generate the token
        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        return new LoginResponse(userDetails.getUsername(), roles, jwtToken);
    }

    public void register(RegisterRequest request, String role) throws AuthenticationException {
        // Check if the username already exists
        if (jdbcUserDetailsManager.userExists(request.getUsername())) {
            throw new AuthenticationException("User already exists") {
            };
        }
        // Create user details
        UserDetails newUser = User.withUsername(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(role)
                .build();

        // Save the new user in the database
        jdbcUserDetailsManager.createUser(newUser);
    }
}

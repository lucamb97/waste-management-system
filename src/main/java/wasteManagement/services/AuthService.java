package wasteManagement.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Service;
import wasteManagement.configuration.utils.JwtUtils;
import wasteManagement.model.entities.Authority;
import wasteManagement.model.entities.UserInfo;
import wasteManagement.model.repositorys.AuthorityRepository;
import wasteManagement.model.repositorys.UserRepository;
import wasteManagement.model.utils.RegisterRequest;
import wasteManagement.model.utils.LoginResponse;

import java.util.List;
import java.util.stream.Collectors;

import static wasteManagement.configuration.utils.Constants.ALLOWED_ROLES;

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
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired IssueTracker issueTracker;

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
        //Make sure we are trying to add a valid role
        role = role.toUpperCase();
        if (!ALLOWED_ROLES.contains(role)){
            throw new IllegalArgumentException("Role is not valid");
        }

        // Create user entity
        UserInfo newUser = new UserInfo();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setCity(request.getCity());
        newUser.setEnabled(true);

        // Create authority entity
        Authority authority = new Authority();
        authority.setAuthority("ROLE_" + role);
        authority.setUser(newUser);

        // Save in the database
        userRepository.save(newUser);
        authorityRepository.save(authority);

        // Add to observers if it's a worker
        if (role.equals("WORKER")) {
            issueTracker.addObserver(newUser);
        }
    }
}

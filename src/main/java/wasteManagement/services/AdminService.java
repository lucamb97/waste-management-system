package wasteManagement.services;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Service;
import wasteManagement.model.entities.Authority;
import wasteManagement.model.entities.UserInfo;
import wasteManagement.model.entities.observer.Observer;
import wasteManagement.model.repositorys.AuthorityRepository;
import wasteManagement.model.repositorys.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;

import static wasteManagement.configuration.utils.Constants.ALLOWED_ROLES;

@Slf4j
@Service
public class AdminService {

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcUserDetailsManager jdbcUserDetailsManager;

    @Autowired
    private IssueTracker issueTracker;

    public List<UserInfo> getAllUsers() throws ObjectNotFoundException {
        List<UserInfo> users = userRepository.findAll();

        if (users.isEmpty()) {
            throw new NoSuchElementException();
        }

        return users;
    }

    public void deleteUser(String username) {
        //delete authority first for foreign key constraints
        authorityRepository.deleteUser(username);
        userRepository.deleteUser(username);
    }

    public List<String> getUserRoles(String username) {
        return authorityRepository.findRolesByUsername(username);
    }

    public void addRoleToUser(String username, String newRole) {
        //make sure role is in uppercase
        newRole = newRole.toUpperCase();

        //Check if the role is valid
        if (!ALLOWED_ROLES.contains(newRole)){
            throw new IllegalArgumentException("Role is not valid");
        }

        //Check if the user exists
        UserInfo user = userRepository.findById(username).orElseThrow(() -> new NoSuchElementException("User not found"));

        // Check if the new role is already assigned
        List<String> roles = getUserRoles(username);
        if (roles.contains("ROLE_"+ newRole)) {
            throw new IllegalArgumentException("Role already assigned to the user");
        }

        Authority authority = new Authority();
        authority.setAuthority("ROLE_"+ newRole);
        authority.setUser(user);

        // Save the Authority to the database
        authorityRepository.save(authority);

        // If the new role is WORKER, add the user to issue observers
        if ("WORKER".equals(newRole)) {
            issueTracker.addObserver(user);
        }


    }

    public void removeRoleFromUser(String username, String role) {
        //make sure role is in uppercase
        role = role.toUpperCase();

        //Check if the role is valid
        if (!ALLOWED_ROLES.contains(role)){
            throw new IllegalArgumentException("Role is not valid");
        }

        //Check if the user exists
        UserInfo user = userRepository.findById(username).orElseThrow(() -> new NoSuchElementException("User not found"));

        // Check if the user has more than 1 role
        List<String> roles = getUserRoles(username);
        if (roles.size() == 1) {
            throw new IllegalArgumentException("Can't remove role, users has only 1 role");
        }

        // Save the Authority to the database
        authorityRepository.deleteUserRole(username, "ROLE_" + role);

        // If role is WORKER,remove the user from the issue observers
        if ("WORKER".equals(role)) {
            issueTracker.removeObserver(user);
        }

    }
}

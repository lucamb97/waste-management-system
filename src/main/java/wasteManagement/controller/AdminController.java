package wasteManagement.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import wasteManagement.model.entities.User;
import wasteManagement.model.utils.RegisterRequest;
import wasteManagement.services.AdminService;
import wasteManagement.services.AuthService;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private AuthService authService;

    //This is like register but you can add any role
    @PostMapping("/registerAnyRole")
    public ResponseEntity<String> registerWorker(@RequestBody RegisterRequest user) {
        try {
            authService.register(user, user.getRole());
            return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
        } catch (AuthenticationException e) {
            log.error("User already exists");
            return new ResponseEntity<>("User already exists", HttpStatus.FORBIDDEN);
        } catch (IllegalArgumentException e) {
            log.error("Error during registration, invalid role");
            return new ResponseEntity<>("Role is not valid", HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            log.error("Error during admin registration");
            return new ResponseEntity<>("Error during registration", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Used to get info of all users
    @GetMapping("/getAllUsersInfo")
    public ResponseEntity<List<User>> getALlUsers() {
        try {
            List<User> users = adminService.getAllUsers();
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            log.error("Could not find any users");
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error trying to get list of users");
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getUserRoles")
    public ResponseEntity<List<String>> getUserRoles (@RequestParam String username) {
        try {
            List <String>roles = adminService.getUserRoles(username);
            return new ResponseEntity<>(roles, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            log.error("Could not find any users");
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error trying to find roles: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Used to add a new role to a user
    @PutMapping("/addRole")
    public ResponseEntity<String> addRole (@RequestParam String username, String role) {
        try {
            // Add role to user
            adminService.addRoleToUser(username, role);
            return new ResponseEntity<>("Role added to user", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("Invalid role provided");
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NoSuchElementException e) {
            log.error("Could not find user");
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error trying to add new role: {}", e.getMessage());
            return new ResponseEntity<>("Error trying to add new role", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Used to remove role from a user, must have more than 1 role
    @DeleteMapping("/removeRole")
    public ResponseEntity<String> removeRole(@RequestParam String username, String role) {
        try {
            // Add role to user
            adminService.removeRoleFromUser(username, role);
            return new ResponseEntity<>("Role removed from user", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("Invalid role provided");
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NoSuchElementException e) {
            log.error("Could not find user");
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error trying to delete role: {}", e.getMessage());
            return new ResponseEntity<>("Error trying to delete role", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Used to delete a user
    @DeleteMapping("/deleteUser")
    public ResponseEntity<String> deleteUser (@RequestParam String username) {
        try {
            adminService.deleteUser(username);
            return new ResponseEntity<>("User deleted", HttpStatus.OK);
        } catch (NoSuchElementException e) {
            log.error("Could not find any users");
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error trying to delete user: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

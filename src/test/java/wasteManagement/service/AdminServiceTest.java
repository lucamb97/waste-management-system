package wasteManagement.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import wasteManagement.model.entities.Authority;
import wasteManagement.model.entities.UserInfo;
import wasteManagement.model.repositorys.AuthorityRepository;
import wasteManagement.model.repositorys.UserRepository;
import wasteManagement.services.AdminService;
import wasteManagement.services.IssueTracker;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    @Mock
    private AuthorityRepository authorityRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JdbcUserDetailsManager jdbcUserDetailsManager;
    @Mock
    private IssueTracker issueTracker;
    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    //Testing of getAllUsers() method
    @Test
    void testGetAllUsers_Success() {
        //Non-empty list of users
        UserInfo mockUser1 = new UserInfo();
        mockUser1.setUsername("user1");

        UserInfo mockUser2 = new UserInfo();
        mockUser2.setUsername("user2");

        List<UserInfo> mockUsers = Arrays.asList(mockUser1, mockUser2);
        when(userRepository.findAll()).thenReturn(mockUsers);

        List<UserInfo> result = adminService.getAllUsers();

        //Assertions and internal verifications
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetAllUsers_EmptyList() {
        //Empty list of users
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        //NoSuchElementException thrown when list is empty
        assertThrows(NoSuchElementException.class, () -> adminService.getAllUsers());
        verify(userRepository, times(1)).findAll();
    }

    //testing of deleteUser() method
    @Test
    void testDeleteUser() {
        // Ensure both authority and user deletion is handled correctly
        doNothing().when(authorityRepository).deleteUser(anyString());
        doNothing().when(userRepository).deleteUser(anyString());

        adminService.deleteUser("user1");

        //Verifications
        verify(authorityRepository, times(1)).deleteUser("user1");
        verify(userRepository, times(1)).deleteUser("user1");
    }

    //Testing of addRoleToUser() method
    @Test
    void testAddRoleToUser_ValidRole() {
        //Set up the mock user with a list of roles
        UserInfo mockUser = new UserInfo();
        mockUser.setUsername("user1");
        mockUser.setCity("TestCity");

        //Assume that this user already has a "ROLE_USER"
        when(userRepository.findById("user1")).thenReturn(java.util.Optional.of(mockUser));
        when(authorityRepository.findRolesByUsername("user1")).thenReturn(Arrays.asList("ROLE_USER"));

        //Add a valid role
        adminService.addRoleToUser("user1", "ADMIN");

        //Verify the internal logic
        verify(authorityRepository, times(1)).save(any(Authority.class));
        verify(issueTracker, never()).addObserver(any(UserInfo.class));
    }

    @Test
    void testAddRoleToUser_InvalidRole() {
        //Try adding an invalid role and check for the exception
        assertThrows(IllegalArgumentException.class, () -> adminService.addRoleToUser("user1", "INVALID_ROLE"));
        verify(authorityRepository, never()).save(any(Authority.class));
    }

    @Test
    void testAddRoleToUser_WorkerRole() {
        //Set up the mock user with a list of roles
        UserInfo mockUser = new UserInfo();
        mockUser.setUsername("user1");
        mockUser.setCity("TestCity");

        //Assume that this user already has a "ROLE_USER"
        when(userRepository.findById("user1")).thenReturn(java.util.Optional.of(mockUser));
        when(authorityRepository.findRolesByUsername("user1")).thenReturn(Arrays.asList("ROLE_USER"));

        //Adding the WORKER role
        adminService.addRoleToUser("user1", "WORKER");

        //Verify the role was added
        verify(authorityRepository, times(1)).save(any(Authority.class));

        //Verify that the user was added as an observer
        verify(issueTracker, times(1)).addObserver(mockUser);
    }

    //testing of removeRoleFromUser() method
    @Test
    void testRemoveRoleFromUser_ValidRole() {
        //Set up the mocks
        UserInfo mockUser = new UserInfo();
        mockUser.setUsername("user1");

        when(userRepository.findById("user1")).thenReturn(java.util.Optional.of(mockUser));
        when(authorityRepository.findRolesByUsername("user1")).thenReturn(Arrays.asList("ROLE_USER", "ROLE_ADMIN"));

        //removing a valid role
        adminService.removeRoleFromUser("user1", "ADMIN");

        verify(authorityRepository, times(1)).deleteUserRole("user1", "ROLE_ADMIN");
        verify(issueTracker, never()).removeObserver(mockUser);
    }

    @Test
    void testRemoveRoleFromUser_WorkerRole() {
        //Set up the mocks
        UserInfo mockUser = new UserInfo();
        mockUser.setUsername("user1");

        when(userRepository.findById("user1")).thenReturn(java.util.Optional.of(mockUser));
        when(authorityRepository.findRolesByUsername("user1")).thenReturn(Arrays.asList("ROLE_USER", "ROLE_WORKER"));

        //Removing the WORKER role
        adminService.removeRoleFromUser("user1", "WORKER");

        verify(authorityRepository, times(1)).deleteUserRole("user1", "ROLE_WORKER");

        //Verify that the user was removed as an observer
        verify(issueTracker, times(1)).removeObserver(mockUser);
    }

    @Test
    void testRemoveRoleFromUser_InvalidRole() {
        //Attempt to remove an invalid role and expect an exception
        assertThrows(IllegalArgumentException.class, () -> adminService.removeRoleFromUser("user1", "INVALID_ROLE"));
        verify(authorityRepository, never()).deleteUserRole(anyString(), anyString());
    }

    @Test
    void testRemoveRoleFromUser_SingleRole() {
        //Set up the mocks
        UserInfo mockUser = new UserInfo();
        mockUser.setUsername("user1");

        when(userRepository.findById("user1")).thenReturn(java.util.Optional.of(mockUser));
        when(authorityRepository.findRolesByUsername("user1")).thenReturn(Arrays.asList("ROLE_USER"));

        //Try to remove the only role, which should throw an exception
        assertThrows(IllegalArgumentException.class, () -> adminService.removeRoleFromUser("user1", "USER"));
        verify(authorityRepository, never()).deleteUserRole(anyString(), anyString());
    }
}


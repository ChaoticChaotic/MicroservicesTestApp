package org.microservices.user.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.microservices.AppPostgresqlContainer;
import org.microservices.UserAppTest;
import org.microservices.user.model.Role;
import org.microservices.user.model.User;
import org.microservices.user.repository.RoleRepository;
import org.microservices.user.repository.UserRepository;
import org.microservices.user.security.exceptions.NotFoundException;
import org.microservices.user.security.exceptions.UserServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class UserServiceImplTest extends UserAppTest {

    @Container
    public static PostgreSQLContainer<AppPostgresqlContainer> postgreSQLContainer =
            AppPostgresqlContainer.getInstance();

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        Role roleAdmin = Role.builder()
                .name("ROLE_ADMIN")
                .build();
        roleRepository.save(roleAdmin);

        testUser = User.builder()
                .username("testuser")
                .password("testpassword")
                .roles(List.of(roleAdmin))
                .build();
    }

    @Test
    void save() {
        User savedUser = userService.save(testUser);
        assertNotNull(savedUser.getId());
        assertEquals(testUser.getUsername(), savedUser.getUsername());
    }

    @Test
    void saveUserAlreadyExistsThenThrowException() {
        userService.save(testUser);

        assertThrows(UserServiceException.class, () -> userService.save(testUser));
    }

    @Test
    void update() {
        User savedUser = userService.save(testUser);
        savedUser.setEmail("updatedemail@example.com");

        User updatedUser = userService.update(savedUser.getId(), savedUser);
        assertEquals(savedUser.getEmail(), updatedUser.getEmail());
    }

    @Test
    void updateUserThatNotFoundThenThrowException() {
        assertThrows(NotFoundException.class, () -> userService.update(-1L, testUser));
    }

    @Test
    void getById() {
        User savedUser = userService.save(testUser);

        User retrievedUser = userService.getById(savedUser.getId());
        assertEquals(savedUser.getId(), retrievedUser.getId());
    }

    @Test
    void getByIdUserNotFoundThenThrowException() {
        assertThrows(NotFoundException.class, () -> userService.getById(-1L));
    }

    @Test
    void findByUsername() {
        userService.save(testUser);

        User foundUser = userService.findByUsername(testUser.getUsername());
        assertEquals(testUser.getUsername(), foundUser.getUsername());
    }

    @Test
    void findByUsernameUserNotFoundThenThrowException() {
        assertThrows(UserServiceException.class, () -> userService.findByUsername("nonexistentuser"));
    }

    @Test
    void getAll() {
        userService.save(testUser);

        List<User> userList = userService.getAll();
        assertThat(userList.stream().map(User::getUsername).collect(Collectors.toList()))
                .contains(testUser.getUsername());
    }

    @Test
    void deleteById() {
        User savedUser = userService.save(testUser);

        userService.deleteById(savedUser.getId());

        assertFalse(userRepository.findById(savedUser.getId()).isPresent());
    }

    @Test
    void deleteByIdUserNotFoundThenThrowException() {
        assertThrows(NotFoundException.class, () -> userService.deleteById(-1L));
    }

    @Test
    void addOrderId() {
        User savedUser = userService.save(testUser);
        String orderId = "order123";

        userService.addOrderId(savedUser.getId(), orderId);

        User updatedUser = userRepository.findById(savedUser.getId()).orElse(null);
        assertNotNull(updatedUser);
        assertThat(updatedUser.getOrderIds()).contains(orderId);
    }
}
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
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


class UserServiceImplTest extends UserAppTest {

    @Container
    public static PostgreSQLContainer<AppPostgresqlContainer> postgreSQLContainer =
            AppPostgresqlContainer.getInstance();

    @Autowired
    private UserServiceImpl underTest;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

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
                .orderIds(new ArrayList<>())
                .build();
    }

    @Test
    void save() {
        User savedUser = underTest.save(testUser);
        assertNotNull(savedUser.getId());
        assertEquals(testUser.getUsername(), savedUser.getUsername());
    }

    @Test
    void saveUserAlreadyExistsThenThrowException() {
        underTest.save(testUser);

        assertThrows(UserServiceException.class, () -> underTest.save(testUser));
    }

    @Test
    void update() {
        User savedUser = underTest.save(testUser);
        savedUser.setEmail("updatedemail@example.com");

        User updatedUser = underTest.update(savedUser.getId(), savedUser);
        assertEquals(savedUser.getEmail(), updatedUser.getEmail());
    }

    @Test
    void updateUserThatNotFoundThenThrowException() {
        assertThrows(NotFoundException.class, () -> underTest.update(-1L, testUser));
    }

    @Test
    void getById() {
        User savedUser = underTest.save(testUser);

        User retrievedUser = underTest.getById(savedUser.getId());
        assertEquals(savedUser.getId(), retrievedUser.getId());
    }

    @Test
    void getByIdUserNotFoundThenThrowException() {
        assertThrows(NotFoundException.class, () -> underTest.getById(-1L));
    }

    @Test
    void findByUsername() {
        underTest.save(testUser);

        User foundUser = underTest.findByUsername(testUser.getUsername());
        assertEquals(testUser.getUsername(), foundUser.getUsername());
    }

    @Test
    void findByUsernameUserNotFoundThenThrowException() {
        assertThrows(UserServiceException.class, () -> underTest.findByUsername("nonexistentuser"));
    }

    @Test
    void getAll() {
        underTest.save(testUser);

        List<User> userList = underTest.getAll();
        assertThat(userList.stream().map(User::getUsername).collect(Collectors.toList()))
                .contains(testUser.getUsername());
    }

    @Test
    void deleteById() {
        User savedUser = underTest.save(testUser);

        underTest.deleteById(savedUser.getId());

        assertFalse(userRepository.findById(savedUser.getId()).isPresent());
    }

    @Test
    void deleteByIdUserNotFoundThenThrowException() {
        assertThrows(NotFoundException.class, () -> underTest.deleteById(-1L));
    }

    @Test
    void addOrderId() {
        User savedUser = underTest.save(testUser);
        String orderId = "order123";

        underTest.addOrderId(savedUser.getId(), orderId);

        User updatedUser = userRepository.findById(savedUser.getId()).orElse(null);
        assertNotNull(updatedUser);
        assertThat(updatedUser.getOrderIds()).contains(orderId);
    }
}
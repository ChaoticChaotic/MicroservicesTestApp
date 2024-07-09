package org.microservices.user.service.user;


import org.microservices.user.model.Role;
import org.microservices.user.model.User;
import org.microservices.user.repository.RoleRepository;
import org.microservices.user.repository.UserRepository;
import org.microservices.user.security.DTO.ChangePassRequest;
import org.microservices.user.security.exceptions.BadRequestException;
import org.microservices.user.security.exceptions.NotFoundException;
import org.microservices.user.security.exceptions.UserServiceException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.*;


@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    private void init() {
        Role roleAdmin = Role.builder()
                .name("ROLE_ADMIN")
                .build();
        if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
            saveRole(roleAdmin);
        }
        Role roleUser = Role.builder()
                .name("ROLE_USER")
                .build();
        if (roleRepository.findByName("ROLE_USER").isEmpty()) {
            saveRole(roleUser);
        }
        if (userRepository.findByUsername("developer").isEmpty()) {
            Role role = roleRepository.findByName("ROLE_ADMIN").orElseThrow(() -> new NotFoundException("Role Not Found!"));
            User developer = User
                    .builder()
                    .username("developer")
                    .password("developer")
                    .roles(List.of(role))
                    .build();
            save(developer);
        }
    }

    @Override
    public User save(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UserServiceException("User already exists!");
        }
//        if (!Pattern.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,20}$", user.getPassword())) {
//            //Here we are can regulate pass complexity
//        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User update(Long id, User user) {
        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User Not Found!"));
        userToUpdate.setUsername(user.getUsername());
        userToUpdate.setEmail(user.getEmail());
        return userRepository.save(user);
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User Not Found!"));
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserServiceException("User Not Found!"));
    }

    @Override
    public List<User> getAll() throws AccessDeniedException {
        return userRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User Not Found!"));
        userRepository.delete(user);
    }

    @Override
    public User changePassword(Long id, @Valid ChangePassRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User Not Found!"));
        if (Objects.isNull(request.getOldPassword())
                || Objects.isNull(request.getNewPassword())
                || Objects.isNull(request.getNewPasswordConfirmation())) {
            throw new BadRequestException("Old and New Password Are Required!");
        }
        if (!BCrypt.checkpw(request.getOldPassword(), user.getPassword())) {
            throw new UserServiceException("Old Password Incorrect!");
        }
        if (!request.getNewPassword().equals(request.getNewPasswordConfirmation())) {
            throw new UserServiceException("New Password Incorrect!");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        return userRepository.save(user);
    }

    @Override
    public boolean existsById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.isPresent();
    }

    @Override
    public void saveRole(Role role) {
        roleRepository.save(role);
    }

    @Override
    public void addRoleToUser(Long orderId, String roleName) {
        User user = userRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("User Not Found!"));
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new NotFoundException("Role Not Found!"));
        user.getRoles().add(role);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserServiceException("User Not Found!"));
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }
}
